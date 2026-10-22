package com.rephone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rephone.mapper.ClickEventMapper;
import com.rephone.mapper.GoodsMapper;
import com.rephone.pojo.entity.ClickEvent;
import com.rephone.pojo.entity.Goods;
import java.util.List;
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
import org.springframework.test.context.TestPropertySource;

/**
 * 行为埋点上报验收：启动/商品曝光/商品浏览/机型点击四类事件落库、
 * 商品类事件写入品牌与价格快照、结构性校验拒绝、未登录 401。
 * 独立命名 H2 库 + 自种数据，避免污染共享库。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:clicktrack;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/schema-h2.sql,classpath:db/schema-sale.sql,classpath:db/schema-click-event.sql",
        "rephone.wx.mock-login=true",
        "rephone.jwt.ttl-seconds=3600"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClickTrackTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClickEventMapper clickEventMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    private String userToken;

    @Test
    @Order(1)
    void mockLoginGetsToken() throws Exception {
        ResponseEntity<String> resp = rest.postForEntity("/api/wx/login",
                Map.of("code", "ct-code-1"), String.class);
        JsonNode data = objectMapper.readTree(resp.getBody()).path("data");
        userToken = data.path("token").asText();
        assertNotEquals("", userToken);
    }

    @Test
    @Order(2)
    void reportFourEventTypesWithGoodsSnapshot() throws Exception {
        Goods goods = new Goods();
        goods.setTenantId(0L);
        goods.setName("测试商品-点击统计");
        goods.setPriceFen(199900L);
        goods.setStock(10);
        goods.setStatus(1);
        goods.setBrand("Apple");
        goods.setStorage("256G");
        goods.setConditionLevel("99新");
        goodsMapper.insert(goods);
        Long goodsId = goods.getId();

        Map<String, Object> payload = Map.of("events", List.of(
                Map.of("eventType", 1, "page", "app-launch"),
                Map.of("eventType", 3, "targetType", 1, "targetId", goodsId, "page", "sale-detail"),
                Map.of("eventType", 2, "targetType", 1, "targetIds", List.of(goodsId, goodsId + 1),
                        "page", "sale-list"),
                Map.of("eventType", 4, "targetType", 2, "targetId", 301L, "page", "model-picker:recycle")));
        ResponseEntity<String> resp = rest.exchange("/api/wx/track/events", HttpMethod.POST,
                new HttpEntity<>(payload, bearer()), String.class);
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertEquals(0, body.get("code").asInt());
        // 启动 1 行 + 浏览 1 行 + 曝光 2 行（每个商品一行）+ 机型 1 行
        assertEquals(5, body.path("data").path("accepted").asInt());

        List<ClickEvent> rows = clickEventMapper.selectList(null);
        assertEquals(5, rows.size());
        ClickEvent view = rows.stream().filter(r -> r.getEventType() == 3).findFirst().orElseThrow();
        assertEquals(ClickEvent.TARGET_GOODS, view.getTargetType());
        assertEquals(goodsId, view.getTargetId());
        assertEquals("Apple", view.getBrand());
        assertEquals(199900L, view.getPriceFen());
        ClickEvent expose = rows.stream().filter(r -> r.getEventType() == 2).findFirst().orElseThrow();
        assertTrue(expose.getTargetId() == goodsId || expose.getTargetId() == goodsId + 1);
        assertTrue(rows.stream().anyMatch(r -> r.getTargetId() == goodsId + 1));
        ClickEvent model = rows.stream().filter(r -> r.getEventType() == 4).findFirst().orElseThrow();
        assertEquals(301L, model.getTargetId());
    }

    @Test
    @Order(3)
    void rejectsInvalidEventType() throws Exception {
        Map<String, Object> payload = Map.of("events",
                List.of(Map.of("eventType", 99)));
        ResponseEntity<String> resp = rest.exchange("/api/wx/track/events", HttpMethod.POST,
                new HttpEntity<>(payload, bearer()), String.class);
        assertEquals(40095, objectMapper.readTree(resp.getBody()).get("code").asInt());
    }

    @Test
    @Order(4)
    void rejectsWhenNotLoggedIn() throws Exception {
        Map<String, Object> payload = Map.of("events",
                List.of(Map.of("eventType", 1)));
        ResponseEntity<String> resp = rest.exchange("/api/wx/track/events", HttpMethod.POST,
                new HttpEntity<>(payload), String.class);
        assertEquals(401, resp.getStatusCode().value());
    }

    private HttpHeaders bearer() {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(userToken);
        return h;
    }
}
