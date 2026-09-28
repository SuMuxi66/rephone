package com.rephone;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
 * P2 验收（AI_PLAN §4 P2）：品牌/机型列表、估价计算、金额单位（元→分）。
 * 与 P1 测试共用 Spring 上下文与 H2 库；种子数据来自 schema-quote.sql（INSERT IGNORE）。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:rephone;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/schema-h2.sql,classpath:db/schema-quote.sql",
        "rephone.wx.mock-login=true",
        "rephone.jwt.ttl-seconds=3600"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class P2QuoteFlowTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeAll
    void login() throws Exception {
        ResponseEntity<String> resp = rest.postForEntity("/api/wx/login",
                Map.of("code", "p2-code"), String.class);
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertEquals(0, body.get("code").asInt());
        token = body.path("data").path("token").asText();
    }

    private HttpHeaders auth() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    @Test
    @Order(1)
    void brands_list_seeded() throws Exception {
        ResponseEntity<String> resp = rest.exchange("/api/wx/brands", HttpMethod.GET,
                new HttpEntity<>(auth()), String.class);
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertEquals(0, body.get("code").asInt());
        assertEquals(5, body.path("data").size(), "应返回 5 个种子品牌");
        assertEquals("Apple", body.path("data").get(0).path("name").asText());
    }

    @Test
    @Order(2)
    void models_by_brand_with_storages() throws Exception {
        ResponseEntity<String> resp = rest.exchange("/api/wx/models?brandId=1", HttpMethod.GET,
                new HttpEntity<>(auth()), String.class);
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertEquals(0, body.get("code").asInt());
        JsonNode models = body.path("data");
        assertEquals(6, models.size(), "Apple 应有 6 个机型");
        assertEquals(3, models.get(0).path("storages").size(), "每个机型应有 3 档内存");
    }

    @Test
    @Order(3)
    void quote_calculate_money_in_fen() throws Exception {
        // iPhone 15 Pro Max 128GB 基准 6800 元 × 95新(0.85) − 屏幕磕碰(120) = 5660 元 = 566000 分
        Map<String, Object> payload = Map.of(
                "modelId", 1,
                "storage", "128GB",
                "condition", "COND_95",
                "issues", List.of("SCREEN"));
        ResponseEntity<String> resp = rest.exchange("/api/wx/quote/calculate", HttpMethod.POST,
                new HttpEntity<>(payload, auth()), String.class);
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertEquals(0, body.get("code").asInt());
        assertEquals(566000L, body.path("data").path("priceFen").asLong(), "金额必须是分");
        assertEquals("iPhone 15 Pro Max", body.path("data").path("modelName").asText());
        assertEquals("95新", body.path("data").path("conditionLabel").asText());
    }

    @Test
    @Order(4)
    void quote_rejects_invalid_options_and_anonymous() throws Exception {
        Map<String, Object> badStorage = Map.of(
                "modelId", 1, "storage", "1TB", "condition", "COND_95", "issues", List.of());
        ResponseEntity<String> resp = rest.exchange("/api/wx/quote/calculate", HttpMethod.POST,
                new HttpEntity<>(badStorage, auth()), String.class);
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertTrue(body.get("code").asInt() != 0, "无效内存应报业务错误");

        ResponseEntity<String> noAuth = rest.getForEntity("/api/wx/brands", String.class);
        assertEquals(401, noAuth.getStatusCode().value(), "匿名访问应 401");
    }
}
