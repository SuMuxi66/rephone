package com.rephone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rephone.mapper.ClickEventMapper;
import com.rephone.mapper.GoodsMapper;
import com.rephone.mapper.PhoneModelMapper;
import com.rephone.mapper.RecycleOrderMapper;
import com.rephone.mapper.RepairOrderMapper;
import com.rephone.mapper.SaleOrderMapper;
import com.rephone.pojo.entity.ClickEvent;
import com.rephone.pojo.entity.Goods;
import com.rephone.pojo.entity.PhoneModel;
import com.rephone.pojo.entity.RecycleOrder;
import com.rephone.pojo.entity.RepairOrder;
import com.rephone.pojo.entity.SaleOrder;
import java.time.LocalDate;
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
 * 点击漏斗统计验收：summary 三窗口计数与 CTR、trend 按天分桶、top 商品/机型榜单与转化、
 * stats:click 权限点 RBAC（无权限角色 40300）。独立命名 H2 库 + 自种数据。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:clickstats;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/schema-h2.sql,classpath:db/schema-role.sql,classpath:db/schema-quote.sql,classpath:db/schema-recycle.sql,classpath:db/schema-repair.sql,classpath:db/schema-sale.sql,classpath:db/schema-click-event.sql",
        "rephone.wx.mock-login=true",
        "rephone.jwt.ttl-seconds=3600",
        "rephone.admin.token=test-admin-token"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ClickStatsTest {

    private static final String MARKER = "CS";

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClickEventMapper clickEventMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private PhoneModelMapper phoneModelMapper;

    @Autowired
    private SaleOrderMapper saleOrderMapper;

    @Autowired
    private RecycleOrderMapper recycleOrderMapper;

    @Autowired
    private RepairOrderMapper repairOrderMapper;

    private ResponseEntity<String> machineGet(String uri) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth("test-admin-token");
        return rest.exchange(uri, HttpMethod.GET, new HttpEntity<>(h), String.class);
    }

    private String uniqueSuffix() {
        return MARKER + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    @Order(1)
    void seedEventsAndOrders() {
        Goods goods = new Goods();
        goods.setTenantId(0L);
        goods.setName("点击统计测试商品" + uniqueSuffix());
        goods.setPriceFen(199900L);
        goods.setStock(10);
        goods.setStatus(1);
        goods.setBrand("Apple");
        goodsMapper.insert(goods);
        Long goodsId = goods.getId();

        PhoneModel model = new PhoneModel();
        model.setTenantId(0L);
        model.setBrandId(1L);
        model.setName("测试机型" + uniqueSuffix());
        model.setSort(0);
        phoneModelMapper.insert(model);
        Long modelId = model.getId();

        LocalDateTime yesterday = LocalDate.now().minusDays(1).atTime(12, 0);
        LocalDateTime today = LocalDate.now().atTime(9, 0);
        // 昨日：启动 1 / 曝光 1 / 浏览 1；今日：启动 1 / 曝光 2 / 浏览 1 / 机型点击 1
        event(ClickEvent.TYPE_APP_OPEN, 0L, yesterday, null, null);
        event(ClickEvent.TYPE_GOODS_EXPOSE, goodsId, yesterday, "Apple", 199900L);
        event(ClickEvent.TYPE_GOODS_VIEW, goodsId, yesterday, "Apple", 199900L);
        event(ClickEvent.TYPE_APP_OPEN, 0L, today, null, null);
        event(ClickEvent.TYPE_GOODS_EXPOSE, goodsId, today, "Apple", 199900L);
        event(ClickEvent.TYPE_GOODS_EXPOSE, goodsId, today, "Apple", 199900L);
        event(ClickEvent.TYPE_GOODS_VIEW, goodsId, today, "Apple", 199900L);
        event(ClickEvent.TYPE_MODEL_CLICK, modelId, today, null, null);

        SaleOrder sale = new SaleOrder();
        sale.setTenantId(0L);
        sale.setOrderNo("S" + uniqueSuffix());
        sale.setUserId(88001L);
        sale.setOpenid("cs-openid");
        sale.setGoodsId(goodsId);
        sale.setGoodsName(goods.getName());
        sale.setPriceFen(199900L);
        sale.setQuantity(1);
        sale.setTotalFen(199900L);
        sale.setStatus(SaleOrder.STATUS_PAID);
        sale.setReceiverName("测试");
        sale.setReceiverPhone("13800000000");
        sale.setReceiverAddr("测试地址");
        saleOrderMapper.insert(sale);

        RecycleOrder recycle = new RecycleOrder();
        recycle.setTenantId(0L);
        recycle.setOrderNo("R" + uniqueSuffix());
        recycle.setUserId(88001L);
        recycle.setOpenid("cs-openid");
        recycle.setModelId(modelId);
        recycle.setBrandName("Apple");
        recycle.setModelName(model.getName());
        recycle.setStorage("256GB");
        recycle.setConditionKey("99new");
        recycle.setConditionLabel("99新");
        recycle.setQuoteFen(500000L);
        recycle.setStatus(10);
        recycle.setPickupType(10);
        recycleOrderMapper.insert(recycle);

        RepairOrder repair = new RepairOrder();
        repair.setTenantId(0L);
        repair.setOrderNo("F" + uniqueSuffix());
        repair.setUserId(88001L);
        repair.setOpenid("cs-openid");
        repair.setModelId(modelId);
        repair.setBrandName("Apple");
        repair.setModelName(model.getName());
        repair.setItemsJson("[]");
        repair.setTotalFen(15000L);
        repair.setStatus(10);
        repair.setServiceType(10);
        repair.setContactName("测试");
        repair.setContactPhone("13800000000");
        repair.setAddress("测试地址");
        repair.setAppointTime("明天 上午");
        repair.setWarrantyDays(180);
        repairOrderMapper.insert(repair);
    }

    @Test
    @Order(2)
    void summaryCountsWindowsAndCtr() throws Exception {
        ResponseEntity<String> resp = machineGet("/api/admin/stats/click/summary");
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertEquals(0, body.get("code").asInt());
        JsonNode data = body.path("data");
        JsonNode last7 = data.path("last7");
        assertEquals(2, last7.path("appOpens").asLong());
        assertEquals(3, last7.path("exposes").asLong());
        assertEquals(2, last7.path("clicks").asLong());
        assertEquals(0.6667, last7.path("ctr").asDouble(), 0.0001);
        assertEquals(1, last7.path("saleOrders").asLong());
        assertEquals(1, last7.path("recycleOrders").asLong());
        assertEquals(1, last7.path("repairOrders").asLong());
        JsonNode today = data.path("today");
        assertEquals(1, today.path("appOpens").asLong());
        assertEquals(2, today.path("exposes").asLong());
        assertEquals(1, today.path("clicks").asLong());
        assertEquals(0.5, today.path("ctr").asDouble(), 0.0001);
    }

    @Test
    @Order(3)
    void trendBucketsByDay() throws Exception {
        ResponseEntity<String> resp = machineGet("/api/admin/stats/click/trend?days=3");
        JsonNode data = objectMapper.readTree(resp.getBody()).path("data");
        assertEquals(3, data.size());
        JsonNode todayRow = data.get(data.size() - 1);
        assertEquals(LocalDate.now().toString(), todayRow.path("date").asText());
        assertEquals(1, todayRow.path("appOpens").asLong());
        assertEquals(2, todayRow.path("exposes").asLong());
        assertEquals(1, todayRow.path("clicks").asLong());
    }

    @Test
    @Order(4)
    void topRanksGoodsAndModelsWithConversion() throws Exception {
        ResponseEntity<String> resp = machineGet("/api/admin/stats/click/top?days=30&limit=10");
        JsonNode data = objectMapper.readTree(resp.getBody()).path("data");
        JsonNode goods = data.path("goods");
        boolean goodsHit = false;
        for (JsonNode row : goods) {
            if (row.path("clicks").asLong() == 2) {
                goodsHit = true;
                assertEquals(3, row.path("exposes").asLong());
                assertEquals(0.6667, row.path("ctr").asDouble(), 0.0001);
                assertEquals(1, row.path("orders").asLong());
                assertTrue(row.path("name").asText().contains("点击统计测试商品"));
            }
        }
        assertTrue(goodsHit);
        JsonNode models = data.path("models");
        boolean modelHit = false;
        for (JsonNode row : models) {
            if (row.path("clicks").asLong() == 1) {
                modelHit = true;
                assertEquals(1, row.path("recycleOrders").asLong());
                assertEquals(1, row.path("repairOrders").asLong());
            }
        }
        assertTrue(modelHit);
    }

    @Test
    @Order(5)
    void rbacGrantsStatsClickOnlyToWhitelistedRoles() throws Exception {
        String suffix = uniqueSuffix();
        HttpHeaders machine = new HttpHeaders();
        machine.setBearerAuth("test-admin-token");
        rest.exchange("/api/admin/account", HttpMethod.POST,
                new HttpEntity<>(Map.of("username", "cs_ops_" + suffix, "password", "password123",
                        "nickname", "点击统计运营", "roleCode", "OPERATOR"), machine), String.class);
        ResponseEntity<String> opsLogin = rest.postForEntity("/api/admin/auth/login",
                Map.of("username", "cs_ops_" + suffix, "password", "password123"), String.class);
        String opsToken = objectMapper.readTree(opsLogin.getBody()).path("data").path("token").asText();
        HttpHeaders ops = new HttpHeaders();
        ops.setBearerAuth(opsToken);
        assertEquals(0, objectMapper.readTree(
                rest.exchange("/api/admin/stats/click/summary", HttpMethod.GET,
                        new HttpEntity<>(ops), String.class).getBody()).get("code").asInt());

        rest.exchange("/api/admin/account", HttpMethod.POST,
                new HttpEntity<>(Map.of("username", "cs_fin_" + suffix, "password", "password123",
                        "nickname", "财务无统计", "roleCode", "FINANCE"), machine), String.class);
        ResponseEntity<String> finLogin = rest.postForEntity("/api/admin/auth/login",
                Map.of("username", "cs_fin_" + suffix, "password", "password123"), String.class);
        String finToken = objectMapper.readTree(finLogin.getBody()).path("data").path("token").asText();
        HttpHeaders fin = new HttpHeaders();
        fin.setBearerAuth(finToken);
        assertEquals(40300, objectMapper.readTree(
                rest.exchange("/api/admin/stats/click/summary", HttpMethod.GET,
                        new HttpEntity<>(fin), String.class).getBody()).get("code").asInt());
    }

    private void event(int eventType, Long targetId, LocalDateTime createTime, String brand, Long priceFen) {
        ClickEvent e = new ClickEvent();
        e.setTenantId(0L);
        e.setUserId(88001L);
        e.setEventType(eventType);
        e.setTargetType(targetId == null ? ClickEvent.TARGET_NONE
                : eventType == ClickEvent.TYPE_MODEL_CLICK ? ClickEvent.TARGET_MODEL : ClickEvent.TARGET_GOODS);
        e.setTargetId(targetId == null ? 0L : targetId);
        e.setBrand(brand);
        e.setPriceFen(priceFen);
        e.setCreateTime(createTime);
        clickEventMapper.insert(e);
    }
}
