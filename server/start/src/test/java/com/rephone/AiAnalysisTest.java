package com.rephone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rephone.mapper.ClickEventMapper;
import com.rephone.mapper.GoodsMapper;
import com.rephone.mapper.UserAiProfileMapper;
import com.rephone.pojo.entity.ClickEvent;
import com.rephone.pojo.entity.Goods;
import java.time.LocalDateTime;
import java.util.List;
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
 * AI 画像分析验收：mock 模式生成、落库唯一（重复生成覆盖）、详情与卡片带 AI 内容。
 * 独立命名 H2 库 + 自种数据 + 钉 AI_MOCK=true（不依赖真实模型 key）。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:aiprofile;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/schema-h2.sql,classpath:db/schema-recycle.sql,classpath:db/schema-repair.sql,classpath:db/schema-sale.sql,classpath:db/schema-click-event.sql,classpath:db/schema-user-ai-profile.sql",
        "rephone.wx.mock-login=true",
        "rephone.jwt.ttl-seconds=3600",
        "rephone.admin.token=test-admin-token",
        "rephone.ai.mock=true"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AiAnalysisTest {

    private static final String MARKER = "AI";

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClickEventMapper clickEventMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private UserAiProfileMapper userAiProfileMapper;

    private Long userId;
    private Long goodsId;

    @Test
    @Order(1)
    void loginAndSeedBehavior() throws Exception {
        ResponseEntity<String> resp = rest.postForEntity("/api/wx/login",
                Map.of("code", MARKER.toLowerCase() + "-code-1"), String.class);
        userId = objectMapper.readTree(resp.getBody()).path("data").path("userId").asLong();
        assertNotEquals(0L, userId);

        Goods goods = new Goods();
        goods.setTenantId(0L);
        goods.setName("AI画像测试商品" + uniqueSuffix());
        goods.setPriceFen(299900L);
        goods.setStock(5);
        goods.setStatus(1);
        goods.setBrand("Huawei");
        goodsMapper.insert(goods);
        goodsId = goods.getId();

        LocalDateTime base = LocalDateTime.now().minusHours(2);
        event(ClickEvent.TYPE_APP_OPEN, 0L, base);
        event(ClickEvent.TYPE_GOODS_VIEW, goodsId, base.plusMinutes(5), "Huawei", 299900L);
        event(ClickEvent.TYPE_GOODS_VIEW, goodsId, base.plusMinutes(15), "Huawei", 299900L);
    }

    @Test
    @Order(2)
    void generateWithMockModel() throws Exception {
        ResponseEntity<String> resp = machinePost("/api/admin/user-profile/" + userId + "/ai-analysis");
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertEquals(0, body.get("code").asInt());
        JsonNode data = body.path("data");
        assertTrue(data.path("analysis").asText().contains("规则模板"));
        assertEquals("mock", data.path("model").asText());
        assertEquals(1, userAiProfileMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.rephone.pojo.entity.UserAiProfile>()
                        .eq("user_id", userId)));
    }

    @Test
    @Order(3)
    void detailAndCardCarryAiContent() throws Exception {
        ResponseEntity<String> detail = machineGet("/api/admin/user-profile/" + userId + "/detail");
        JsonNode data = objectMapper.readTree(detail.getBody()).path("data");
        assertTrue(data.path("aiAnalysis").asText().contains("规则模板"));
        assertEquals("mock", data.path("aiModel").asText());

        ResponseEntity<String> page = machineGet("/api/admin/user-profile/page?pageNum=1&pageSize=10");
        JsonNode records = objectMapper.readTree(page.getBody()).path("data").path("records");
        boolean hit = false;
        for (JsonNode row : records) {
            if (row.path("userId").asLong() == userId) {
                hit = true;
                assertTrue(row.path("aiSummary").asText().contains("规则模板"));
            }
        }
        assertTrue(hit);
    }

    @Test
    @Order(4)
    void regenerateOverwritesInsteadOfDuplicating() throws Exception {
        ResponseEntity<String> resp = machinePost("/api/admin/user-profile/" + userId + "/ai-analysis");
        assertEquals(0, objectMapper.readTree(resp.getBody()).get("code").asInt());
        assertEquals(1, userAiProfileMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.rephone.pojo.entity.UserAiProfile>()
                        .eq("user_id", userId)));
    }

    // ===== 内部 =====

    private String uniqueSuffix() {
        return MARKER + UUID.randomUUID().toString().substring(0, 8);
    }

    private ResponseEntity<String> machineGet(String uri) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth("test-admin-token");
        return rest.exchange(uri, HttpMethod.GET, new HttpEntity<>(h), String.class);
    }

    private ResponseEntity<String> machinePost(String uri) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth("test-admin-token");
        return rest.exchange(uri, HttpMethod.POST, new HttpEntity<>(h), String.class);
    }

    private void event(int eventType, Long targetId, LocalDateTime createTime) {
        event(eventType, targetId, createTime, null, null);
    }

    private void event(int eventType, Long targetId, LocalDateTime createTime, String brand, Long priceFen) {
        ClickEvent e = new ClickEvent();
        e.setTenantId(0L);
        e.setUserId(userId);
        e.setEventType(eventType);
        e.setTargetType(targetId == 0L ? ClickEvent.TARGET_NONE : ClickEvent.TARGET_GOODS);
        e.setTargetId(targetId);
        e.setBrand(brand);
        e.setPriceFen(priceFen);
        e.setCreateTime(createTime);
        clickEventMapper.insert(e);
    }
}
