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
 * P5 验收：账号制管理员登录（自种 admin/test-admin-123）、双凭证管理端鉴权
 * （ADMIN_TOKEN 与管理员 JWT）、/me 身份查询、管理端回收单详情与管理端 COS 签名。
 * 使用独立 H2 库并自种管理员，不依赖共享库残留、AdminBootstrapRunner 顺序或本机 .env。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:p5auth;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/schema-h2.sql,classpath:db/schema-quote.sql,classpath:db/schema-recycle.sql,classpath:db/schema-repair.sql",
        "rephone.wx.mock-login=true",
        "rephone.jwt.ttl-seconds=3600",
        "rephone.admin.token=test-admin-token",
        "rephone.admin.bootstrap-password=test-admin-123",
        "rephone.cos.mock=true"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class P5AdminAuthTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private String userToken;
    private String adminToken;

    @BeforeAll
    void loginUsers() throws Exception {
        // 自种平台管理员：不依赖 AdminBootstrapRunner 执行顺序与共享 H2 库/本机 .env 残留
        jdbcTemplate.update("delete from `user` where username = 'admin'");
        jdbcTemplate.update("insert into `user` (tenant_id, openid, nickname, role, status, username, password_hash) "
                        + "values (0, 'p5auth-admin', '平台管理员', 'ADMIN', 1, 'admin', ?)",
                encoder.encode("test-admin-123"));
        // 普通用户（wx mock 登录）
        ResponseEntity<String> resp = rest.postForEntity("/api/wx/login",
                Map.of("code", "p5-user"), String.class);
        userToken = objectMapper.readTree(resp.getBody()).path("data").path("token").asText();
        assertNotEquals("", userToken);
        // 管理员账号登录（bootstrap 种子）
        adminToken = adminLogin("test-admin-123");
    }

    private String adminLogin(String password) throws Exception {
        ResponseEntity<String> resp = rest.postForEntity("/api/admin/auth/login",
                Map.of("username", "admin", "password", password), String.class);
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertEquals(0, body.get("code").asInt());
        assertEquals("ADMIN", body.path("data").path("role").asText());
        return body.path("data").path("token").asText();
    }

    private HttpHeaders bearer(String token) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        return h;
    }

    @Test
    @Order(1)
    void login_success_wrong_password() throws Exception {
        assertNotEquals("", adminToken, "管理员应能登录并拿到 token");

        ResponseEntity<String> bad = rest.postForEntity("/api/admin/auth/login",
                Map.of("username", "admin", "password", "wrong-password"), String.class);
        assertEquals(40101, objectMapper.readTree(bad.getBody()).get("code").asInt(), "错密码应 40101");
    }

    @Test
    @Order(2)
    void user_jwt_rejected_admin_jwt_accepted() throws Exception {
        // 普通用户 JWT 访问管理端应 401
        ResponseEntity<String> forbidden = rest.exchange("/api/admin/repair/orders", HttpMethod.GET,
                new HttpEntity<>(bearer(userToken)), String.class);
        assertEquals(401, forbidden.getStatusCode().value(), "普通用户 JWT 不得进入管理端");

        // 管理员 JWT 可访问
        ResponseEntity<String> ok = rest.exchange("/api/admin/repair/orders", HttpMethod.GET,
                new HttpEntity<>(bearer(adminToken)), String.class);
        assertEquals(0, objectMapper.readTree(ok.getBody()).get("code").asInt());
    }

    @Test
    @Order(3)
    void machine_token_still_works() throws Exception {
        ResponseEntity<String> resp = rest.exchange("/api/admin/recycle/orders", HttpMethod.GET,
                new HttpEntity<>(bearer("test-admin-token")), String.class);
        assertEquals(0, objectMapper.readTree(resp.getBody()).get("code").asInt(), "ADMIN_TOKEN 机器凭证仍可用");
    }

    @Test
    @Order(4)
    void me_both_credentials() throws Exception {
        ResponseEntity<String> viaToken = rest.exchange("/api/admin/auth/me", HttpMethod.GET,
                new HttpEntity<>(bearer("test-admin-token")), String.class);
        JsonNode body1 = objectMapper.readTree(viaToken.getBody());
        assertEquals(0, body1.get("code").asInt());
        assertEquals("token", body1.path("data").path("authType").asText());

        ResponseEntity<String> viaJwt = rest.exchange("/api/admin/auth/me", HttpMethod.GET,
                new HttpEntity<>(bearer(adminToken)), String.class);
        JsonNode body2 = objectMapper.readTree(viaJwt.getBody());
        assertEquals(0, body2.get("code").asInt());
        assertEquals("account", body2.path("data").path("authType").asText());
        assertEquals("平台管理员", body2.path("data").path("nickname").asText());

        // 无凭证应 401
        ResponseEntity<String> anon = rest.getForEntity("/api/admin/auth/me", String.class);
        assertEquals(401, anon.getStatusCode().value());
    }

    @Test
    @Order(5)
    void admin_recycle_detail_and_cos_sign() throws Exception {
        // 用户端创建一单
        Map<String, Object> payload = Map.of(
                "modelId", 1, "storage", "128GB", "condition", "COND_95",
                "issues", List.of("SCREEN"), "quoteFen", 566000L, "pickupType", 10,
                "pickupName", "测试丙", "pickupPhone", "13500000005",
                "pickupAddress", "甘肃省兰州市城关区P5路1号", "remark", "p5");
        ResponseEntity<String> createResp = rest.exchange("/api/wx/recycle/order", HttpMethod.POST,
                new HttpEntity<>(payload, bearer(userToken)), String.class);
        String orderNo = objectMapper.readTree(createResp.getBody()).path("data").path("orderNo").asText();
        assertNotEquals("", orderNo);

        // 管理端详情（不做归属校验，凭证为管理员 JWT）
        ResponseEntity<String> detail = rest.exchange("/api/admin/recycle/order/" + orderNo, HttpMethod.GET,
                new HttpEntity<>(bearer(adminToken)), String.class);
        JsonNode detailBody = objectMapper.readTree(detail.getBody());
        assertEquals(0, detailBody.get("code").asInt());
        assertEquals(10, detailBody.path("data").path("status").asInt());
        assertTrue(detailBody.path("data").path("inspections").isArray(), "详情应含质检记录数组");

        // 管理端 COS 签名
        ResponseEntity<String> sign = rest.exchange("/api/admin/cos/upload-sign?ext=jpg", HttpMethod.GET,
                new HttpEntity<>(bearer(adminToken)), String.class);
        JsonNode signBody = objectMapper.readTree(sign.getBody());
        assertEquals(0, signBody.get("code").asInt());
        assertTrue(signBody.path("data").path("key").asText().startsWith("recycle/"), "key 应钉在 recycle/ 前缀");
    }
}
