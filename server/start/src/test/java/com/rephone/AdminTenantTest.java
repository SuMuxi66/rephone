package com.rephone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rephone.mapper.RecycleOrderMapper;
import com.rephone.pojo.entity.RecycleOrder;
import java.util.Map;
import java.util.UUID;
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
 * P7 租户管理验收：创建租户附带管理员、租户管理员数据限定本租户、
 * 租户级权限拦截、停用租户整体阻断、平台自营租户保护。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:rephone;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/schema-h2.sql,classpath:db/schema-role.sql,classpath:db/schema-recycle.sql",
        "rephone.wx.mock-login=true",
        "rephone.jwt.ttl-seconds=3600",
        "rephone.admin.token=test-admin-token"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminTenantTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RecycleOrderMapper recycleOrderMapper;

    private long tenantId;
    private String tenantAdminToken;

    private ResponseEntity<String> machineExchange(String uri, HttpMethod method, Object body) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth("test-admin-token");
        return rest.exchange(uri, method, new HttpEntity<>(body, h), String.class);
    }

    private ResponseEntity<String> tenantGet(String uri) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(tenantAdminToken);
        return rest.exchange(uri, HttpMethod.GET, new HttpEntity<>(h), String.class);
    }

    private void insertRecycleOrder(long tid, String orderNo) {
        RecycleOrder order = new RecycleOrder();
        order.setTenantId(tid);
        order.setOrderNo(orderNo);
        order.setUserId(99000L + tid);
        order.setOpenid("tenant-test-" + tid);
        order.setModelId(1L);
        order.setBrandName("Apple");
        order.setModelName("iPhone 15 Pro Max");
        order.setStorage("256GB");
        order.setConditionKey("99new");
        order.setConditionLabel("99新");
        order.setQuoteFen(500000L);
        order.setStatus(10);
        order.setPickupType(10);
        recycleOrderMapper.insert(order);
    }

    @Test
    @Order(1)
    void createTenantWithAdminAccount() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        ResponseEntity<String> resp = machineExchange("/api/admin/tenant", HttpMethod.POST, Map.of(
                "name", " partner-shop-" + suffix,
                "adminUsername", "tadmin_" + suffix,
                "adminPassword", "password123",
                "adminNickname", "店长" + suffix));
        assertEquals(0, objectMapper.readTree(resp.getBody()).get("code").asInt());
        tenantId = objectMapper.readTree(resp.getBody()).path("data").path("tenantId").asLong();
        assertTrue(tenantId > 0);

        ResponseEntity<String> login = rest.postForEntity("/api/admin/auth/login", Map.of(
                "username", "tadmin_" + suffix, "password", "password123"), String.class);
        JsonNode body = objectMapper.readTree(login.getBody());
        assertEquals(0, body.get("code").asInt());
        tenantAdminToken = body.path("data").path("token").asText();
        assertEquals("TENANT_ADMIN", body.path("data").path("roleCode").asText());
        assertEquals(tenantId, body.path("data").path("tenantId").asLong());
    }

    @Test
    @Order(2)
    void tenantAdminSeesOnlyOwnTenantData() throws Exception {
        insertRecycleOrder(tenantId, "RT-OWN-" + tenantId);
        insertRecycleOrder(0, "RT-PLAT-" + tenantId);

        ResponseEntity<String> own = tenantGet("/api/admin/recycle/orders?pageNum=1&pageSize=50");
        assertEquals(0, objectMapper.readTree(own.getBody()).get("code").asInt());
        JsonNode page = objectMapper.readTree(own.getBody()).path("data");
        assertEquals(1, page.get("total").asLong());
        assertEquals("RT-OWN-" + tenantId, page.path("records").get(0).path("orderNo").asText());

        ResponseEntity<String> platform = machineExchange("/api/admin/recycle/orders?pageNum=1&pageSize=50",
                HttpMethod.GET, null);
        long total = objectMapper.readTree(platform.getBody()).path("data").get("total").asLong();
        assertTrue(total >= 2);
    }

    @Test
    @Order(3)
    void tenantAdminDeniedTenantManage() throws Exception {
        ResponseEntity<String> resp = tenantGet("/api/admin/tenants?pageNum=1&pageSize=10");
        assertEquals(403, resp.getStatusCode().value());
        assertEquals(40300, objectMapper.readTree(resp.getBody()).get("code").asInt());
    }

    @Test
    @Order(4)
    void disabledTenantBlocksItsAdmin() throws Exception {
        ResponseEntity<String> resp = machineExchange("/api/admin/tenant/" + tenantId, HttpMethod.PUT,
                Map.of("status", 0));
        assertEquals(0, objectMapper.readTree(resp.getBody()).get("code").asInt());
        ResponseEntity<String> blocked = tenantGet("/api/admin/recycle/orders?pageNum=1&pageSize=10");
        assertEquals(401, blocked.getStatusCode().value());
    }

    @Test
    @Order(5)
    void platformTenantCannotBeDisabled() throws Exception {
        ResponseEntity<String> resp = machineExchange("/api/admin/tenant/0", HttpMethod.PUT, Map.of("status", 0));
        assertEquals(40073, objectMapper.readTree(resp.getBody()).get("code").asInt());
    }

    @Test
    @Order(6)
    void createAccountWithFinanceRole() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        ResponseEntity<String> resp = machineExchange("/api/admin/account", HttpMethod.POST, Map.of(
                "username", "fin_" + suffix, "password", "password123",
                "nickname", "财务" + suffix, "roleCode", "FINANCE"));
        assertEquals(0, objectMapper.readTree(resp.getBody()).get("code").asInt());

        ResponseEntity<String> login = rest.postForEntity("/api/admin/auth/login", Map.of(
                "username", "fin_" + suffix, "password", "password123"), String.class);
        assertEquals("FINANCE", objectMapper.readTree(login.getBody()).path("data").path("roleCode").asText());
    }
}
