package com.rephone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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
 * 管理端机型参数维护验收：spec 全量保存/回显、noSpec 过滤、校验拒绝、清空语义、创建带参数。
 * 独立 H2 库 + 静态 admin token + API 自种数据（见 AGENT.md 并行会话规范与 v1.0.1 共享库教训）。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:mspec;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/schema-h2.sql,classpath:db/schema-quote.sql,classpath:db/schema-recycle.sql,classpath:db/schema-repair.sql",
        "rephone.wx.mock-login=true",
        "rephone.jwt.ttl-seconds=3600",
        "rephone.admin.token=test-admin-token",
        "rephone.admin.bootstrap-password=test-admin-123"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminModelSpecTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    private HttpHeaders admin;
    private Long modelId;

    @BeforeAll
    void init() {
        admin = new HttpHeaders();
        admin.setBearerAuth("test-admin-token");
    }

    private JsonNode post(String url, Object body) throws Exception {
        ResponseEntity<String> resp = rest.postForEntity(url, new HttpEntity<>(body, admin), String.class);
        return objectMapper.readTree(resp.getBody());
    }

    private JsonNode put(String url, Object body) throws Exception {
        ResponseEntity<String> resp = rest.exchange(url, HttpMethod.PUT, new HttpEntity<>(body, admin), String.class);
        return objectMapper.readTree(resp.getBody());
    }

    private JsonNode listModels(Boolean noSpec) throws Exception {
        String url = "/api/admin/models?brandId=3&pageNum=1&pageSize=50" + (noSpec ? "&noSpec=true" : "");
        ResponseEntity<String> resp = rest.exchange(url, HttpMethod.GET, new HttpEntity<>(admin), String.class);
        return objectMapper.readTree(resp.getBody());
    }

    private boolean pageContains(JsonNode page, String name) {
        for (JsonNode n : page.path("data").path("records")) {
            if (name.equals(n.path("name").asText())) {
                return true;
            }
        }
        return false;
    }

    @Test
    @Order(1)
    void create_without_spec_then_missing_shows_in_noSpec_filter() throws Exception {
        JsonNode created = post("/api/admin/model", Map.of(
                "brandId", 3,
                "name", "参数测试机",
                "releaseYear", 2025,
                "prices", List.of(Map.of("storage", "256GB", "priceYuan", new BigDecimal("1999")))));
        assertEquals(0, created.path("code").asInt(), created.toString());
        modelId = created.path("data").path("modelId").asLong();

        JsonNode page = listModels(false);
        assertTrue(pageContains(page, "参数测试机"));
        // 新建机型 spec 全空（chip 为 null）
        for (JsonNode n : page.path("data").path("records")) {
            if ("参数测试机".equals(n.path("name").asText())) {
                assertTrue(n.path("spec").isNull() || n.path("spec").path("chip").isNull());
            }
        }

        JsonNode missing = listModels(true);
        assertTrue(pageContains(missing, "参数测试机"), "未填参数的机型应出现在 noSpec 过滤里");
    }

    @Test
    @Order(2)
    void save_spec_then_visible_and_filtered_out() throws Exception {
        Map<String, Object> spec = new java.util.HashMap<>();
        spec.put("releaseMonth", 10);
        spec.put("series", "数字系列");
        spec.put("chip", "骁龙8 Gen3");
        spec.put("ram", "12GB/16GB");
        spec.put("storage", "256GB/512GB");
        spec.put("batteryMah", 5300);
        spec.put("chargeW", 90);
        spec.put("wirelessW", 50);
        spec.put("screenSize", "6.36英寸");
        spec.put("resolution", "2670×1200");
        spec.put("refreshRate", "1-120");
        spec.put("rearCamera", "5000万主摄");
        JsonNode saved = put("/api/admin/model/" + modelId + "/spec", spec);
        assertEquals(0, saved.path("code").asInt(), saved.toString());

        JsonNode page = listModels(false);
        for (JsonNode n : page.path("data").path("records")) {
            if ("参数测试机".equals(n.path("name").asText())) {
                assertEquals("骁龙8 Gen3", n.path("spec").path("chip").asText());
                assertEquals(5300, n.path("spec").path("batteryMah").asInt());
                assertEquals("6.36英寸", n.path("spec").path("screenSize").asText());
            }
        }
        JsonNode missing = listModels(true);
        assertFalse(pageContains(missing, "参数测试机"), "已填参数的机型不应再出现在 noSpec 过滤里");
    }

    @Test
    @Order(3)
    void invalid_ranges_rejected() throws Exception {
        assertEquals(40054, put("/api/admin/model/" + modelId + "/spec",
                Map.of("batteryMah", 99999)).path("code").asInt());
        assertEquals(40054, put("/api/admin/model/" + modelId + "/spec",
                Map.of("chargeW", -1)).path("code").asInt());
        assertEquals(40054, put("/api/admin/model/" + modelId + "/spec",
                Map.of("releaseMonth", 13)).path("code").asInt());
        // 字符串超长（DB chip VARCHAR(128)）
        assertEquals(40039, put("/api/admin/model/" + modelId + "/spec",
                Map.of("chip", "超".repeat(129))).path("code").asInt());
    }

    @Test
    @Order(4)
    void null_clears_field() throws Exception {
        Map<String, Object> partial = new java.util.HashMap<>();
        partial.put("chip", "骁龙8 Gen3");
        partial.put("batteryMah", 5300);
        // 显式传 null 的字段应被清空（updateById 忽略 null 的坑由显式 set 兜住）
        for (String f : List.of("series", "ram", "storage", "chargeW", "wirelessW",
                "screenSize", "resolution", "refreshRate", "rearCamera", "releaseMonth")) {
            partial.put(f, null);
        }
        JsonNode saved = put("/api/admin/model/" + modelId + "/spec", partial);
        assertEquals(0, saved.path("code").asInt(), saved.toString());

        JsonNode page = listModels(false);
        for (JsonNode n : page.path("data").path("records")) {
            if ("参数测试机".equals(n.path("name").asText())) {
                assertEquals("骁龙8 Gen3", n.path("spec").path("chip").asText(), "未传 null 的字段保留");
                assertTrue(n.path("spec").path("ram").isNull(), "传 null 的字段应被清空");
                assertTrue(n.path("spec").path("screenSize").isNull(), "清空字段应写 null 而非静默保留");
            }
        }
    }

    @Test
    @Order(5)
    void create_with_spec_populates_immediately() throws Exception {
        JsonNode created = post("/api/admin/model", Map.of(
                "brandId", 3,
                "name", "参数测试机2",
                "releaseYear", 2026,
                "spec", Map.of("chip", "天玑9400+", "batteryMah", 6000),
                "prices", List.of(Map.of("storage", "512GB", "priceYuan", new BigDecimal("2999")))));
        assertEquals(0, created.path("code").asInt(), created.toString());
        long id2 = created.path("data").path("modelId").asLong();

        JsonNode page = listModels(false);
        boolean found = false;
        for (JsonNode n : page.path("data").path("records")) {
            if ("参数测试机2".equals(n.path("name").asText())) {
                found = true;
                assertEquals("天玑9400+", n.path("spec").path("chip").asText());
                assertEquals(6000, n.path("spec").path("batteryMah").asInt());
            }
        }
        assertTrue(found);
        // 清理：不该出现在 noSpec 里
        assertFalse(pageContains(listModels(true), "参数测试机2"));
        assertTrue(id2 > 0);
    }
}
