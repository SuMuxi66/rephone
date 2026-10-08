package com.rephone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
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
 * 评价系统验收：三类订单（回收60/出售40/维修50）完成后可评一次；
 * 未完成/非本人订单/重复评价/非法参数全部拒绝；管理端分页筛选；COS 目录白名单。
 * 订单用 JdbcTemplate 直插（评价链路不依赖下单流程），金额断言单位为分。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:cmt;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/schema-h2.sql,classpath:db/schema-comment.sql,"
                + "classpath:db/schema-recycle.sql,classpath:db/schema-sale.sql,classpath:db/schema-repair.sql",
        "rephone.wx.mock-login=true",
        "rephone.jwt.ttl-seconds=3600",
        "rephone.admin.token=test-admin-token",
        "rephone.cos.mock=true"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CommentFlowTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String token;
    private long userId;

    @BeforeAll
    void login() throws Exception {
        ResponseEntity<String> resp = rest.postForEntity("/api/wx/login",
                Map.of("code", "cmt-code"), String.class);
        JsonNode data = objectMapper.readTree(resp.getBody()).path("data");
        token = data.path("token").asText();
        assertNotEquals("", token);
        userId = data.path("userId").asLong();
        assertTrue(userId > 0);

        // 直插测试订单：回收（已完成60/质检中30/他人所有）、出售（已完成40/待发货20）、维修（已完成50）
        jdbcTemplate.update("insert into recycle_order (tenant_id, order_no, user_id, openid, model_id, "
                        + "brand_name, model_name, storage, condition_key, condition_label, quote_fen, status) "
                        + "values (0, 'R-CMT-DONE', ?, 'mock-cmt', 1, 'Apple', 'iPhone 15', '128GB', 'COND_95', '95新', 566000, 60)",
                userId);
        jdbcTemplate.update("insert into recycle_order (tenant_id, order_no, user_id, openid, model_id, "
                        + "brand_name, model_name, storage, condition_key, condition_label, quote_fen, status) "
                        + "values (0, 'R-CMT-WIP', ?, 'mock-cmt', 1, 'Apple', 'iPhone 15', '128GB', 'COND_95', '95新', 566000, 30)",
                userId);
        jdbcTemplate.update("insert into recycle_order (tenant_id, order_no, user_id, openid, model_id, "
                        + "brand_name, model_name, storage, condition_key, condition_label, quote_fen, status) "
                        + "values (0, 'R-CMT-OTHER', 999999, 'mock-other', 1, 'Apple', 'iPhone 15', '128GB', 'COND_95', '95新', 566000, 60)");
        jdbcTemplate.update("insert into sale_order (tenant_id, order_no, user_id, openid, goods_id, goods_name, "
                        + "price_fen, quantity, total_fen, status, receiver_name, receiver_phone, receiver_addr) "
                        + "values (0, 'S-CMT-DONE', ?, 'mock-cmt', 1, 'iPhone 14 95新', 299900, 1, 299900, 40, '张三', '13800000001', '甘肃省兰州市城关区XX路1号')",
                userId);
        jdbcTemplate.update("insert into sale_order (tenant_id, order_no, user_id, openid, goods_id, goods_name, "
                        + "price_fen, quantity, total_fen, status, receiver_name, receiver_phone, receiver_addr) "
                        + "values (0, 'S-CMT-WIP', ?, 'mock-cmt', 1, 'iPhone 14 95新', 299900, 1, 299900, 20, '张三', '13800000001', '甘肃省兰州市城关区XX路1号')",
                userId);
        jdbcTemplate.update("insert into repair_order (tenant_id, order_no, user_id, openid, model_id, "
                        + "brand_name, model_name, items_json, total_fen, status, service_type, contact_name, "
                        + "contact_phone, address, appoint_time) "
                        + "values (0, 'F-CMT-DONE', ?, 'mock-cmt', 1, 'Apple', 'iPhone 15', "
                        + "'[{\"itemId\":3,\"name\":\"更换电池\",\"priceFen\":14900}]', 14900, 50, 10, '张三', '13800000001', "
                        + "'甘肃省兰州市城关区XX路1号', '今天 上午')",
                userId);
    }

    private HttpHeaders auth() {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        return h;
    }

    private HttpHeaders admin() {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth("test-admin-token");
        return h;
    }

    private ResponseEntity<String> createComment(String orderNo, int orderType, int score,
                                                 String content, List<String> images) {
        Map<String, Object> payload = Map.of(
                "orderNo", orderNo, "orderType", orderType, "score", score,
                "content", content, "images", images);
        return rest.exchange("/api/wx/comment", HttpMethod.POST, new HttpEntity<>(payload, auth()), String.class);
    }

    @Test
    @Order(1)
    void invalid_inputs_are_rejected() throws Exception {
        // 订单类型不在 10/20/30
        assertNotEquals(0, objectMapper.readTree(createComment("R-CMT-DONE", 99, 5, "好", List.of()).getBody()).path("code").asInt());
        // 星级越界
        assertNotEquals(0, objectMapper.readTree(createComment("R-CMT-DONE", 10, 0, "好", List.of()).getBody()).path("code").asInt());
        assertNotEquals(0, objectMapper.readTree(createComment("R-CMT-DONE", 10, 6, "好", List.of()).getBody()).path("code").asInt());
        // 内容超长（501 字）
        assertNotEquals(0, objectMapper.readTree(createComment("R-CMT-DONE", 10, 5, "好".repeat(501), List.of()).getBody()).path("code").asInt());
        // 图片超 9 张
        assertNotEquals(0, objectMapper.readTree(createComment("R-CMT-DONE", 10, 5, "好",
                List.of("a/1.jpg", "a/2.jpg", "a/3.jpg", "a/4.jpg", "a/5.jpg", "a/6.jpg", "a/7.jpg", "a/8.jpg", "a/9.jpg", "a/10.jpg")).getBody()).path("code").asInt());
    }

    @Test
    @Order(2)
    void completed_recycle_order_can_comment_once() throws Exception {
        ResponseEntity<String> resp = createComment("R-CMT-DONE", 10, 5, "质检快，打款及时",
                List.of("comment/2026-10-08/abc.jpg"));
        assertEquals(0, objectMapper.readTree(resp.getBody()).get("code").asInt(), resp.getBody());

        // 重复评价被拒
        assertNotEquals(0, objectMapper.readTree(createComment("R-CMT-DONE", 10, 4, "再来一条", List.of()).getBody()).path("code").asInt());

        // 按单查询：图片 JSON 已解析为数组
        JsonNode data = objectMapper.readTree(rest.exchange(
                "/api/wx/comment/order/R-CMT-DONE?orderType=10", HttpMethod.GET,
                new HttpEntity<>(auth()), String.class).getBody()).path("data");
        assertEquals(5, data.path("score").asInt());
        assertEquals("质检快，打款及时", data.path("content").asText());
        assertEquals(1, data.path("images").size());
    }

    @Test
    @Order(3)
    void incomplete_or_foreign_orders_are_rejected() throws Exception {
        // 未完成（回收还在质检中）
        assertNotEquals(0, objectMapper.readTree(createComment("R-CMT-WIP", 10, 5, "好", List.of()).getBody()).path("code").asInt());
        // 他人的订单
        assertNotEquals(0, objectMapper.readTree(createComment("R-CMT-OTHER", 10, 5, "好", List.of()).getBody()).path("code").asInt());
        // 订单不存在
        assertNotEquals(0, objectMapper.readTree(createComment("R-CMT-NOPE", 10, 5, "好", List.of()).getBody()).path("code").asInt());
    }

    @Test
    @Order(4)
    void sale_and_repair_completed_orders_can_comment() throws Exception {
        assertEquals(0, objectMapper.readTree(createComment("S-CMT-DONE", 20, 4, "机器和描述一致",
                List.of()).getBody()).get("code").asInt());
        // 未完成的出售单不可评
        assertNotEquals(0, objectMapper.readTree(createComment("S-CMT-WIP", 20, 5, "好", List.of()).getBody()).path("code").asInt());

        assertEquals(0, objectMapper.readTree(createComment("F-CMT-DONE", 30, 5, "维修很快，质保180天",
                List.of()).getBody()).get("code").asInt());

        JsonNode repair = objectMapper.readTree(rest.exchange(
                "/api/wx/comment/order/F-CMT-DONE?orderType=30", HttpMethod.GET,
                new HttpEntity<>(auth()), String.class).getBody()).path("data");
        assertEquals(30, repair.path("orderType").asInt());
    }

    @Test
    @Order(5)
    void admin_list_with_filters_and_auth() throws Exception {
        // 未带凭证 401
        assertEquals(401, rest.getForEntity("/api/admin/comment/list", String.class).getStatusCode().value());

        ResponseEntity<String> resp = rest.exchange(
                "/api/admin/comment/list?pageNum=1&pageSize=10", HttpMethod.GET,
                new HttpEntity<>(admin()), String.class);
        JsonNode page = objectMapper.readTree(resp.getBody()).path("data");
        assertEquals(0, objectMapper.readTree(resp.getBody()).get("code").asInt());
        assertTrue(page.path("total").asLong() >= 3, "应至少有 3 条评价");

        // 类型 + 星级筛选：回收 5 星应只剩 1 条
        ResponseEntity<String> filtered = rest.exchange(
                "/api/admin/comment/list?pageNum=1&pageSize=10&orderType=10&score=5", HttpMethod.GET,
                new HttpEntity<>(admin()), String.class);
        JsonNode filteredPage = objectMapper.readTree(filtered.getBody()).path("data");
        assertEquals(1, filteredPage.path("records").size());
        assertEquals("R-CMT-DONE", filteredPage.path("records").get(0).path("orderNo").asText());

        // 单号模糊筛选
        ResponseEntity<String> byNo = rest.exchange(
                "/api/admin/comment/list?pageNum=1&pageSize=10&orderNo=F-CMT", HttpMethod.GET,
                new HttpEntity<>(admin()), String.class);
        assertEquals(1, objectMapper.readTree(byNo.getBody()).path("data").path("records").size());
    }

    @Test
    @Order(6)
    void cos_sign_respects_dir_whitelist() throws Exception {
        JsonNode commentSign = objectMapper.readTree(rest.exchange(
                "/api/wx/cos/upload-sign?ext=jpg&dir=comment", HttpMethod.GET,
                new HttpEntity<>(auth()), String.class).getBody()).path("data");
        assertTrue(commentSign.path("key").asText().startsWith("comment/"), "评价图 key 应在 comment/ 目录");

        JsonNode defaultSign = objectMapper.readTree(rest.exchange(
                "/api/wx/cos/upload-sign?ext=jpg", HttpMethod.GET,
                new HttpEntity<>(auth()), String.class).getBody()).path("data");
        assertTrue(defaultSign.path("key").asText().startsWith("recycle/"), "默认目录保持 recycle/ 不变");

        JsonNode evilSign = objectMapper.readTree(rest.exchange(
                "/api/wx/cos/upload-sign?ext=jpg&dir=../evil", HttpMethod.GET,
                new HttpEntity<>(auth()), String.class).getBody()).path("data");
        assertTrue(evilSign.path("key").asText().startsWith("recycle/"), "非法目录应回退默认");
    }
}
