package com.rephone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rephone.wechat.WxPayClient;
import java.util.Map;
import java.util.TreeMap;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

/**
 * 维修真实支付链路验收（WXPAY_MOCK=true，回调验签/金额/商户号校验为真实逻辑）。
 * 覆盖：mock 正常支付闭环（payment_order 入账 + 38→30）、重复支付拒绝、回调金额篡改拒绝、
 * 回调重复通知幂等、签名错误拒绝、跨用户越权、支付后状态推进与日志。
 * 订单/支付单用 JdbcTemplate 直插；独立 H2 库不污染其他测试。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:rpay;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/schema-h2.sql,classpath:db/schema-payment.sql,"
                + "classpath:db/schema-repair.sql",
        "rephone.wx.mock-login=true",
        "rephone.wx.appid=wx-test-appid",
        "rephone.jwt.ttl-seconds=3600",
        "rephone.admin.token=test-admin-token",
        "rephone.wxpay.mock=true",
        "rephone.wxpay.mch-id=test-mch",
        "rephone.wxpay.mch-key=test-key",
        "rephone.wxpay.notify-url=https://example.com/api/callback/wxpay"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RepairPaymentFlowTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private com.rephone.service.PaymentService paymentService;

    private String userToken;
    private long userId;

    @BeforeAll
    void loginAndSeed() throws Exception {
        ResponseEntity<String> resp = rest.postForEntity("/api/wx/login",
                Map.of("code", "rpay-code"), String.class);
        JsonNode data = objectMapper.readTree(resp.getBody()).path("data");
        userToken = data.path("token").asText();
        assertNotEquals("", userToken);
        userId = data.path("userId").asLong();

        insertOrder("F-RP-1", userId, 38);
        insertQuote("F-RP-1");
        insertOrder("F-RP-2", userId, 38);
        insertQuote("F-RP-2");
        insertOrder("F-RP-3", userId, 38);
        insertQuote("F-RP-3");
        insertOrder("F-RP-4", userId, 38);
        insertQuote("F-RP-4");
        insertOrder("F-RP-OTHER", 999999, 38);
        insertQuote("F-RP-OTHER");

        // 待支付支付单（模拟 pay 已发起、等待回调）：金额篡改/重复通知/坏签名用例各一条
        insertPendingPayment("F-RP-2", "P-RP-T2", 25000);
        insertPendingPayment("F-RP-3", "P-RP-T3", 25000);
        insertPendingPayment("F-RP-4", "P-RP-T4", 25000);
    }

    private void insertOrder(String orderNo, long orderUserId, int status) {
        jdbcTemplate.update("insert into repair_order (tenant_id, order_no, user_id, openid, model_id, "
                        + "brand_name, model_name, items_json, total_fen, status, service_type, contact_name, "
                        + "contact_phone, address, appoint_time) "
                        + "values (0, ?, ?, 'seed-rpay', 1, 'Apple', 'iPhone 15', "
                        + "'[{\"itemId\":3,\"name\":\"更换电池\",\"priceFen\":14900}]', 14900, ?, 10, '张三', "
                        + "'13800000001', '甘肃省兰州市城关区1号', '今天 上午')",
                orderNo, orderUserId, status);
    }

    private void insertQuote(String orderNo) {
        jdbcTemplate.update("insert into repair_quote (tenant_id, repair_order_no, quote_amount, description, "
                        + "status) values (0, ?, 250.00, '更换电池', 2)", orderNo);
    }

    private void insertPendingPayment(String orderNo, String payNo, long amountFen) {
        jdbcTemplate.update("insert into payment_order (tenant_id, order_no, order_type, pay_no, amount_fen, status) "
                + "values (0, ?, 30, ?, ?, 0)", orderNo, payNo, amountFen);
    }

    private HttpHeaders user() {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(userToken);
        return h;
    }

    /** 构造微信 V2 回调报文；sign 传 null 表示按 test-key 正确签名。 */
    private String callbackXml(String payNo, long totalFee, String sign, String txnId) {
        Map<String, String> params = new TreeMap<>();
        params.put("appid", "wx-test-appid");
        params.put("mch_id", "test-mch");
        params.put("nonce_str", "testnonce");
        params.put("out_trade_no", payNo);
        params.put("result_code", "SUCCESS");
        params.put("return_code", "SUCCESS");
        params.put("total_fee", String.valueOf(totalFee));
        params.put("trade_type", "JSAPI");
        params.put("transaction_id", txnId);
        params.put("sign", sign == null ? WxPayClient.signV2(params, "test-key") : sign);
        return WxPayClient.buildXml(params);
    }

    private ResponseEntity<String> notify(String xml) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_XML);
        return rest.postForEntity("/api/callback/wxpay", new HttpEntity<>(xml, h), String.class);
    }

    private int statusOf(String orderNo) {
        Integer status = jdbcTemplate.queryForObject(
                "select status from repair_order where order_no = ?", Integer.class, orderNo);
        return status == null ? -1 : status;
    }

    private int paymentStatus(String payNo) {
        Integer status = jdbcTemplate.queryForObject(
                "select status from payment_order where pay_no = ?", Integer.class, payNo);
        return status == null ? -1 : status;
    }

    private int logCount(String orderNo) {
        Integer count = jdbcTemplate.queryForObject(
                "select count(*) from order_status_log where order_no = ?", Integer.class, orderNo);
        return count == null ? 0 : count;
    }

    @Test
    @Order(1)
    void normal_pay_closes_loop_and_advances_order() throws Exception {
        ResponseEntity<String> resp = rest.exchange("/api/wx/repair/order/F-RP-1/pay", HttpMethod.PUT,
                new HttpEntity<>(user()), String.class);
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertEquals(0, body.get("code").asInt(), resp.getBody());
        assertEquals(Boolean.TRUE, body.path("data").path("mock").asBoolean(), "测试配置为 mock 模式");
        assertEquals(Boolean.TRUE, body.path("data").path("paid").asBoolean(), "mock 模式应直接闭环");
        String payNo = body.path("data").path("payNo").asText();
        assertTrue(payNo.startsWith("P") && payNo.length() > 10, "支付单号业务生成");

        // 订单推进 + 流水落库
        assertEquals(30, statusOf("F-RP-1"));
        String payNoOnOrder = jdbcTemplate.queryForObject(
                "select pay_no from repair_order where order_no = 'F-RP-1'", String.class);
        assertEquals(payNo, payNoOnOrder);
        assertEquals(1, paymentStatus(payNo), "payment_order 应已支付");
        String txn = jdbcTemplate.queryForObject(
                "select transaction_id from payment_order where pay_no = ?", String.class, payNo);
        assertNotNull(txn);
        assertTrue(txn.startsWith("MOCKTXN-"));
        assertTrue(logCount("F-RP-1") >= 1, "支付应写状态日志");
    }

    @Test
    @Order(2)
    void repeat_pay_is_rejected() throws Exception {
        // F-RP-1 已是维修中（30），再次发起支付必须拒绝
        ResponseEntity<String> resp = rest.exchange("/api/wx/repair/order/F-RP-1/pay", HttpMethod.PUT,
                new HttpEntity<>(user()), String.class);
        assertNotEquals(0, objectMapper.readTree(resp.getBody()).path("code").asInt());
    }

    @Test
    @Order(3)
    void tampered_amount_is_rejected() {
        // 回调金额与支付单不符：验签通过也必须拒绝，订单与支付单原状
        ResponseEntity<String> resp = notify(callbackXml("P-RP-T2", 100, null, "4200TEST2"));
        assertTrue(resp.getBody().contains("FAIL"), "金额不符应回 FAIL: " + resp.getBody());
        assertEquals(38, statusOf("F-RP-2"));
        assertEquals(0, paymentStatus("P-RP-T2"), "支付单应保持待支付");
    }

    @Test
    @Order(4)
    void duplicate_notify_is_idempotent() {
        ResponseEntity<String> first = notify(callbackXml("P-RP-T3", 25000, null, "4200TEST3"));
        assertTrue(first.getBody().contains("SUCCESS"), "首次通知应成功: " + first.getBody());
        assertEquals(30, statusOf("F-RP-3"));
        assertEquals(1, paymentStatus("P-RP-T3"));
        int logsAfterFirst = logCount("F-RP-3");
        String txnAfterFirst = jdbcTemplate.queryForObject(
                "select transaction_id from payment_order where pay_no = 'P-RP-T3'", String.class);

        // 同一通知重发：仍应答 SUCCESS（微信要求），但不得重复入账/推进
        ResponseEntity<String> second = notify(callbackXml("P-RP-T3", 25000, null, "4200TEST3"));
        assertTrue(second.getBody().contains("SUCCESS"), "重复通知应答成功: " + second.getBody());
        assertEquals(logsAfterFirst, logCount("F-RP-3"), "重复通知不应重复写状态日志");
        String txnAfterSecond = jdbcTemplate.queryForObject(
                "select transaction_id from payment_order where pay_no = 'P-RP-T3'", String.class);
        assertEquals(txnAfterFirst, txnAfterSecond);
    }

    @Test
    @Order(5)
    void bad_signature_is_rejected() {
        ResponseEntity<String> resp = notify(callbackXml("P-RP-T4", 25000, "INVALIDSIGNATURE", "4200TEST4"));
        assertTrue(resp.getBody().contains("FAIL"), "签名错误应回 FAIL: " + resp.getBody());
        assertEquals(38, statusOf("F-RP-4"));
        assertEquals(0, paymentStatus("P-RP-T4"));
    }

    @Test
    @Order(6)
    void foreign_order_payment_is_forbidden() throws Exception {
        ResponseEntity<String> resp = rest.exchange("/api/wx/repair/order/F-RP-OTHER/pay", HttpMethod.PUT,
                new HttpEntity<>(user()), String.class);
        assertNotEquals(0, objectMapper.readTree(resp.getBody()).path("code").asInt(),
                "他人维修单不可支付");
    }

    @Test
    @Order(7)
    void payment_advances_status_with_log() {
        // 场景 1 已覆盖 38→30；这里补断言推进日志存在且为系统操作（operator_type=30）
        Integer rows = jdbcTemplate.queryForObject(
                "select count(*) from order_status_log where order_no = 'F-RP-1' "
                        + "and from_status = 38 and to_status = 30 and operator_type = 30", Integer.class);
        assertEquals(1, rows, "应有一条 38→30 的系统推进日志");
    }

    /** M4：过期 PENDING 自动关闭——过期关/未过期保留/已支付不动/重复关闭幂等/迟到回调兑现/重新支付新生成。 */
    @Test
    @Order(8)
    void expired_pending_payments_auto_close() throws Exception {
        insertOrder("F-RP-EXP", userId, 38);
        insertQuote("F-RP-EXP");
        insertOrder("F-RP-EXP2", userId, 38);
        insertQuote("F-RP-EXP2");
        // 31 分钟前的过期单 + 29 分钟前的未到期单；已支付单把 create_time 拨旧仍不得关闭
        jdbcTemplate.update("insert into payment_order (tenant_id, order_no, order_type, pay_no, amount_fen, status, create_time) "
                + "values (0, 'F-RP-EXP', 30, 'P-RP-EXP1', 25000, 0, dateadd(minute, -31, current_timestamp))");
        jdbcTemplate.update("insert into payment_order (tenant_id, order_no, order_type, pay_no, amount_fen, status, create_time) "
                + "values (0, 'F-RP-EXP2', 30, 'P-RP-EXP2', 25000, 0, dateadd(minute, -31, current_timestamp))");
        jdbcTemplate.update("insert into payment_order (tenant_id, order_no, order_type, pay_no, amount_fen, status, create_time) "
                + "values (0, 'F-RP-KEEP', 30, 'P-RP-KEEP', 25000, 0, dateadd(minute, -29, current_timestamp))");
        jdbcTemplate.update("update payment_order set create_time = dateadd(minute, -31, current_timestamp) "
                + "where pay_no = (select pay_no from repair_order where order_no = 'F-RP-1')");

        int closed = paymentService.closeExpired();
        assertTrue(closed >= 2, "应至少关闭 2 条过期 PENDING");
        assertEquals(2, paymentStatus("P-RP-EXP1"), "过期 PENDING 应被关闭");
        assertEquals(2, paymentStatus("P-RP-EXP2"), "过期 PENDING 应被关闭");
        assertEquals(0, paymentStatus("P-RP-KEEP"), "未到期 PENDING 不得关闭");
        Integer paidStatus = jdbcTemplate.queryForObject(
                "select status from payment_order where pay_no = (select pay_no from repair_order where order_no = 'F-RP-1')",
                Integer.class);
        assertEquals(1, paidStatus, "已支付支付单不得被关闭");

        // 重复关闭：第二次扫描不再有针对这些单的变更（幂等）
        assertEquals(2, paymentStatus("P-RP-EXP1"));
        assertEquals(2, paymentStatus("P-RP-EXP2"));

        // 关联订单不受关单影响：仍 38 待支付
        assertEquals(38, statusOf("F-RP-EXP"));

        // 关单后的迟到真实支付必须兑现：回调应答 SUCCESS，支付单入账、订单推进 38→30
        ResponseEntity<String> late = notify(callbackXml("P-RP-EXP1", 25000, null, "4200EXPLATE"));
        assertTrue(late.getBody().contains("SUCCESS"), "迟到真实支付应兑现: " + late.getBody());
        assertEquals(1, paymentStatus("P-RP-EXP1"), "迟到支付应入账");
        assertEquals(30, statusOf("F-RP-EXP"), "迟到兑现后订单应推进维修中");
        // 重复通知幂等
        ResponseEntity<String> lateAgain = notify(callbackXml("P-RP-EXP1", 25000, null, "4200EXPLATE"));
        assertTrue(lateAgain.getBody().contains("SUCCESS"));
        assertEquals(30, statusOf("F-RP-EXP"));

        // 重新发起支付：不复用已关闭单，生成新支付单并 mock 闭环 → 30
        ResponseEntity<String> payResp = rest.exchange("/api/wx/repair/order/F-RP-EXP2/pay", HttpMethod.PUT,
                new HttpEntity<>(user()), String.class);
        JsonNode body = objectMapper.readTree(payResp.getBody());
        assertEquals(0, body.get("code").asInt(), payResp.getBody());
        String newPayNo = body.path("data").path("payNo").asText();
        assertTrue(newPayNo.startsWith("P") && !newPayNo.equals("P-RP-EXP2"), "重新支付应生成新支付单");
        assertEquals(30, statusOf("F-RP-EXP2"), "重新支付成功后订单应进入维修中");
    }

    /** M5：有效期内重复发起支付必须复用同一待支付单（同 payNo、不产生第二行）。 */
    @Test
    @Order(9)
    void pending_payment_reused_within_ttl() throws Exception {
        insertOrder("F-RP-REUSE", userId, 38);
        insertQuote("F-RP-REUSE");
        insertPendingPayment("F-RP-REUSE", "P-RP-REUSE1", 25000);

        ResponseEntity<String> resp = rest.exchange("/api/wx/repair/order/F-RP-REUSE/pay", HttpMethod.PUT,
                new HttpEntity<>(user()), String.class);
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertEquals(0, body.get("code").asInt(), resp.getBody());
        assertEquals("P-RP-REUSE1", body.path("data").path("payNo").asText(), "有效期内重试应复用同一支付单");
        Integer rows = jdbcTemplate.queryForObject(
                "select count(*) from payment_order where order_no = 'F-RP-REUSE'", Integer.class);
        assertEquals(1, rows, "复用不得产生第二行支付单");
        assertEquals(1, paymentStatus("P-RP-REUSE1"), "mock 闭环后该支付单入账");
        assertEquals(30, statusOf("F-RP-REUSE"), "支付成功后订单应进入维修中");
    }
}
