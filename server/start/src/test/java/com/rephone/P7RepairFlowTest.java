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
import org.springframework.test.context.TestPropertySource;

/**
 * P7 验收（R1 范围）：维修项目字典与机型价回退、下单服务端计价（客户端不传金额）、
 * 取消与管理端状态机。金额断言单位为分。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:rephone;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/schema-h2.sql,classpath:db/schema-quote.sql,classpath:db/schema-recycle.sql,classpath:db/schema-repair.sql",
        "rephone.wx.mock-login=true",
        "rephone.jwt.ttl-seconds=3600",
        "rephone.admin.token=test-admin-token"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class P7RepairFlowTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeAll
    void login() throws Exception {
        ResponseEntity<String> resp = rest.postForEntity("/api/wx/login",
                Map.of("code", "p7-code"), String.class);
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

    private long itemPrice(long modelId, long itemId) throws Exception {
        ResponseEntity<String> resp = rest.exchange("/api/wx/repair/items?modelId=" + modelId,
                HttpMethod.GET, new HttpEntity<>(auth()), String.class);
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertEquals(0, body.get("code").asInt());
        for (JsonNode group : body.path("data")) {
            for (JsonNode item : group.path("items")) {
                if (item.path("itemId").asLong() == itemId) {
                    return item.path("priceFen").asLong();
                }
            }
        }
        throw new AssertionError("机型 " + modelId + " 未下发维修项目 " + itemId);
    }

    private ResponseEntity<String> createOrder(long modelId, List<Long> itemIds) {
        Map<String, Object> payload = Map.of(
                "modelId", modelId, "itemIds", itemIds, "serviceType", 10,
                "contactName", "李四", "contactPhone", "13900000001",
                "address", "甘肃省兰州市城关区YY路2号", "appointTime", "今天 下午",
                "remark", "测试维修单", "images", List.of());
        return rest.exchange("/api/wx/repair/order", HttpMethod.POST,
                new HttpEntity<>(payload, auth()), String.class);
    }

    private JsonNode detail(String orderNo) throws Exception {
        ResponseEntity<String> resp = rest.exchange("/api/wx/repair/order/" + orderNo,
                HttpMethod.GET, new HttpEntity<>(auth()), String.class);
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertEquals(0, body.get("code").asInt());
        return body.path("data");
    }

    @Test
    @Order(1)
    void items_price_fallback_create_cancel_flow() throws Exception {
        // 1. 机型价覆盖：iPhone 15 Pro Max（modelId=1）换外屏=699 元；iPhone 15（modelId=2）回退基准价 199 元
        assertEquals(69900L, itemPrice(1, 1), "机型价应优先于基准价");
        assertEquals(19900L, itemPrice(2, 1), "未覆盖机型应回退基准价");

        // 2. 下单：modelId=1，换外屏(699) + 更换电池(499) = 1198 元 = 119800 分（服务端计价）
        ResponseEntity<String> createResp = createOrder(1, List.of(1L, 3L));
        JsonNode created = objectMapper.readTree(createResp.getBody());
        assertEquals(0, created.get("code").asInt());
        String orderNo = created.path("data").path("orderNo").asText();
        assertTrue(orderNo.startsWith("F") && orderNo.length() > 10, "维修单号应业务生成（F前缀）");

        JsonNode d = detail(orderNo);
        assertEquals(10, d.path("status").asInt(), "新单应为待确认");
        assertEquals(119800L, d.path("totalFen").asLong(), "合计应为服务端实时计价");
        assertEquals(2, d.path("items").size(), "详情应包含项目快照");
        assertEquals(180, d.path("warrantyDays").asInt());

        // 3. 列表可见
        ResponseEntity<String> listResp = rest.exchange("/api/wx/repair/orders", HttpMethod.GET,
                new HttpEntity<>(auth()), String.class);
        assertTrue(objectMapper.readTree(listResp.getBody()).path("data").path("records").size() >= 1);

        // 4. 用户取消（仅待确认可取消）→ 80
        ResponseEntity<String> cancelResp = rest.exchange("/api/wx/repair/order/" + orderNo + "/cancel",
                HttpMethod.PUT, new HttpEntity<>(Map.of("reason", "暂时不修了"), auth()), String.class);
        assertEquals(0, objectMapper.readTree(cancelResp.getBody()).get("code").asInt());
        assertEquals(80, detail(orderNo).path("status").asInt());
    }

    @Test
    @Order(2)
    void admin_transition_and_cancel_guard() throws Exception {
        ResponseEntity<String> createResp = createOrder(2, List.of(1L, 3L));
        String orderNo = objectMapper.readTree(createResp.getBody()).path("data").path("orderNo").asText();

        // 管理端确认预约：10 → 20
        ResponseEntity<String> t1 = rest.exchange("/api/admin/repair/order/" + orderNo + "/status",
                HttpMethod.PUT, new HttpEntity<>(Map.of("toStatus", 20, "remark", "已电话确认"), admin()),
                String.class);
        assertEquals(0, objectMapper.readTree(t1.getBody()).get("code").asInt());
        assertEquals(20, detail(orderNo).path("status").asInt());

        // 已预约（20）后用户不可取消
        ResponseEntity<String> cancelResp = rest.exchange("/api/wx/repair/order/" + orderNo + "/cancel",
                HttpMethod.PUT, new HttpEntity<>(Map.of("reason", "x"), auth()), String.class);
        assertNotEquals(0, objectMapper.readTree(cancelResp.getBody()).get("code").asInt(), "已预约后应禁止取消");

        // 非法跳转：20 → 50 应被拒绝
        ResponseEntity<String> bad = rest.exchange("/api/admin/repair/order/" + orderNo + "/status",
                HttpMethod.PUT, new HttpEntity<>(Map.of("toStatus", 50, "remark", "x"), admin()),
                String.class);
        assertNotEquals(0, objectMapper.readTree(bad.getBody()).get("code").asInt(), "跳过状态应被拒绝");
    }

    @Test
    @Order(3)
    void invalid_inputs_and_admin_auth() throws Exception {
        // 无效维修项目
        assertNotEquals(0, objectMapper.readTree(createOrder(1, List.of(9999L)).getBody()).get("code").asInt());
        // 缺预约时间
        Map<String, Object> noTime = Map.of(
                "modelId", 1, "itemIds", List.of(1L), "serviceType", 10,
                "contactName", "王五", "contactPhone", "13700000001",
                "address", " somewhere", "remark", "", "images", List.of());
        ResponseEntity<String> resp = rest.exchange("/api/wx/repair/order", HttpMethod.POST,
                new HttpEntity<>(noTime, auth()), String.class);
        assertNotEquals(0, objectMapper.readTree(resp.getBody()).get("code").asInt(), "缺预约时间应被拒绝");
        // 超长联系人（列宽 32）应返回业务错误而非 DB 异常
        Map<String, Object> longName = Map.of(
                "modelId", 1, "itemIds", List.of(1L), "serviceType", 10,
                "contactName", "测".repeat(300), "contactPhone", "13700000002",
                "address", " somewhere", "appointTime", "今天 上午", "remark", "", "images", List.of());
        ResponseEntity<String> longResp = rest.exchange("/api/wx/repair/order", HttpMethod.POST,
                new HttpEntity<>(longName, auth()), String.class);
        assertNotEquals(0, objectMapper.readTree(longResp.getBody()).get("code").asInt(), "超长联系人应被拒绝");

        // items 缺 modelId
        ResponseEntity<String> noModel = rest.exchange("/api/wx/repair/items", HttpMethod.GET,
                new HttpEntity<>(auth()), String.class);
        assertNotEquals(0, objectMapper.readTree(noModel.getBody()).get("code").asInt());

        // 管理端无 token 应 401
        ResponseEntity<String> noAdmin = rest.getForEntity("/api/admin/repair/orders", String.class);
        assertEquals(401, noAdmin.getStatusCode().value(), "管理端匿名访问应 401");
    }
}
