package com.rephone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rephone.mapper.ClickEventMapper;
import com.rephone.mapper.GoodsMapper;
import com.rephone.mapper.SaleOrderMapper;
import com.rephone.mapper.UserMapper;
import com.rephone.pojo.entity.ClickEvent;
import com.rephone.pojo.entity.Goods;
import com.rephone.pojo.entity.SaleOrder;
import com.rephone.pojo.entity.User;
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
 * 用户行为画像验收：卡片分页（活跃度/偏好/消费力）、昵称手机号过滤、明细分布与订单数、
 * profile:read 权限点 RBAC。独立命名 H2 库 + 自种数据。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:userprofile;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/schema-h2.sql,classpath:db/schema-role.sql,classpath:db/schema-recycle.sql,classpath:db/schema-repair.sql,classpath:db/schema-sale.sql,classpath:db/schema-click-event.sql,classpath:db/schema-user-ai-profile.sql",
        "rephone.wx.mock-login=true",
        "rephone.jwt.ttl-seconds=3600",
        "rephone.admin.token=test-admin-token"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserProfileTest {

    private static final String MARKER = "UP";

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClickEventMapper clickEventMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private SaleOrderMapper saleOrderMapper;

    @Autowired
    private UserMapper userMapper;

    private String adminHeaders() {
        return "test-admin-token";
    }

    private String uniqueSuffix() {
        return MARKER + UUID.randomUUID().toString().substring(0, 8);
    }

    private Long user1;
    private Long user2;
    private Long goodsId;

    @Test
    @Order(1)
    void loginTwoUsersAndSeedEvents() throws Exception {
        user1 = mockLogin("up-code-1");
        user2 = mockLogin("up-code-2");

        Goods goods = new Goods();
        goods.setTenantId(0L);
        goods.setName("画像测试商品" + uniqueSuffix());
        goods.setPriceFen(150000L);
        goods.setStock(5);
        goods.setStatus(1);
        goods.setBrand("Apple");
        goodsMapper.insert(goods);
        goodsId = goods.getId();

        LocalDateTime base = LocalDateTime.now().minusHours(3);
        // 用户1：启动 2 次；浏览 3 次（Apple x2 + Xiaomi x1，价格跨三个档）
        event(user1, ClickEvent.TYPE_APP_OPEN, 0L, base);
        event(user1, ClickEvent.TYPE_APP_OPEN, 0L, base.plusHours(1));
        event(user1, ClickEvent.TYPE_GOODS_VIEW, goodsId, base, "Apple", 150000L);
        event(user1, ClickEvent.TYPE_GOODS_VIEW, goodsId, base.plusMinutes(10), "Apple", 250000L);
        event(user1, ClickEvent.TYPE_GOODS_VIEW, goodsId + 1, base.plusMinutes(20), "Xiaomi", 400000L);
        // 用户2：仅启动 1 次
        event(user2, ClickEvent.TYPE_APP_OPEN, 0L, base);

        // 便于关键词过滤断言
        User u1 = userMapper.selectById(user1);
        u1.setNickname("画像用户甲" + uniqueSuffix());
        u1.setPhone("13900001111");
        userMapper.updateById(u1);
        User u2 = userMapper.selectById(user2);
        u2.setNickname("画像用户乙" + uniqueSuffix());
        u2.setPhone("13900002222");
        userMapper.updateById(u2);

        SaleOrder sale = new SaleOrder();
        sale.setTenantId(0L);
        sale.setOrderNo("S" + uniqueSuffix());
        sale.setUserId(user1);
        sale.setOpenid("up-openid");
        sale.setGoodsId(goodsId);
        sale.setGoodsName(goods.getName());
        sale.setPriceFen(150000L);
        sale.setQuantity(1);
        sale.setTotalFen(150000L);
        sale.setStatus(SaleOrder.STATUS_PAID);
        sale.setReceiverName("测试");
        sale.setReceiverPhone("13800000000");
        sale.setReceiverAddr("测试地址");
        saleOrderMapper.insert(sale);
    }

    @Test
    @Order(2)
    void pageAggregatesPreferenceAndPurchasingPower() throws Exception {
        ResponseEntity<String> resp = machineGet("/api/admin/user-profile/page?pageNum=1&pageSize=20");
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertEquals(0, body.get("code").asInt());
        JsonNode page = body.path("data");
        assertTrue(page.path("total").asLong() >= 2);

        JsonNode card1 = findCard(page.path("records"), user1);
        assertEquals(2, card1.path("openCount").asLong());
        assertEquals(3, card1.path("viewCount").asLong());
        assertEquals("Apple", card1.path("topBrand").asText());
        assertEquals(266666L, card1.path("avgPriceFen").asLong());
        assertEquals("1千-2千", card1.path("topPriceBand").asText());
        assertNotEquals("", card1.path("lastOpenTime").asText());
        assertTrue(findCard(page.path("records"), user2).path("openCount").asLong() == 1);
    }

    @Test
    @Order(3)
    void pageFiltersByNicknameAndPhone() throws Exception {
        User u2 = userMapper.selectById(user2);
        ResponseEntity<String> byNickname = machineGet(
                "/api/admin/user-profile/page?pageNum=1&pageSize=20&keyword=" + u2.getNickname());
        JsonNode records = objectMapper.readTree(byNickname.getBody()).path("data").path("records");
        assertEquals(1, records.size());
        assertEquals(user2.longValue(), records.get(0).path("userId").asLong());

        ResponseEntity<String> byPhone = machineGet(
                "/api/admin/user-profile/page?pageNum=1&pageSize=20&keyword=13900001111");
        assertEquals(1, objectMapper.readTree(byPhone.getBody()).path("data").path("records").size());
    }

    @Test
    @Order(4)
    void detailReturnsTimelineViewsDistsAndOrders() throws Exception {
        ResponseEntity<String> resp = machineGet("/api/admin/user-profile/" + user1 + "/detail");
        JsonNode data = objectMapper.readTree(resp.getBody()).path("data");
        assertEquals(2, data.path("openTimes").size());
        assertEquals(3, data.path("views").size());
        assertEquals(2, data.path("brandDist").path("Apple").asLong());
        assertEquals(1, data.path("brandDist").path("Xiaomi").asLong());
        assertEquals(1, data.path("priceBandDist").path("1千-2千").asLong());
        assertEquals(1, data.path("priceBandDist").path("2千-3.5千").asLong());
        assertEquals(1, data.path("priceBandDist").path("3.5千以上").asLong());
        assertEquals(1, data.path("saleOrders").asLong());
        assertEquals(0, data.path("recycleOrders").asLong());
        assertEquals(0, data.path("repairOrders").asLong());
    }

    @Test
    @Order(5)
    void rbacBlocksRolesWithoutProfileRead() throws Exception {
        String suffix = uniqueSuffix();
        HttpHeaders machine = new HttpHeaders();
        machine.setBearerAuth(adminHeaders());
        rest.exchange("/api/admin/account", HttpMethod.POST,
                new HttpEntity<>(Map.of("username", "up_fin_" + suffix, "password", "password123",
                        "nickname", "画像财务", "roleCode", "FINANCE"), machine), String.class);
        ResponseEntity<String> login = rest.postForEntity("/api/admin/auth/login",
                Map.of("username", "up_fin_" + suffix, "password", "password123"), String.class);
        String finToken = objectMapper.readTree(login.getBody()).path("data").path("token").asText();
        HttpHeaders fin = new HttpHeaders();
        fin.setBearerAuth(finToken);
        ResponseEntity<String> denied = rest.exchange("/api/admin/user-profile/page?pageNum=1&pageSize=20",
                HttpMethod.GET, new HttpEntity<>(fin), String.class);
        assertEquals(40300, objectMapper.readTree(denied.getBody()).get("code").asInt());
    }

    // ===== 内部 =====

    private Long mockLogin(String code) throws Exception {
        ResponseEntity<String> resp = rest.postForEntity("/api/wx/login", Map.of("code", code), String.class);
        JsonNode data = objectMapper.readTree(resp.getBody()).path("data");
        assertNotEquals("", data.path("token").asText());
        return data.path("userId").asLong();
    }

    private ResponseEntity<String> machineGet(String uri) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(adminHeaders());
        return rest.exchange(uri, HttpMethod.GET, new HttpEntity<>(h), String.class);
    }

    private JsonNode findCard(JsonNode records, Long userId) {
        for (JsonNode row : records) {
            if (row.path("userId").asLong() == userId) {
                return row;
            }
        }
        throw new AssertionError("card not found for user " + userId);
    }

    private void event(Long userId, int eventType, Long targetId, LocalDateTime createTime) {
        event(userId, eventType, targetId, createTime, null, null);
    }

    private void event(Long userId, int eventType, Long targetId, LocalDateTime createTime,
                       String brand, Long priceFen) {
        ClickEvent e = new ClickEvent();
        e.setTenantId(0L);
        e.setUserId(userId);
        e.setEventType(eventType);
        e.setTargetType(targetId == null || targetId == 0L ? ClickEvent.TARGET_NONE : ClickEvent.TARGET_GOODS);
        e.setTargetId(targetId == null ? 0L : targetId);
        e.setBrand(brand);
        e.setPriceFen(priceFen);
        e.setCreateTime(createTime);
        clickEventMapper.insert(e);
    }
}
