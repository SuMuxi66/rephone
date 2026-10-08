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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;

/**
 * 维修报价模式验收：检测→报价→用户确认→支付→维修。
 * 覆盖：正常报价链（含支付落 pay_no）、拒绝后退回可重报、未登录 401、跨租户隔离、
 * 重复报价拒绝、非法状态流转拒绝、跨用户越权。
 * 订单用 JdbcTemplate 直插；独立 H2 库，不污染其他测试类。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:rquote;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/schema-h2.sql,classpath:db/schema-role.sql,"
                + "classpath:db/schema-repair.sql",
        "rephone.wx.mock-login=true",
        "rephone.jwt.ttl-seconds=3600",
        "rephone.admin.token=test-admin-token"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RepairQuoteFlowTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private String userToken;
    private long userId;
    private String tenantAdminToken;

    @BeforeAll
    void loginAndSeed() throws Exception {
        ResponseEntity<String> resp = rest.postForEntity("/api/wx/login",
                Map.of("code", "rq-code"), String.class);
        JsonNode data = objectMapper.readTree(resp.getBody()).path("data");
        userToken = data.path("token").asText();
        assertNotEquals("", userToken);
        userId = data.path("userId").asLong();

        // 直插测试订单：报价链主单（上门/待确认）、拒绝链主单、寄修收件单、他人单、租户5 单
        insertOrder(0, "F-RQ-ONSITE", userId, 10, 10);
        insertOrder(0, "F-RQ-REJECT", userId, 10, 10);
        insertOrder(0, "F-RQ-MAIL", userId, 25, 20);
        insertOrder(0, "F-RQ-FOREIGN", 999999, 35, 10);
        insertOrder(5, "F-RQ-TENANT5", 888888, 35, 10);

        // 租户 5 + 租户管理员（repair:manage），用于跨租户隔离断言
        jdbcTemplate.update("merge into tenant (id, name, status) key (id) values (5, '报价测试租户', 1)");
        jdbcTemplate.update("merge into role (role_code, role_name, permissions_json, status) key (role_code) "
                + "values ('TENANT_ADMIN', '租户管理员', '[\"repair:manage\"]', 1)");
        jdbcTemplate.update("delete from `user` where username = 'rq_tadmin'");
        jdbcTemplate.update("insert into `user` (tenant_id, openid, nickname, status, username, password_hash, role) "
                        + "values (5, 'rq-tadmin-openid', '报价租户管理员', 1, 'rq_tadmin', ?, 'TENANT_ADMIN')",
                encoder.encode("password123"));
        ResponseEntity<String> loginResp = rest.postForEntity("/api/admin/auth/login",
                Map.of("username", "rq_tadmin", "password", "password123"), String.class);
        tenantAdminToken = objectMapper.readTree(loginResp.getBody()).path("data").path("token").asText();
        assertNotEquals("", tenantAdminToken);
    }

    private void insertOrder(int tenantId, String orderNo, long orderUserId, int status, int serviceType) {
        jdbcTemplate.update("insert into repair_order (tenant_id, order_no, user_id, openid, model_id, "
                        + "brand_name, model_name, items_json, total_fen, status, service_type, contact_name, "
                        + "contact_phone, address, appoint_time) "
                        + "values (?, ?, ?, 'seed', 1, 'Apple', 'iPhone 15', "
                        + "'[{\"itemId\":3,\"name\":\"更换电池\",\"priceFen\":14900}]', 14900, ?, ?, '张三', "
                        + "'13800000001', '甘肃省兰州市城关区1号', ?)",
                tenantId, orderNo, orderUserId, status, serviceType,
                serviceType == 20 ? "" : "今天 上午");
    }

    private HttpHeaders user() {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(userToken);
        return h;
    }

    private HttpHeaders admin() {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth("test-admin-token");
        return h;
    }

    private HttpHeaders tenantAdmin() {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(tenantAdminToken);
        return h;
    }

    private void adminAdvance(String orderNo, int to) throws Exception {
        ResponseEntity<String> resp = rest.exchange("/api/admin/repair/order/" + orderNo + "/status",
                HttpMethod.PUT, new HttpEntity<>(Map.of("toStatus", to, "remark", "it"), admin()), String.class);
        assertEquals(0, objectMapper.readTree(resp.getBody()).path("code").asInt(), resp.getBody());
    }

    private ResponseEntity<String> submitQuote(String orderNo, HttpHeaders who, long amountFen) {
        return rest.exchange("/api/admin/repair/order/" + orderNo + "/quote", HttpMethod.POST,
                new HttpEntity<>(Map.of("amountFen", amountFen, "description", "主板受潮，需更换电源ic",
                        "images", List.of("comment/2026-10-08/q1.jpg")), who), String.class);
    }

    private int statusOf(String orderNo) {
        Integer status = jdbcTemplate.queryForObject(
                "select status from repair_order where order_no = ?", Integer.class, orderNo);
        return status == null ? -1 : status;
    }

    @Test
    @Order(1)
    void normal_quote_flow_ends_in_repairing() throws Exception {
        // 接单检测 → 检测完成 → 提交报价 → 用户确认 → 支付 → 维修中
        adminAdvance("F-RQ-ONSITE", 15);
        assertEquals(15, statusOf("F-RQ-ONSITE"));
        adminAdvance("F-RQ-ONSITE", 35);
        assertEquals(35, statusOf("F-RQ-ONSITE"));

        ResponseEntity<String> quoteResp = submitQuote("F-RQ-ONSITE", admin(), 25000);
        JsonNode quoteBody = objectMapper.readTree(quoteResp.getBody());
        assertEquals(0, quoteBody.get("code").asInt(), quoteResp.getBody());
        assertTrue(quoteBody.path("data").path("quoteId").asLong() > 0);
        assertEquals(36, statusOf("F-RQ-ONSITE"));

        // 用户查看报价：金额分、图片已解析
        JsonNode quote = objectMapper.readTree(rest.exchange(
                "/api/wx/repair/order/F-RQ-ONSITE/quote", HttpMethod.GET,
                new HttpEntity<>(user()), String.class).getBody()).path("data");
        assertEquals(25000, quote.path("amountFen").asLong());
        assertEquals(1, quote.path("status").asInt());
        assertEquals(1, quote.path("images").size());

        // 确认报价 → 待支付；支付 → 维修中 + pay_no
        assertEquals(0, objectMapper.readTree(rest.exchange(
                "/api/wx/repair/order/F-RQ-ONSITE/confirm-price", HttpMethod.PUT,
                new HttpEntity<>(user()), String.class).getBody()).get("code").asInt());
        assertEquals(38, statusOf("F-RQ-ONSITE"));

        ResponseEntity<String> payResp = rest.exchange("/api/wx/repair/order/F-RQ-ONSITE/pay",
                HttpMethod.PUT, new HttpEntity<>(user()), String.class);
        assertEquals(0, objectMapper.readTree(payResp.getBody()).get("code").asInt(), payResp.getBody());
        assertTrue(objectMapper.readTree(payResp.getBody()).path("data").path("payNo").asText().startsWith("MOCKPAY-"));
        assertEquals(30, statusOf("F-RQ-ONSITE"));
        String payNo = jdbcTemplate.queryForObject(
                "select pay_no from repair_order where order_no = 'F-RQ-ONSITE'", String.class);
        assertTrue(payNo != null && payNo.startsWith("MOCKPAY-"));

        // 全链状态日志：订单为直插（无创建日志），10→15→35→36→38→30 共 5 条
        Integer logs = jdbcTemplate.queryForObject(
                "select count(*) from order_status_log where order_no = 'F-RQ-ONSITE'", Integer.class);
        assertTrue(logs >= 5, "报价链状态日志应 >= 5 条，实际 " + logs);
    }

    @Test
    @Order(2)
    void rejected_price_returns_and_can_requote() throws Exception {
        adminAdvance("F-RQ-REJECT", 15);
        adminAdvance("F-RQ-REJECT", 35);
        assertEquals(0, objectMapper.readTree(submitQuote("F-RQ-REJECT", admin(), 99000).getBody())
                .get("code").asInt());
        assertEquals(36, statusOf("F-RQ-REJECT"));

        // 拒绝 → 退回 20 已预约（可重新检测报价），报价单置已拒绝
        assertEquals(0, objectMapper.readTree(rest.exchange(
                "/api/wx/repair/order/F-RQ-REJECT/reject-price", HttpMethod.PUT,
                new HttpEntity<>(user()), String.class).getBody()).get("code").asInt());
        assertEquals(20, statusOf("F-RQ-REJECT"));
        Integer rejected = jdbcTemplate.queryForObject(
                "select count(*) from repair_quote where repair_order_no = 'F-RQ-REJECT' and status = 3", Integer.class);
        assertEquals(1, rejected);

        // 重新走一轮报价成立（拒绝不是死路）
        adminAdvance("F-RQ-REJECT", 15);
        adminAdvance("F-RQ-REJECT", 35);
        assertEquals(0, objectMapper.readTree(submitQuote("F-RQ-REJECT", admin(), 58000).getBody())
                .get("code").asInt());
        assertEquals(36, statusOf("F-RQ-REJECT"));
    }

    @Test
    @Order(3)
    void unauthenticated_requests_are_rejected() {
        assertEquals(401, rest.getForEntity("/api/wx/repair/order/F-RQ-ONSITE/quote", String.class)
                .getStatusCode().value());
        assertEquals(401, rest.exchange("/api/admin/repair/order/F-RQ-ONSITE/quote", HttpMethod.POST,
                new HttpEntity<>(Map.of("amountFen", 100)), String.class).getStatusCode().value());
    }

    @Test
    @Order(4)
    void tenant_isolation_blocks_cross_tenant_quote() throws Exception {
        // 租户 5 管理员对租户 0 的维修单报价 → 查无此单（多租户插件隔离）
        ResponseEntity<String> foreign = submitQuote("F-RQ-ONSITE", tenantAdmin(), 100);
        assertNotEquals(0, objectMapper.readTree(foreign.getBody()).get("code").asInt(), foreign.getBody());

        // 对本租户（5）的维修单报价正常
        ResponseEntity<String> own = submitQuote("F-RQ-TENANT5", tenantAdmin(), 6600);
        assertEquals(0, objectMapper.readTree(own.getBody()).get("code").asInt(), own.getBody());
        assertEquals(36, statusOf("F-RQ-TENANT5"));
    }

    @Test
    @Order(5)
    void duplicate_pending_quote_is_rejected() throws Exception {
        // F-RQ-REJECT 当前在 36（待确认），再次报价必须被拒
        ResponseEntity<String> dup = submitQuote("F-RQ-REJECT", admin(), 100);
        assertNotEquals(0, objectMapper.readTree(dup.getBody()).get("code").asInt(), dup.getBody());
    }

    @Test
    @Order(6)
    void invalid_transitions_are_rejected() throws Exception {
        // 寄修收件(25)不可直跳待验收(40)
        ResponseEntity<String> skip = rest.exchange("/api/admin/repair/order/F-RQ-MAIL/status",
                HttpMethod.PUT, new HttpEntity<>(Map.of("toStatus", 40, "remark", "x"), admin()), String.class);
        assertNotEquals(0, objectMapper.readTree(skip.getBody()).get("code").asInt());

        // 未检测先报价
        assertNotEquals(0, objectMapper.readTree(submitQuote("F-RQ-MAIL", admin(), 100).getBody())
                .path("code").asInt());

        // 非待确认状态确认报价
        assertNotEquals(0, objectMapper.readTree(rest.exchange(
                "/api/wx/repair/order/F-RQ-MAIL/confirm-price", HttpMethod.PUT,
                new HttpEntity<>(user()), String.class).getBody()).get("code").asInt());

        // 非待支付状态支付
        assertNotEquals(0, objectMapper.readTree(rest.exchange(
                "/api/wx/repair/order/F-RQ-ONSITE/pay", HttpMethod.PUT,
                new HttpEntity<>(user()), String.class).getBody()).get("code").asInt());

        // 寄修正向边：收件(25)后可进入检测
        adminAdvance("F-RQ-MAIL", 15);
        assertEquals(15, statusOf("F-RQ-MAIL"));
    }

    @Test
    @Order(7)
    void foreign_user_cannot_read_or_confirm_quote() throws Exception {
        // 他人维修单：用户端查报价 403
        ResponseEntity<String> peek = rest.exchange("/api/wx/repair/order/F-RQ-FOREIGN/quote",
                HttpMethod.GET, new HttpEntity<>(user()), String.class);
        assertNotEquals(0, objectMapper.readTree(peek.getBody()).path("code").asInt());

        // 他人维修单：确认报价 403
        ResponseEntity<String> confirm = rest.exchange("/api/wx/repair/order/F-RQ-FOREIGN/confirm-price",
                HttpMethod.PUT, new HttpEntity<>(user()), String.class);
        assertNotEquals(0, objectMapper.readTree(confirm.getBody()).path("code").asInt());
    }
}
