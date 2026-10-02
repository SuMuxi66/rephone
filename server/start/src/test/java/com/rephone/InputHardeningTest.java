package com.rephone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

/**
 * 安全补测 SEC-4 验收：客户端输入错误必须落 400xx 语义（缺参 40000 / 类型不匹配 40001 /
 * 请求体不可读 40002），不得与真实服务故障混同返回 500「服务繁忙」。
 * SEC-5 验收（见 oversizedBody）：超过上限的请求体必须被拒绝，不得进入业务逻辑。
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
class InputHardeningTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    private ResponseEntity<String> machineGet(String uri) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth("test-admin-token");
        return rest.exchange(uri, HttpMethod.GET, new HttpEntity<>(h), String.class);
    }

    @Test
    void missingParamReturns40000_not500() throws Exception {
        // /api/admin/models 必填 brandId，缺参此前落 500「服务繁忙」
        ResponseEntity<String> resp = machineGet("/api/admin/models?pageNum=1&pageSize=5");
        assertEquals(200, resp.getStatusCode().value());
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertEquals(40000, body.get("code").asInt());
        assertFalse(body.get("message").asText().contains("服务繁忙"));
    }

    @Test
    void typeMismatchReturns40001_not500() throws Exception {
        // trend 的 days 是 int 型，字符串注入此前落 500
        ResponseEntity<String> resp = machineGet("/api/admin/dashboard/trend?days=%27%20OR%20%271%27%3D%271");
        assertEquals(200, resp.getStatusCode().value());
        assertEquals(40001, objectMapper.readTree(resp.getBody()).get("code").asInt());
    }

    @Test
    void malformedJsonReturns40002_not500() throws Exception {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> resp = rest.exchange("/api/admin/auth/login", HttpMethod.POST,
                new HttpEntity<>("{invalid json", h), String.class);
        assertEquals(200, resp.getStatusCode().value());
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertEquals(40002, body.get("code").asInt());
        assertFalse(body.get("message").asText().contains("服务繁忙"));
    }

    @Test
    void oversizedBodyRejectedBeforeBusinessLogic() throws Exception {
        // SEC-5：1MB 硬上限，超限请求在过滤器层直接 413，不进入登录逻辑（也不计暴破计数）
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        StringBuilder big = new StringBuilder("{\"username\":\"").append("A".repeat(1024 * 1024 + 16))
                .append("\",\"password\":\"x\"}");
        ResponseEntity<String> resp = rest.exchange("/api/admin/auth/login", HttpMethod.POST,
                new HttpEntity<>(big.toString(), h), String.class);
        assertEquals(413, resp.getStatusCode().value());
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertEquals(41300, body.get("code").asInt());
    }
}
