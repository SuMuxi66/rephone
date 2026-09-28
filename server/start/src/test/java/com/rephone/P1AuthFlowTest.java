package com.rephone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
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
 * P1 验收标准自动化（AI_PLAN §4 P1）：
 * 1. 登录接口能签发 token；
 * 2. 携带 token 能访问 /api/wx/user/profile；
 * 3. user 表有记录；
 * 4. 无 token / 伪造 token 返回 401。
 * 运行于 H2 内存库（MODE=MySQL），无需本机 MySQL。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:rephone;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/schema-h2.sql",
        "rephone.wx.mock-login=true",
        "rephone.jwt.ttl-seconds=3600"
})
class P1AuthFlowTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void login_issueToken_thenProfile_thenUserRow_thenUnauthorized() throws Exception {
        // 1. 登录拿 token（mock-login 模式，code 稳定映射到固定 openid）
        ResponseEntity<String> loginResp = rest.postForEntity("/api/wx/login",
                Map.of("code", "it-code-p1"), String.class);
        assertEquals(200, loginResp.getStatusCode().value());
        JsonNode login = objectMapper.readTree(loginResp.getBody());
        assertEquals(0, login.get("code").asInt());
        String token = login.path("data").path("token").asText();
        assertFalse(token.isBlank(), "登录响应应包含 token");

        // 2. 携带 token 访问 profile
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<String> profileResp = rest.exchange("/api/wx/user/profile", HttpMethod.GET,
                new HttpEntity<>(headers), String.class);
        assertEquals(200, profileResp.getStatusCode().value());
        JsonNode profile = objectMapper.readTree(profileResp.getBody());
        assertEquals(0, profile.get("code").asInt());
        assertTrue(profile.path("data").path("userId").asLong() > 0, "profile 应返回 userId");

        // 3. user 表有记录（tenant_id 默认 0）
        Long tenantId = jdbcTemplate.queryForObject("select tenant_id from `user` limit 1", Long.class);
        assertEquals(0L, tenantId);
        Integer count = jdbcTemplate.queryForObject("select count(*) from `user`", Integer.class);
        assertEquals(1, count);

        // 4. 无 token → 401
        ResponseEntity<String> noAuth = rest.getForEntity("/api/wx/user/profile", String.class);
        assertEquals(401, noAuth.getStatusCode().value());

        // 5. 伪造 token → 401
        HttpHeaders badHeaders = new HttpHeaders();
        badHeaders.setBearerAuth(token.substring(0, token.length() - 2) + "xx");
        ResponseEntity<String> forged = rest.exchange("/api/wx/user/profile", HttpMethod.GET,
                new HttpEntity<>(badHeaders), String.class);
        assertEquals(401, forged.getStatusCode().value());

        // 6. 同一 code 再次登录不产生新用户
        rest.postForEntity("/api/wx/login", Map.of("code", "it-code-p1"), String.class);
        Integer countAgain = jdbcTemplate.queryForObject("select count(*) from `user`", Integer.class);
        assertEquals(1, countAgain);
    }
}
