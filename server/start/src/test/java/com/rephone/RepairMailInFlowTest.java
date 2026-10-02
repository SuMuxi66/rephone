package com.rephone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
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
 * P7 寄修验收：寄修全流程（建单→确认→用户填寄出单→收件开修→修好→商家填回寄单→确认收货）、
 * 状态机守卫（上门单禁填运单、寄修跳步拒绝、取消窗口）、轨迹快照缓存。
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
        "rephone.admin.token=test-admin-token",
        "rephone.express.mock=true"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RepairMailInFlowTest {

    private static final String EXPRESS_NO = "75316922091234";

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeAll
    void login() throws Exception {
        ResponseEntity<String> resp = rest.postForEntity("/api/wx/login",
                Map.of("code", "mailin-code"), String.class);
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

    private String createOrder(int serviceType) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("modelId", 1);
        payload.put("itemIds", List.of(1));
        payload.put("serviceType", serviceType);
        payload.put("contactName", "王五");
        payload.put("contactPhone", "13700000001");
        payload.put("address", "江苏省南京市鼓楼区中山北路1号");
        payload.put("appointTime", serviceType == 20 ? "" : "明天 上午");
        payload.put("remark", "寄修测试");
        payload.put("images", List.of());
        ResponseEntity<String> resp = rest.exchange("/api/wx/repair/order", HttpMethod.POST,
                new HttpEntity<>(payload, auth()), String.class);
        assertEquals(0, objectMapper.readTree(resp.getBody()).get("code").asInt(), resp.getBody());
        return objectMapper.readTree(resp.getBody()).path("data").path("orderNo").asText();
    }

    private ResponseEntity<String> adminPut(String uri, Object body) {
        return rest.exchange(uri, HttpMethod.PUT, new HttpEntity<>(body, admin()), String.class);
    }

    private ResponseEntity<String> userPut(String uri, Object body) {
        return rest.exchange(uri, HttpMethod.PUT, new HttpEntity<>(body, auth()), String.class);
    }

    private JsonNode detail(String orderNo) throws Exception {
        ResponseEntity<String> resp = rest.exchange("/api/wx/repair/order/" + orderNo,
                HttpMethod.GET, new HttpEntity<>(auth()), String.class);
        return objectMapper.readTree(resp.getBody()).path("data");
    }

    @Test
    @Order(1)
    void mailInFullFlow() throws Exception {
        String orderNo = createOrder(20);
        assertEquals(10, detail(orderNo).path("status").asInt());
        assertEquals(20, detail(orderNo).path("serviceType").asInt());
        assertEquals("", detail(orderNo).path("appointTime").asText());

        // 管理端确认寄修 10→20
        assertEquals(0, objectMapper.readTree(adminPut(
                "/api/admin/repair/order/" + orderNo + "/status",
                Map.of("toStatus", 20, "remark", "确认寄修")).getBody()).get("code").asInt());

        // 用户填寄出运单号 20→25
        assertEquals(0, objectMapper.readTree(userPut(
                "/api/wx/repair/order/" + orderNo + "/express",
                Map.of("expressCompany", "中通快递", "expressCom", "zhongtong", "expressNo", EXPRESS_NO)
        ).getBody()).get("code").asInt());
        JsonNode shipped = detail(orderNo);
        assertEquals(25, shipped.path("status").asInt());
        assertEquals("zhongtong", shipped.path("expressCom").asText());
        assertEquals(EXPRESS_NO, shipped.path("expressNo").asText());

        // 寄修跳步 20→30 拒绝
        String earlyOrder = createOrder(20);
        adminPut("/api/admin/repair/order/" + earlyOrder + "/status", Map.of("toStatus", 20));
        ResponseEntity<String> skip = adminPut("/api/admin/repair/order/" + earlyOrder + "/status",
                Map.of("toStatus", 30));
        assertEquals(40036, objectMapper.readTree(skip.getBody()).get("code").asInt());

        // 收件开修 25→30、修好 30→40
        assertEquals(0, objectMapper.readTree(adminPut(
                "/api/admin/repair/order/" + orderNo + "/status", Map.of("toStatus", 30)).getBody())
                .get("code").asInt());
        assertEquals(0, objectMapper.readTree(adminPut(
                "/api/admin/repair/order/" + orderNo + "/status", Map.of("toStatus", 40)).getBody())
                .get("code").asInt());

        // 商家填回寄运单号 40→45
        assertEquals(0, objectMapper.readTree(adminPut(
                "/api/admin/repair/order/" + orderNo + "/return-express",
                Map.of("expressCompany", "顺丰速运", "expressCom", "shunfeng", "expressNo", "SF1234567890")
        ).getBody()).get("code").asInt());
        assertEquals(45, detail(orderNo).path("status").asInt());
        assertEquals("SF1234567890", detail(orderNo).path("returnExpressNo").asText());

        // 用户确认收货 45→50
        assertEquals(0, objectMapper.readTree(userPut(
                "/api/wx/repair/order/" + orderNo + "/confirm", null).getBody()).get("code").asInt());
        assertEquals(50, detail(orderNo).path("status").asInt());

        // 完成后再确认应拒绝
        ResponseEntity<String> lateConfirm = userPut(
                "/api/wx/repair/order/" + orderNo + "/confirm", null);
        assertEquals(40051, objectMapper.readTree(lateConfirm.getBody()).get("code").asInt());
    }

    @Test
    @Order(2)
    void onsiteOrderCannotFillExpress() throws Exception {
        String orderNo = createOrder(10);
        ResponseEntity<String> resp = userPut("/api/wx/repair/order/" + orderNo + "/express",
                Map.of("expressCompany", "中通快递", "expressCom", "zhongtong", "expressNo", EXPRESS_NO));
        assertEquals(40048, objectMapper.readTree(resp.getBody()).get("code").asInt());
    }

    @Test
    @Order(3)
    void mailInCancelWindowClosesAfterShipping() throws Exception {
        String orderNo = createOrder(20);
        adminPut("/api/admin/repair/order/" + orderNo + "/status", Map.of("toStatus", 20));
        // 寄出前（20）可取消
        assertEquals(0, objectMapper.readTree(userPut(
                "/api/wx/repair/order/" + orderNo + "/cancel",
                Map.of("reason", "不想修了")).getBody()).get("code").asInt());
        assertEquals(80, detail(orderNo).path("status").asInt());

        // 寄出后（25）不可取消
        String orderNo2 = createOrder(20);
        adminPut("/api/admin/repair/order/" + orderNo2 + "/status", Map.of("toStatus", 20));
        userPut("/api/wx/repair/order/" + orderNo2 + "/express",
                Map.of("expressCompany", "中通快递", "expressCom", "zhongtong", "expressNo", EXPRESS_NO));
        ResponseEntity<String> lateCancel = userPut("/api/wx/repair/order/" + orderNo2 + "/cancel",
                Map.of("reason", "测试"));
        assertEquals(40036, objectMapper.readTree(lateCancel.getBody()).get("code").asInt());
    }

    @Test
    @Order(4)
    void traceUsesSnapshotOnSecondCall() throws Exception {
        String orderNo = createOrder(20);
        adminPut("/api/admin/repair/order/" + orderNo + "/status", Map.of("toStatus", 20));
        userPut("/api/wx/repair/order/" + orderNo + "/express",
                Map.of("expressCompany", "中通快递", "expressCom", "zhongtong", "expressNo", EXPRESS_NO));

        ResponseEntity<String> first = rest.exchange("/api/wx/repair/order/" + orderNo + "/trace",
                HttpMethod.GET, new HttpEntity<>(auth()), String.class);
        JsonNode firstBody = objectMapper.readTree(first.getBody());
        assertEquals(0, firstBody.get("code").asInt());
        assertEquals("zhongtong", firstBody.path("data").path("com").asText());
        assertTrue(firstBody.path("data").path("items").size() > 0);

        ResponseEntity<String> second = rest.exchange("/api/wx/repair/order/" + orderNo + "/trace",
                HttpMethod.GET, new HttpEntity<>(auth()), String.class);
        JsonNode secondBody = objectMapper.readTree(second.getBody());
        assertEquals(0, secondBody.get("code").asInt());
        assertTrue(secondBody.path("data").path("cached").asBoolean(), "第二次查询应命中本地快照");

        // 回寄轨迹：未填回寄单号时明确报错
        ResponseEntity<String> returnTrace = rest.exchange(
                "/api/wx/repair/order/" + orderNo + "/return-trace",
                HttpMethod.GET, new HttpEntity<>(auth()), String.class);
        assertEquals(40050, objectMapper.readTree(returnTrace.getBody()).get("code").asInt());
    }
}
