package com.rephone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * P1 验收标准自动化（AI_PLAN §4 P1）：
 * 1. 登录接口能签发 token；
 * 2. 携带 token 能访问 /api/wx/user/profile；
 * 3. user 表有记录；
 * 4. 无 token / 伪造 token 返回 401。
 * 运行于 H2 内存库（MODE=MySQL），无需本机 MySQL。
 * 注意：P2 测试与本病例共享同一 H2 库，user 行数断言一律用相对值。
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
        long userId = profile.path("data").path("userId").asLong();
        assertTrue(userId > 0, "profile 应返回 userId");

        // 3. user 表有记录（tenant_id 默认 0）；总行数断言用相对值（P2 测试共用同一 H2 库）
        Long tenantId = jdbcTemplate.queryForObject(
                "select tenant_id from `user` where id = ?", Long.class, userId);
        assertEquals(0L, tenantId);
        Integer countBefore = jdbcTemplate.queryForObject("select count(*) from `user`", Integer.class);

        // 4. 无 token → 401
        ResponseEntity<String> noAuth = rest.getForEntity("/api/wx/user/profile", String.class);
        assertEquals(401, noAuth.getStatusCode().value());

        // 5. 伪造 token → 401
        HttpHeaders badHeaders = new HttpHeaders();
        badHeaders.setBearerAuth(token.substring(0, token.length() - 2) + "xx");
        ResponseEntity<String> forged = rest.exchange("/api/wx/user/profile", HttpMethod.GET,
                new HttpEntity<>(badHeaders), String.class);
        assertEquals(401, forged.getStatusCode().value());

        // 6. 同一 code 再次登录不产生新用户（openid 稳定）
        rest.postForEntity("/api/wx/login", Map.of("code", "it-code-p1"), String.class);
        Integer countAfter = jdbcTemplate.queryForObject("select count(*) from `user`", Integer.class);
        assertEquals(countBefore, countAfter, "同一 code 重复登录不应新增用户");
    }

    @Test
    void avatarUpdateValidatesKeyAndEchoesInProfile() throws Exception {
        String token = objectMapper.readTree(rest.postForEntity("/api/wx/login",
                Map.of("code", "it-code-avatar"), String.class).getBody())
                .path("data").path("token").asText();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        // 1. 非法 key（目录穿越/非 COS key 形态）→ 40039
        ResponseEntity<String> evil = rest.exchange("/api/wx/user/profile", HttpMethod.PUT,
                new HttpEntity<>(Map.of("avatarUrl", "avatar/../../evil.jpg"), headers), String.class);
        assertEquals(40039, objectMapper.readTree(evil.getBody()).get("code").asInt());

        // 2. 合法 key 保存后，profile 返回原样回显（测试环境无 COS 密钥，signGetObject 原样返回）
        ResponseEntity<String> saved = rest.exchange("/api/wx/user/profile", HttpMethod.PUT,
                new HttpEntity<>(Map.of("avatarUrl", "avatar/2026-10-24/abc123.jpg"), headers), String.class);
        assertEquals(0, objectMapper.readTree(saved.getBody()).get("code").asInt());
        ResponseEntity<String> profile = rest.exchange("/api/wx/user/profile", HttpMethod.GET,
                new HttpEntity<>(headers), String.class);
        JsonNode data = objectMapper.readTree(profile.getBody()).path("data");
        assertEquals("avatar/2026-10-24/abc123.jpg", data.path("avatarUrl").asText());
        // phone 字段语义不应被头像更新污染（null/空或 11 位手机号，绝不能混入签名 URL）
        String phone = data.path("phone").asText("");
        assertTrue(phone.isEmpty() || phone.matches("1\\d{10}"),
                "phone 字段语义不应被头像更新污染");

        // 3. 空串=清空头像
        rest.exchange("/api/wx/user/profile", HttpMethod.PUT,
                new HttpEntity<>(Map.of("avatarUrl", ""), headers), String.class);
        ResponseEntity<String> cleared = rest.exchange("/api/wx/user/profile", HttpMethod.GET,
                new HttpEntity<>(headers), String.class);
        assertEquals("", objectMapper.readTree(cleared.getBody()).path("data").path("avatarUrl").asText());
    }

    @Test
    void localUploadFallbackStoresFileAndEchoesAsAvatar() throws Exception {
        String token = objectMapper.readTree(rest.postForEntity("/api/wx/login",
                Map.of("code", "it-code-local-avatar"), String.class).getBody())
                .path("data").path("token").asText();
        HttpHeaders uploadHeaders = new HttpHeaders();
        uploadHeaders.setBearerAuth(token);
        uploadHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        byte[] png = new byte[]{(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1a, '\n', 0, 0, 0, 0};
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", new ByteArrayResource(png) {
            @Override
            public String getFilename() {
                return "a.png";
            }
        });
        ResponseEntity<String> upload = rest.exchange("/api/wx/upload/image?dir=avatar", HttpMethod.POST,
                new HttpEntity<>(parts, uploadHeaders), String.class);
        assertEquals(0, objectMapper.readTree(upload.getBody()).get("code").asInt());
        String url = objectMapper.readTree(upload.getBody()).path("data").path("url").asText();
        assertTrue(url.matches("/img/avatar/\\d+_\\d+\\.png"), "本地兜底应返回 /img/** 访问路径: " + url);

        // 存为头像后 profile 原样回显（/img/** 静态路径不是 COS key，不得被签名改写）
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setBearerAuth(token);
        rest.exchange("/api/wx/user/profile", HttpMethod.PUT,
                new HttpEntity<>(Map.of("avatarUrl", url), authHeaders), String.class);
        ResponseEntity<String> profile = rest.exchange("/api/wx/user/profile", HttpMethod.GET,
                new HttpEntity<>(authHeaders), String.class);
        assertEquals(url, objectMapper.readTree(profile.getBody()).path("data").path("avatarUrl").asText());

        // 非白名单目录回落 avatar
        ResponseEntity<String> evilDir = rest.exchange("/api/wx/upload/image?dir=../evil", HttpMethod.POST,
                new HttpEntity<>(parts, uploadHeaders), String.class);
        assertEquals(0, objectMapper.readTree(evilDir.getBody()).get("code").asInt());
        String evilUrl = objectMapper.readTree(evilDir.getBody()).path("data").path("url").asText();
        assertTrue(evilUrl.startsWith("/img/avatar/"), "非法目录应回落 avatar: " + evilUrl);

        // 清理写入的测试文件（测试 CWD=server/start，勿留未跟踪产物）
        Files.deleteIfExists(Paths.get("data", "img", "avatar", url.substring(url.lastIndexOf('/') + 1)));
        Files.deleteIfExists(Paths.get("data", "img", "avatar", evilUrl.substring(evilUrl.lastIndexOf('/') + 1)));
    }
}
