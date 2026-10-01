package com.rephone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * P3/P4 验收：回收订单闭环（创建→填写运单→回调揽收→质检→打款）+ COS 签名 + 管理端鉴权。
 * express/cos/wxpay 均为 mock 模式；金额断言单位为分。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:rephone;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/schema-h2.sql,classpath:db/schema-quote.sql,classpath:db/schema-recycle.sql",
        "rephone.wx.mock-login=true",
        "rephone.jwt.ttl-seconds=3600",
        "rephone.admin.token=test-admin-token",
        "rephone.express.mock=true",
        "rephone.cos.mock=true"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class P3OrderFlowTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String token;

    @BeforeAll
    void login() throws Exception {
        ResponseEntity<String> resp = rest.postForEntity("/api/wx/login",
                Map.of("code", "p3-code"), String.class);
        token = objectMapper.readTree(resp.getBody()).path("data").path("token").asText();
        assertNotEquals("", token);
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

    private ResponseEntity<String> createOrder(Long quoteFen, int pickupType) {
        Map<String, Object> payload = Map.of(
                "modelId", 1, "storage", "128GB", "condition", "COND_95",
                "issues", List.of("SCREEN"), "quoteFen", quoteFen,
                "pickupType", pickupType,
                "pickupName", "张三", "pickupPhone", "13800000001",
                "pickupAddress", "甘肃省兰州市城关区XX路1号", "remark", "测试单");
        return rest.exchange("/api/wx/recycle/order", HttpMethod.POST,
                new HttpEntity<>(payload, auth()), String.class);
    }

    @Test
    @Order(1)
    void create_list_detail_express() throws Exception {
        // 1. 创建订单（报价与 P2 算例一致）
        ResponseEntity<String> createResp = createOrder(566000L, 10);
        JsonNode created = objectMapper.readTree(createResp.getBody());
        assertEquals(0, created.get("code").asInt());
        String orderNo = created.path("data").path("orderNo").asText();
        assertTrue(orderNo.startsWith("R") && orderNo.length() > 10, "订单号应业务生成");

        // 2. 列表可见
        ResponseEntity<String> listResp = rest.exchange("/api/wx/recycle/orders", HttpMethod.GET,
                new HttpEntity<>(auth()), String.class);
        JsonNode list = objectMapper.readTree(listResp.getBody());
        assertTrue(list.path("data").path("records").size() >= 1);

        // 3. 详情：待寄出
        JsonNode detail = getDetail(orderNo);
        assertEquals(10, detail.path("status").asInt());
        assertEquals(566000L, detail.path("quoteFen").asLong());

        // 4. 填运单号 → 20 运输中
        ResponseEntity<String> expressResp = rest.exchange(
                "/api/wx/recycle/order/" + orderNo + "/express", HttpMethod.PUT,
                new HttpEntity<>(Map.of("expressCompany", "顺丰速运", "expressNo", "SF123456789"), auth()),
                String.class);
        assertEquals(0, objectMapper.readTree(expressResp.getBody()).get("code").asInt());
        assertEquals(20, getDetail(orderNo).path("status").asInt());

        // 5. 待寄出之外不可取消
        ResponseEntity<String> cancelResp = rest.exchange(
                "/api/wx/recycle/order/" + orderNo + "/cancel", HttpMethod.PUT,
                new HttpEntity<>(Map.of("reason", "x"), auth()), String.class);
        assertNotEquals(0, objectMapper.readTree(cancelResp.getBody()).get("code").asInt());

        // 6. 质检：30 → 提交质检 → 40 + 最终价
        adminStatus(orderNo, 30);
        assertEquals(30, getDetail(orderNo).path("status").asInt());
        ResponseEntity<String> inspResp = rest.exchange(
                "/api/admin/recycle/order/" + orderNo + "/inspection", HttpMethod.POST,
                new HttpEntity<>(Map.of("result", "外观轻微磨损，功能正常", "finalFen", 500000,
                        "images", List.of("recycle/insp/1.jpg")), admin()), String.class);
        assertEquals(0, objectMapper.readTree(inspResp.getBody()).get("code").asInt());
        JsonNode detail40 = getDetail(orderNo);
        assertEquals(40, detail40.path("status").asInt());
        assertEquals(500000L, detail40.path("finalFen").asLong());
        assertTrue(detail40.path("inspections").size() >= 1, "详情应包含质检记录");

        // 7. 用户确认 → mock 打款 → 50 已打款
        ResponseEntity<String> confirmResp = rest.exchange(
                "/api/wx/recycle/order/" + orderNo + "/confirm", HttpMethod.PUT,
                new HttpEntity<>(auth()), String.class);
        assertEquals(0, objectMapper.readTree(confirmResp.getBody()).get("code").asInt());
        assertEquals(50, getDetail(orderNo).path("status").asInt());

        // 8. 状态流转日志完整
        Integer logs = jdbcTemplate.queryForObject(
                "select count(*) from order_status_log where order_no = ?", Integer.class, orderNo);
        assertTrue(logs >= 5, "状态日志应 >= 5 条，实际 " + logs);

        // 9. 报价不一致下单被拒
        ResponseEntity<String> tampered = createOrder(1L, 10);
        assertNotEquals(0, objectMapper.readTree(tampered.getBody()).get("code").asInt(), "篡改报价应被拒绝");
    }

    @Test
    @Order(2)
    void express_callback_moves_pickup_order_to_shipping() throws Exception {
        ResponseEntity<String> createResp = createOrder(566000L, 20);
        String orderNo = objectMapper.readTree(createResp.getBody()).path("data").path("orderNo").asText();

        // 上门取件单创建时应已绑定 mock 运单号（快递100 预约结果回写）
        JsonNode detail = getDetail(orderNo);
        String expressNo = detail.path("expressNo").asText();
        assertTrue(!expressNo.isBlank(), "上门取件单应有运单号");

        // 快递员揽收（模拟快递100 推送回调；mock 模式免验签）
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("param", "{\"kuaidinum\":\"" + expressNo + "\",\"last_result\":[{\"message\":\"快件已揽收\"}]}");
        form.add("sign", "ignored-in-mock");
        ResponseEntity<String> cb = rest.postForEntity("/api/callback/kuaidi100", form, String.class);
        assertEquals("success", cb.getBody());
        assertEquals(20, getDetail(orderNo).path("status").asInt(), "揽收回调应推进到运输中");
    }

    @Test
    @Order(3)
    void cos_sign_mock_and_admin_auth() throws Exception {
        ResponseEntity<String> signResp = rest.exchange("/api/wx/cos/upload-sign?ext=jpg", HttpMethod.GET,
                new HttpEntity<>(auth()), String.class);
        JsonNode sign = objectMapper.readTree(signResp.getBody()).path("data");
        assertTrue(sign.path("mock").asBoolean(), "未配 COS 密钥应返回 mock 签名");
        assertTrue(sign.path("key").asText().startsWith("recycle/"), "对象 key 应在 recycle/ 目录");

        ResponseEntity<String> noAdmin = rest.getForEntity("/api/admin/recycle/orders", String.class);
        assertEquals(401, noAdmin.getStatusCode().value(), "管理端无 token 应 401");
    }

    @Test
    @Order(4)
    void inspection_final_price_upper_bound() throws Exception {
        // 质检最终价必须为正且不超过估价两倍（566000 分估价 → 上限 1132000 分）
        ResponseEntity<String> createResp = createOrder(566000L, 10);
        String orderNo = objectMapper.readTree(createResp.getBody()).path("data").path("orderNo").asText();
        // 状态机要求 10 → 20（填运单）后才能进入质检
        rest.exchange("/api/wx/recycle/order/" + orderNo + "/express", HttpMethod.PUT,
                new HttpEntity<>(Map.of("expressCompany", "顺丰速运", "expressNo", "SF-BOUND-4"), auth()),
                String.class);
        adminStatus(orderNo, 30);

        ResponseEntity<String> resp = rest.exchange("/api/admin/recycle/order/" + orderNo + "/inspection",
                HttpMethod.POST, new HttpEntity<>(Map.of("result", "质检结论", "finalFen", 2000000L,
                        "images", List.of()), admin()), String.class);
        assertNotEquals(0, objectMapper.readTree(resp.getBody()).get("code").asInt(),
                "最终价超估价两倍应被拒绝");
    }

    /** 快递100 实时查询接入：公司字典统一 + 30 分钟快照防超频锁单。 */
    @Test
    @Order(5)
    void express_companies_and_trace_snapshot() throws Exception {
        // 1. 快递公司字典（编码与后端查询共用一份，不再是前端写死的 mock）
        JsonNode companies = objectMapper.readTree(rest.exchange("/api/wx/express/companies", HttpMethod.GET,
                new HttpEntity<>(auth()), String.class).getBody()).path("data");
        assertTrue(companies.size() >= 10, "快递公司字典应包含常用公司");
        boolean hasShunfeng = false;
        for (JsonNode c : companies) {
            if ("shunfeng".equals(c.path("com").asText())) {
                hasShunfeng = true;
                assertTrue(c.path("needPhone").asBoolean(), "顺丰查询轨迹必须带收寄件人电话");
            }
        }
        assertTrue(hasShunfeng, "字典应包含顺丰速运");

        // 2. 只传编码填运单号，展示名由后端从字典补齐
        String orderNo = objectMapper.readTree(createOrder(566000L, 10).getBody())
                .path("data").path("orderNo").asText();
        ResponseEntity<String> fillResp = rest.exchange("/api/wx/recycle/order/" + orderNo + "/express",
                HttpMethod.PUT,
                new HttpEntity<>(Map.of("expressCom", "shunfeng", "expressNo", "SF1234567890"), auth()),
                String.class);
        assertEquals(0, objectMapper.readTree(fillResp.getBody()).get("code").asInt());
        JsonNode filled = getDetail(orderNo);
        assertEquals(20, filled.path("status").asInt());
        assertEquals("顺丰速运", filled.path("expressCompany").asText());

        // 3. 首次查询回源快递100
        JsonNode first = trace(orderNo);
        assertTrue(first.path("items").size() >= 1, "轨迹应返回节点");
        assertEquals("shunfeng", first.path("com").asText());
        assertFalse(first.path("cached").asBoolean(), "首次查询不应命中快照");

        // 4. 二次查询命中本地快照，不再消耗快递100 单量
        assertTrue(trace(orderNo).path("cached").asBoolean(), "30 分钟内应命中快照");

        // 5. 认不出的快递公司必须被拒，避免存进查不了轨迹的脏数据
        String orderNo2 = objectMapper.readTree(createOrder(566000L, 10).getBody())
                .path("data").path("orderNo").asText();
        ResponseEntity<String> bad = rest.exchange("/api/wx/recycle/order/" + orderNo2 + "/express",
                HttpMethod.PUT,
                new HttpEntity<>(Map.of("expressCompany", "某某物流", "expressNo", "XX123456789"), auth()),
                String.class);
        assertNotEquals(0, objectMapper.readTree(bad.getBody()).get("code").asInt(), "未知快递公司应被拒绝");

        // 6. 没有运单号时不允许查轨迹
        ResponseEntity<String> noNo = rest.exchange("/api/wx/recycle/order/" + orderNo2 + "/trace",
                HttpMethod.GET, new HttpEntity<>(auth()), String.class);
        assertNotEquals(0, objectMapper.readTree(noNo.getBody()).get("code").asInt(), "无运单号不应返回轨迹");
    }

    private JsonNode trace(String orderNo) throws Exception {
        ResponseEntity<String> resp = rest.exchange("/api/wx/recycle/order/" + orderNo + "/trace",
                HttpMethod.GET, new HttpEntity<>(auth()), String.class);
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertEquals(0, body.get("code").asInt(), "查轨迹失败: " + resp.getBody());
        return body.path("data");
    }

    private JsonNode getDetail(String orderNo) throws Exception {
        ResponseEntity<String> resp = rest.exchange("/api/wx/recycle/order/" + orderNo, HttpMethod.GET,
                new HttpEntity<>(auth()), String.class);
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertEquals(0, body.get("code").asInt());
        return body.path("data");
    }

    private void adminStatus(String orderNo, int to) throws Exception {
        ResponseEntity<String> resp = rest.exchange("/api/admin/recycle/order/" + orderNo + "/status",
                HttpMethod.PUT, new HttpEntity<>(Map.of("toStatus", to, "remark", "it"), admin()), String.class);
        assertEquals(0, objectMapper.readTree(resp.getBody()).get("code").asInt());
    }
}
