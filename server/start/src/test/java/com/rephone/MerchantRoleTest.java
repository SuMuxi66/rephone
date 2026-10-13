package com.rephone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;

/**
 * 商家角色（MERCHANT）验收：租户必填校验；/me 仅下发 repair:manage；
 * 可查本租户维修单（租户隔离）；跨模块接口（回收单/账号管理）40300 拒绝。
 * 订单与账号用 JdbcTemplate 直插；独立 H2 库，不污染其他测试类。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:merchant;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/schema-h2.sql,classpath:db/schema-role.sql,"
                + "classpath:db/schema-recycle.sql,classpath:db/schema-repair.sql",
        "rephone.wx.mock-login=true",
        "rephone.jwt.ttl-seconds=3600",
        "rephone.admin.token=test-admin-token"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MerchantRoleTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private HttpHeaders machine() {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth("test-admin-token");
        return h;
    }

    private ResponseEntity<String> get(String uri, String token) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token == null ? "test-admin-token" : token);
        return rest.exchange(uri, HttpMethod.GET, new HttpEntity<>(h), String.class);
    }

    private String merchantToken;

    @BeforeAll
    void seedTenantAndMerchant() throws Exception {
        // 租户 7 启用；商家账号挂租户 7；两笔维修单分属租户 7 与平台 0
        jdbcTemplate.update("merge into tenant (id, name, status) key (id) values (7, '商家测试租户', 1)");
        jdbcTemplate.update("delete from `user` where username = 'mch_test'");
        jdbcTemplate.update("insert into `user` (tenant_id, openid, nickname, status, username, password_hash, role) "
                        + "values (7, 'merchant-test-openid', '维修商家', 1, 'mch_test', ?, 'MERCHANT')",
                encoder.encode("password123"));
        insertOrder(7, "F-MCH-T7");
        insertOrder(0, "F-MCH-T0");

        ResponseEntity<String> login = rest.postForEntity("/api/admin/auth/login",
                Map.of("username", "mch_test", "password", "password123"), String.class);
        merchantToken = objectMapper.readTree(login.getBody()).path("data").path("token").asText();
        assertNotEquals("", merchantToken);
    }

    private void insertOrder(int tenantId, String orderNo) {
        jdbcTemplate.update("insert into repair_order (tenant_id, order_no, user_id, openid, model_id, "
                        + "brand_name, model_name, items_json, total_fen, status, service_type, contact_name, "
                        + "contact_phone, address, appoint_time) "
                        + "values (?, ?, 888888, 'seed', 1, 'Apple', 'iPhone 15', "
                        + "'[{\"itemId\":3,\"name\":\"更换电池\",\"priceFen\":14900}]', 14900, 10, 10, "
                        + "'张三', '13800000001', '甘肃省兰州市城关区1号', '今天 上午')",
                tenantId, orderNo);
    }

    @Test
    @Order(1)
    void merchantWithoutTenantRejected() throws Exception {
        ResponseEntity<String> resp = rest.exchange("/api/admin/account", HttpMethod.POST,
                new HttpEntity<>(Map.of("username", "mch_no_tenant", "password", "password123",
                        "nickname", "无租户商家", "roleCode", "MERCHANT"), machine()), String.class);
        assertEquals(200, resp.getStatusCode().value());
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertEquals(40080, body.get("code").asInt(), "商家账号不带租户应被拒");
    }

    @Test
    @Order(2)
    void merchantMeReturnsOnlyRepairPermission() throws Exception {
        ResponseEntity<String> me = get("/api/admin/auth/me", merchantToken);
        JsonNode data = objectMapper.readTree(me.getBody()).path("data");
        assertEquals("MERCHANT", data.path("roleCode").asText());
        JsonNode perms = data.path("permissions");
        assertEquals(1, perms.size(), "商家应仅持 repair:manage 一个权限点");
        assertEquals("repair:manage", perms.get(0).asText());
    }

    @Test
    @Order(3)
    void merchantSeesOnlyOwnTenantRepairOrders() throws Exception {
        ResponseEntity<String> resp = get("/api/admin/repair/orders?pageNum=1&pageSize=20", merchantToken);
        assertEquals(200, resp.getStatusCode().value());
        JsonNode data = objectMapper.readTree(resp.getBody()).path("data");
        assertEquals(1, data.path("total").asLong(), "商家只能看到本租户维修单");
        assertEquals("F-MCH-T7", data.path("records").get(0).path("orderNo").asText());
    }

    @Test
    @Order(4)
    void merchantDeniedOtherModules() throws Exception {
        for (String uri : new String[] {
                "/api/admin/recycle/orders?pageNum=1&pageSize=5",
                "/api/admin/users?pageNum=1&pageSize=5",
                "/api/admin/roles"}) {
            ResponseEntity<String> resp = get(uri, merchantToken);
            assertEquals(403, resp.getStatusCode().value(), uri + " 应拒绝商家角色");
            assertEquals(40300, objectMapper.readTree(resp.getBody()).get("code").asInt());
        }
    }
}
