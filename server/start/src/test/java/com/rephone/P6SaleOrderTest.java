package com.rephone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
 * P6 验收：商品上架 → 用户下单（服务端计价+扣库存）→ mock 支付 → 发货 → 收货；
 * 取消回补库存；售后申请 → 审核退款联动订单置 90；越权守卫。金额断言单位为分。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:rephone;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/schema-h2.sql,classpath:db/schema-quote.sql,classpath:db/schema-recycle.sql,classpath:db/schema-repair.sql,classpath:db/schema-sale.sql",
        "rephone.wx.mock-login=true",
        "rephone.jwt.ttl-seconds=3600",
        "rephone.admin.token=test-admin-token",
        "rephone.admin.bootstrap-password=test-admin-123"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class P6SaleOrderTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    private HttpHeaders admin;
    private String userToken;

    @BeforeAll
    void init() throws Exception {
        admin = new HttpHeaders();
        admin.setBearerAuth("test-admin-token");
        ResponseEntity<String> resp = rest.postForEntity("/api/wx/login",
                Map.of("code", "p6-user"), String.class);
        userToken = objectMapper.readTree(resp.getBody()).path("data").path("token").asText();
    }

    private HttpHeaders admin() {
        return admin;
    }

    private HttpHeaders user() {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(userToken);
        return h;
    }

    private Long createGoods(String name, String priceYuan, int stock) throws Exception {
        ResponseEntity<String> resp = rest.exchange("/api/admin/goods", HttpMethod.POST,
                new HttpEntity<>(Map.of("name", name, "priceYuan", new BigDecimal(priceYuan),
                        "stock", stock, "descText", "P6 测试商品"), admin()), String.class);
        assertEquals(0, objectMapper.readTree(resp.getBody()).get("code").asInt());
        return objectMapper.readTree(resp.getBody()).path("data").path("goodsId").asLong();
    }

    private String createOrder(Long goodsId, int qty) throws Exception {
        ResponseEntity<String> resp = rest.exchange("/api/wx/sale/order", HttpMethod.POST,
                new HttpEntity<>(Map.of("goodsId", goodsId, "quantity", qty,
                        "receiverName", "买家甲", "receiverPhone", "13800002222",
                        "receiverAddr", "甘肃省兰州市城关区P6路1号", "remark", "p6"), user()), String.class);
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertEquals(0, body.get("code").asInt());
        return body.path("data").path("orderNo").asText();
    }

    private JsonNode orderDetail(String orderNo, HttpHeaders who) throws Exception {
        ResponseEntity<String> resp = rest.exchange("/api/wx/sale/order/" + orderNo, HttpMethod.GET,
                new HttpEntity<>(who), String.class);
        return objectMapper.readTree(resp.getBody()).path("data");
    }

    @Test
    @Order(1)
    void goods_create_and_user_view() throws Exception {
        Long goodsId = createGoods("P6 测试机", "1999.00", 5);
        assertNotEquals(0L, goodsId);

        ResponseEntity<String> list = rest.exchange("/api/wx/goods", HttpMethod.GET,
                new HttpEntity<>(user()), String.class);
        JsonNode body = objectMapper.readTree(list.getBody());
        assertEquals(0, body.get("code").asInt());
        assertTrue(body.path("data").size() >= 1, "用户端应能看到上架商品");
        assertEquals(199900L, body.path("data").get(0).path("priceFen").asLong(), "价格应为分");
    }

    @Test
    @Order(2)
    void order_pay_ship_confirm_and_stock() throws Exception {
        Long goodsId = createGoods("P6 库存机", "500.00", 5);

        // 下单 2 件：服务端计价 100000 分，库存 5→3
        String orderNo = createOrder(goodsId, 2);
        assertTrue(orderNo.startsWith("S"), "订单号应 S 前缀");
        assertEquals(100000L, orderDetail(orderNo, user()).path("totalFen").asLong());

        // 取消回补库存
        rest.exchange("/api/wx/sale/order/" + orderNo + "/cancel", HttpMethod.PUT,
                new HttpEntity<>(Map.of("reason", "不买了"), user()), String.class);
        assertEquals(80, orderDetail(orderNo, user()).path("status").asInt());

        // 再下单支付发货收货
        String no2 = createOrder(goodsId, 1);
        ResponseEntity<String> pay = rest.exchange("/api/wx/sale/order/" + no2 + "/pay", HttpMethod.POST,
                new HttpEntity<>(user()), String.class);
        assertEquals(0, objectMapper.readTree(pay.getBody()).get("code").asInt());
        assertTrue(objectMapper.readTree(pay.getBody()).path("data").path("payNo").asText().startsWith("MOCKPAY-"));
        assertEquals(20, orderDetail(no2, user()).path("status").asInt());

        ResponseEntity<String> ship = rest.exchange("/api/admin/sale/order/" + no2 + "/ship",
                HttpMethod.PUT, new HttpEntity<>(Map.of("expressCompany", "顺丰速运",
                        "expressNo", "SF-P6-001", "remark", "p6"), admin()), String.class);
        assertEquals(0, objectMapper.readTree(ship.getBody()).get("code").asInt());
        assertEquals(30, orderDetail(no2, user()).path("status").asInt());

        ResponseEntity<String> confirm = rest.exchange("/api/wx/sale/order/" + no2 + "/confirm",
                HttpMethod.PUT, new HttpEntity<>(user()), String.class);
        assertEquals(0, objectMapper.readTree(confirm.getBody()).get("code").asInt());
        assertEquals(40, orderDetail(no2, user()).path("status").asInt());

        // 库存校验：5 - 2(取消回补) - 1 = 4
        ResponseEntity<String> goods = rest.exchange("/api/wx/goods/" + goodsId, HttpMethod.GET,
                new HttpEntity<>(user()), String.class);
        assertEquals(4, objectMapper.readTree(goods.getBody()).path("data").path("stock").asInt(),
                "取消应回补库存");
    }

    @Test
    @Order(3)
    void after_sale_refund_linkage() throws Exception {
        Long goodsId = createGoods("P6 售后机", "800.00", 10);
        String orderNo = createOrder(goodsId, 1);
        rest.exchange("/api/wx/sale/order/" + orderNo + "/pay", HttpMethod.POST,
                new HttpEntity<>(user()), String.class);

        // 用户申请售后 → 管理端同意 → mock 退款到账 + 订单置 90
        ResponseEntity<String> apply = rest.exchange("/api/wx/sale/order/" + orderNo + "/after-sale",
                HttpMethod.POST, new HttpEntity<>(Map.of("reason", "屏幕有划痕与描述不符"), user()), String.class);
        JsonNode applyBody = objectMapper.readTree(apply.getBody());
        assertEquals(0, applyBody.get("code").asInt());
        String asNo = applyBody.path("data").path("asNo").asText();
        assertTrue(asNo.startsWith("A"), "售后单号应 A 前缀");

        // 重复申请被拒
        ResponseEntity<String> dup = rest.exchange("/api/wx/sale/order/" + orderNo + "/after-sale",
                HttpMethod.POST, new HttpEntity<>(Map.of("reason", "again"), user()), String.class);
        assertNotEquals(0, objectMapper.readTree(dup.getBody()).get("code").asInt(), "重复申请应被拒");

        ResponseEntity<String> agree = rest.exchange("/api/admin/after-sales/" + asNo + "/agree",
                HttpMethod.PUT, new HttpEntity<>(Map.of("adminRemark", "情况属实，同意退款"), admin()), String.class);
        assertEquals(0, objectMapper.readTree(agree.getBody()).get("code").asInt());

        ResponseEntity<String> detail = rest.exchange("/api/admin/after-sales/" + asNo, HttpMethod.GET,
                new HttpEntity<>(admin()), String.class);
        JsonNode asBody = objectMapper.readTree(detail.getBody());
        assertEquals(30, asBody.path("data").path("status").asInt(), "售后单应为已退款");
        assertEquals(80000L, asBody.path("data").path("refundFen").asLong());
        assertEquals(90, orderDetail(orderNo, user()).path("status").asInt(), "订单应联动置为已退款");
        assertEquals(80000L, orderDetail(orderNo, user()).path("refundFen").asLong());
    }

    @Test
    @Order(4)
    void ownership_guard_and_reject() throws Exception {
        // 用户 B 不得操作用户 A 的订单
        String tokenB = objectMapper.readTree(rest.postForEntity("/api/wx/login",
                Map.of("code", "p6-user-b"), String.class).getBody()).path("data").path("token").asText();
        HttpHeaders userB = new HttpHeaders();
        userB.setBearerAuth(tokenB);

        Long goodsId = createGoods("P6 越权机", "100.00", 10);
        String orderNo = createOrder(goodsId, 1);
        rest.exchange("/api/wx/sale/order/" + orderNo + "/pay", HttpMethod.POST,
                new HttpEntity<>(user()), String.class);

        ResponseEntity<String> forbidden = rest.exchange("/api/wx/sale/order/" + orderNo, HttpMethod.GET,
                new HttpEntity<>(userB), String.class);
        assertEquals(40302, objectMapper.readTree(forbidden.getBody()).get("code").asInt(), "B 查看 A 订单应被拒");

        // 售后拒绝路径（A 自己的订单再申请一次：已付款单）
        ResponseEntity<String> apply = rest.exchange("/api/wx/sale/order/" + orderNo + "/after-sale",
                HttpMethod.POST, new HttpEntity<>(Map.of("reason", "不想要了"), user()), String.class);
        String asNo = objectMapper.readTree(apply.getBody()).path("data").path("asNo").asText();
        rest.exchange("/api/admin/after-sales/" + asNo + "/reject", HttpMethod.PUT,
                new HttpEntity<>(Map.of("adminRemark", "已发货超7天"), admin()), String.class);
        ResponseEntity<String> detail = rest.exchange("/api/admin/after-sales/" + asNo, HttpMethod.GET,
                new HttpEntity<>(admin()), String.class);
        assertEquals(40, objectMapper.readTree(detail.getBody()).path("data").path("status").asInt());
    }
}
