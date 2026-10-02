package com.rephone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rephone.mapper.UserMapper;
import com.rephone.pojo.entity.User;
import java.util.Map;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;

/**
 * P7 RBAC 验收：机器凭证全权；角色登录准入；权限点拦截（无权限 40300）；
 * 禁用账号 token 立即失效；/me 下发角色与权限点。
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
class AdminRbacTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private HttpHeaders machine() {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth("test-admin-token");
        return h;
    }

    private Long operatorId;
    private String operatorToken;

    private long insertAdminUser(String roleCode, String username) {
        // H2 mem 库跨测试类复用，先清同用户名防唯一键冲突
        userMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        User user = new User();
        user.setTenantId(0L);
        user.setOpenid("admin-rbac-" + username);
        user.setUsername(username);
        user.setNickname("RBAC-" + roleCode);
        user.setRole(roleCode);
        user.setStatus(1);
        user.setGender(0);
        user.setAvatarUrl("");
        user.setPasswordHash(encoder.encode("password123"));
        userMapper.insert(user);
        return user.getId();
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        ResponseEntity<String> resp = rest.postForEntity("/api/admin/auth/login",
                Map.of("username", username, "password", password), String.class);
        return objectMapper.readTree(resp.getBody()).path("data").path("token").asText();
    }

    private ResponseEntity<String> get(String uri, String token) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token == null ? "test-admin-token" : token);
        return rest.exchange(uri, HttpMethod.GET, new HttpEntity<>(h), String.class);
    }

    @Test
    @Order(1)
    void machineTokenHasFullAccess() throws Exception {
        ResponseEntity<String> resp = get("/api/admin/users?pageNum=1&pageSize=5", null);
        assertEquals(200, resp.getStatusCode().value());
        assertEquals(0, objectMapper.readTree(resp.getBody()).get("code").asInt());
    }

    @Test
    @Order(2)
    void operatorCanLoginAndMeReturnsPermissions() throws Exception {
        operatorId = insertAdminUser("OPERATOR", "rbac_operator");
        operatorToken = loginAndGetToken("rbac_operator", "password123");
        assertTrue(!operatorToken.isEmpty());

        ResponseEntity<String> me = get("/api/admin/auth/me", operatorToken);
        JsonNode data = objectMapper.readTree(me.getBody()).path("data");
        assertEquals("OPERATOR", data.path("roleCode").asText());
        assertTrue(data.path("permissions").toString().contains("recycle:manage"));
    }

    @Test
    @Order(3)
    void operatorDeniedAccountManage() throws Exception {
        // 账号管理需要 account:manage，OPERATOR 无此权限
        ResponseEntity<String> resp = get("/api/admin/users?pageNum=1&pageSize=5", operatorToken);
        assertEquals(403, resp.getStatusCode().value());
        assertEquals(40300, objectMapper.readTree(resp.getBody()).get("code").asInt());
    }

    @Test
    @Order(4)
    void operatorAllowedRecycleManage() throws Exception {
        // recycle:manage 在 OPERATOR 权限集内；回收接口未建任何数据时也应通过鉴权（列表 200）
        ResponseEntity<String> resp = get("/api/admin/recycle/orders?pageNum=1&pageSize=5", operatorToken);
        assertEquals(200, resp.getStatusCode().value());
        assertEquals(0, objectMapper.readTree(resp.getBody()).get("code").asInt());
    }

    @Test
    @Order(5)
    void disabledAccountTokenRevokedImmediately() {
        rest.exchange("/api/admin/user/" + operatorId + "/status", HttpMethod.PUT,
                new HttpEntity<>(Map.of("status", 0), machine()), String.class);
        ResponseEntity<String> me = get("/api/admin/auth/me", operatorToken);
        assertEquals(401, me.getStatusCode().value());
    }

    @Test
    @Order(6)
    void financeRoleCannotReadRecycleList() throws Exception {
        insertAdminUser("FINANCE", "rbac_finance");
        String financeToken = loginAndGetToken("rbac_finance", "password123");
        // FINANCE 无 recycle:manage
        ResponseEntity<String> resp = get("/api/admin/recycle/orders?pageNum=1&pageSize=5", financeToken);
        assertEquals(40300, objectMapper.readTree(resp.getBody()).get("code").asInt());
    }

    @Test
    @Order(7)
    void superAdminAccountPassesEverything() throws Exception {
        long id = insertAdminUser("ADMIN", "rbac_super");
        assertTrue(id > 0);
        String token = loginAndGetToken("rbac_super", "password123");
        ResponseEntity<String> me = get("/api/admin/auth/me", token);
        assertTrue(objectMapper.readTree(me.getBody()).path("data").path("permissions").toString().contains("*"));
        assertEquals(0, objectMapper.readTree(get("/api/admin/users?pageNum=1&pageSize=5", token).getBody())
                .get("code").asInt());
    }
}
