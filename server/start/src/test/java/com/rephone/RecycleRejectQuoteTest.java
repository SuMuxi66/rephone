package com.rephone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

/**
 * 回收「拒绝最终价」验收：40→30 退回重新质检（机器留平台侧，无退回物流）、拒绝不改 final_fen、
 * 管理端重新质检以新最终价进 40、次数上限转人工（40044）、非 40 态拒绝被拒、非本人 40302、
 * 40 态整单取消仍可用。独立 H2 库，不依赖共享库残留与本机 .env。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:rrjq;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/schema-h2.sql,classpath:db/schema-recycle.sql",
        "rephone.wx.mock-login=true",
        "rephone.jwt.ttl-seconds=3600",
        "rephone.admin.token=test-admin-token",
        "rephone.cos.mock=true",
        "rephone.express.mock=true"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RecycleRejectQuoteTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String userToken;
    private long userId;

    @BeforeAll
    void loginAndSeed() throws Exception {
        ResponseEntity<String> resp = rest.postForEntity("/api/wx/login",
                Map.of("code", "rrjq-code"), String.class);
        JsonNode data = objectMapper.readTree(resp.getBody()).path("data");
        userToken = data.path("token").asText();
        assertNotEquals("", userToken);
        userId = data.path("userId").asLong();

        // 主链单（40 待确认）+ 取消单（40）+ 非法状态单（30 质检中）+ 他人单（租户 5，40）
        insertOrder("R-RJQ-A", userId, 0, 40, 566000L);
        insertOrder("R-RJQ-B", userId, 0, 40, 566000L);
        insertOrder("R-RJQ-C", userId, 0, 30, 566000L);
        insertOrder("R-RJQ-FOREIGN", 999999, 5, 40, 566000L);
    }

    private void insertOrder(String orderNo, long orderUserId, int tenantId, int status, long finalFen) {
        jdbcTemplate.update("insert into recycle_order (tenant_id, order_no, user_id, openid, model_id, "
                        + "brand_name, model_name, storage, condition_key, condition_label, quote_fen, final_fen, status) "
                        + "values (?, ?, ?, 'seed-rrjq', 1, 'Apple', 'iPhone 15', '128GB', 'COND_95', '95新', 566000, ?, ?)",
                tenantId, orderNo, orderUserId, finalFen, status);
    }

    private HttpHeaders user() {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(userToken);
        return h;
    }

    private HttpHeaders machine() {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth("test-admin-token");
        return h;
    }

    private ResponseEntity<String> reject(String orderNo) {
        return rest.exchange("/api/wx/recycle/order/" + orderNo + "/reject-price", HttpMethod.PUT,
                new HttpEntity<>(user()), String.class);
    }

    private int statusOf(String orderNo) {
        Integer status = jdbcTemplate.queryForObject(
                "select status from recycle_order where order_no = ?", Integer.class, orderNo);
        return status == null ? -1 : status;
    }

    private long finalFenOf(String orderNo) {
        Long fen = jdbcTemplate.queryForObject(
                "select final_fen from recycle_order where order_no = ?", Long.class, orderNo);
        return fen == null ? -1 : fen;
    }

    private int rejectLogCount(String orderNo) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from order_status_log where order_no = ? and from_status = 40 "
                        + "and to_status = 30 and operator_type = 10", Integer.class, orderNo);
        return count == null ? 0 : count;
    }

    private void adminInspection(String orderNo, long finalFen) throws Exception {
        ResponseEntity<String> resp = rest.exchange("/api/admin/recycle/order/" + orderNo + "/inspection",
                HttpMethod.POST, new HttpEntity<>(Map.of("result", "复检结论", "finalFen", finalFen), machine()),
                String.class);
        assertEquals(0, objectMapper.readTree(resp.getBody()).get("code").asInt(), resp.getBody());
    }

    @Test
    @Order(1)
    void reject_returns_to_inspecting_then_reinspect_and_second_reject() throws Exception {
        // 第一轮拒绝：40→30，final_fen 不变，日志落 40→30/用户操作
        ResponseEntity<String> first = reject("R-RJQ-A");
        assertEquals(0, objectMapper.readTree(first.getBody()).get("code").asInt(), first.getBody());
        assertEquals(30, statusOf("R-RJQ-A"), "拒绝后应退回质检中");
        assertEquals(566000L, finalFenOf("R-RJQ-A"), "拒绝不得修改最终价");
        assertEquals(1, rejectLogCount("R-RJQ-A"), "应有一条 40→30 的用户操作日志");

        // 拒绝后立刻重复请求：状态已是 30，非法流转必须被拒且状态不变
        ResponseEntity<String> repeat = reject("R-RJQ-A");
        assertNotEquals(0, objectMapper.readTree(repeat.getBody()).get("code").asInt(), "重复拒绝应被拒绝");
        assertEquals(30, statusOf("R-RJQ-A"));

        // 管理端重新质检：30→40，新最终价生效
        adminInspection("R-RJQ-A", 400000L);
        assertEquals(40, statusOf("R-RJQ-A"), "重新质检后应进入待确认");
        assertEquals(400000L, finalFenOf("R-RJQ-A"), "新最终价应以再次质检为准");

        // 第二轮拒绝仍允许（上限 2 次）
        ResponseEntity<String> second = reject("R-RJQ-A");
        assertEquals(0, objectMapper.readTree(second.getBody()).get("code").asInt(), second.getBody());
        assertEquals(30, statusOf("R-RJQ-A"));
        assertEquals(2, rejectLogCount("R-RJQ-A"));

        // 第三轮拒绝：超上限转人工，状态与价格原状
        adminInspection("R-RJQ-A", 480000L);
        ResponseEntity<String> third = reject("R-RJQ-A");
        assertEquals(40044, objectMapper.readTree(third.getBody()).get("code").asInt(), "超上限应转人工");
        assertEquals(40, statusOf("R-RJQ-A"), "被拒的拒绝不得改变状态");
        assertEquals(480000L, finalFenOf("R-RJQ-A"));
    }

    @Test
    @Order(2)
    void cancel_still_works_from_wait_confirm() throws Exception {
        // 新出边不得影响既有 40→80 取消出路
        ResponseEntity<String> resp = rest.exchange("/api/wx/recycle/order/R-RJQ-B/cancel", HttpMethod.PUT,
                new HttpEntity<>(Map.of("reason", "不卖了"), user()), String.class);
        assertEquals(0, objectMapper.readTree(resp.getBody()).get("code").asInt(), resp.getBody());
        assertEquals(80, statusOf("R-RJQ-B"), "40 待确认仍可整单取消");
    }

    @Test
    @Order(3)
    void reject_rejected_in_non_wait_confirm_status() throws Exception {
        ResponseEntity<String> resp = reject("R-RJQ-C");
        assertNotEquals(0, objectMapper.readTree(resp.getBody()).get("code").asInt(), "非 40 态拒绝应被拒");
        assertEquals(30, statusOf("R-RJQ-C"), "状态不得变化");
    }

    @Test
    @Order(4)
    void foreign_order_reject_forbidden() throws Exception {
        ResponseEntity<String> resp = reject("R-RJQ-FOREIGN");
        assertNotEquals(0, objectMapper.readTree(resp.getBody()).get("code").asInt(), "他人/跨租户订单不可拒绝");
        assertEquals(40, statusOf("R-RJQ-FOREIGN"), "越权请求不得改变状态");
    }

    @Test
    @Order(5)
    void anonymous_reject_unauthorized() {
        ResponseEntity<String> resp = rest.exchange("/api/wx/recycle/order/R-RJQ-A/reject-price",
                HttpMethod.PUT, HttpEntity.EMPTY, String.class);
        assertEquals(401, resp.getStatusCode().value(), "未登录应 401");
    }
}
