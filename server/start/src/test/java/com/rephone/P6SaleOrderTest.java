package com.rephone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    /** 商品机型属性写入/回读 + 在售列表筛选、排序与筛选项接口。 */
    @Test
    @Order(5)
    void goods_attributes_and_filters() throws Exception {
        ResponseEntity<String> created = rest.exchange("/api/admin/goods", HttpMethod.POST,
                new HttpEntity<>(Map.of(
                        "name", "P6 属性机",
                        "priceYuan", new BigDecimal("2999.00"),
                        "stock", 3,
                        "descText", "属性用例",
                        "brand", "Apple",
                        "storage", "256G",
                        "conditionLevel", "95新",
                        "tags", "官方自营,已验机"), admin()), String.class);
        JsonNode createBody = objectMapper.readTree(created.getBody());
        assertEquals(0, createBody.get("code").asInt());
        Long goodsId = createBody.path("data").path("goodsId").asLong();

        // 属性回读
        JsonNode data = objectMapper.readTree(rest.exchange("/api/wx/goods/" + goodsId, HttpMethod.GET,
                new HttpEntity<>(user()), String.class).getBody()).path("data");
        assertEquals("Apple", data.path("brand").asText());
        assertEquals("256G", data.path("storage").asText());
        assertEquals("95新", data.path("conditionLevel").asText());
        assertEquals("官方自营,已验机", data.path("tags").asText(), "标签应去重后原样返回");

        // 精确筛选与关键词命中
        assertTrue(containsGoods("/api/wx/goods?brand=Apple", goodsId), "按品牌筛选应命中");
        assertTrue(containsGoods("/api/wx/goods?conditionLevel=95新", goodsId), "按成色筛选应命中");
        assertTrue(containsGoods("/api/wx/goods?keyword=Apple", goodsId), "关键词应匹配品牌");
        assertFalse(containsGoods("/api/wx/goods?brand=NoSuchBrand", goodsId), "不存在的品牌不应命中");

        // 筛选项接口
        JsonNode filters = objectMapper.readTree(rest.exchange("/api/wx/goods/filters", HttpMethod.GET,
                new HttpEntity<>(user()), String.class).getBody()).path("data");
        assertTrue(filters.path("brands").toString().contains("Apple"), "筛选项应含品牌 Apple");
        assertTrue(filters.path("conditions").toString().contains("95新"), "筛选项应含成色 95新");

        // 排序：价格升序时首项不高于末项
        JsonNode asc = objectMapper.readTree(rest.exchange("/api/wx/goods?sort=priceAsc", HttpMethod.GET,
                new HttpEntity<>(user()), String.class).getBody()).path("data");
        assertTrue(asc.size() >= 2, "应有多个在售商品用于排序断言");
        assertTrue(asc.get(0).path("priceFen").asLong() <= asc.get(asc.size() - 1).path("priceFen").asLong(),
                "价格升序应生效");
    }

    /** 查询在售列表并判断是否包含指定商品 id。 */
    private boolean containsGoods(String path, Long goodsId) throws Exception {
        ResponseEntity<String> resp = rest.exchange(path, HttpMethod.GET,
                new HttpEntity<>(user()), String.class);
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertEquals(0, body.get("code").asInt());
        for (JsonNode node : body.path("data")) {
            if (node.path("id").asLong() == goodsId) {
                return true;
            }
        }
        return false;
    }

    /** 商品质检报告：无报告 → 录入 → 用户端读取 → 整体覆盖 → 清空。 */
    @Test
    @Order(6)
    void goods_inspection_report() throws Exception {
        ResponseEntity<String> created = rest.exchange("/api/admin/goods", HttpMethod.POST,
                new HttpEntity<>(Map.of("name", "P6 质检机", "priceYuan", new BigDecimal("1500.00"),
                        "stock", 2, "conditionLevel", "9成新"), admin()), String.class);
        Long goodsId = objectMapper.readTree(created.getBody()).path("data").path("goodsId").asLong();

        // 未录入报告 → data 为 null
        JsonNode before = objectMapper.readTree(rest.exchange("/api/wx/goods/" + goodsId + "/inspection",
                HttpMethod.GET, new HttpEntity<>(user()), String.class).getBody());
        assertEquals(0, before.get("code").asInt());
        assertTrue(before.get("data") == null || before.get("data").isNull(), "未录入报告时应为空");

        // 录入：3 项检查，其中 1 项异常
        Map<String, Object> body = Map.of(
                "inspector", "质检员小李",
                "inspectedAt", "2026-09-30 10:30:00",
                "batteryHealth", 92,
                "summary", "整机功能正常，屏幕左上角有细微划痕",
                "images", List.of("https://cdn.example.com/q1.jpg", "https://cdn.example.com/q2.jpg"),
                "items", List.of(
                        Map.of("category", "外观", "name", "后盖", "result", "轻微磕碰", "note", "右下角一处"),
                        Map.of("category", "屏幕", "name", "屏幕显示", "result", "正常", "note", ""),
                        Map.of("category", "功能", "name", "电池健康", "result", "正常", "note", "92%")));
        ResponseEntity<String> saved = rest.exchange("/api/admin/goods/" + goodsId + "/inspection",
                HttpMethod.POST, new HttpEntity<>(body, admin()), String.class);
        assertEquals(0, objectMapper.readTree(saved.getBody()).get("code").asInt());

        JsonNode report = objectMapper.readTree(rest.exchange("/api/wx/goods/" + goodsId + "/inspection",
                HttpMethod.GET, new HttpEntity<>(user()), String.class).getBody()).path("data");
        String reportNo = report.path("reportNo").asText();
        assertTrue(reportNo.startsWith("Q"), "报告号应 Q 前缀");
        assertEquals("质检员小李", report.path("inspector").asText());
        assertEquals(92, report.path("batteryHealth").asInt());
        assertEquals("9成新", report.path("conditionLevel").asText(), "成色应取自商品本身");
        assertEquals(3, report.path("items").size());
        assertEquals(2, report.path("normalCount").asInt());
        assertEquals(1, report.path("abnormalCount").asInt());
        assertEquals(2, report.path("images").size());

        // 整体覆盖：只留 1 项，报告号不变
        Map<String, Object> overwrite = Map.of(
                "inspector", "质检员小李",
                "batteryHealth", 88,
                "items", List.of(Map.of("category", "功能", "name", "电池健康",
                        "result", "已更换", "note", "官方更换")));
        rest.exchange("/api/admin/goods/" + goodsId + "/inspection", HttpMethod.POST,
                new HttpEntity<>(overwrite, admin()), String.class);
        JsonNode after = objectMapper.readTree(rest.exchange("/api/wx/goods/" + goodsId + "/inspection",
                HttpMethod.GET, new HttpEntity<>(user()), String.class).getBody()).path("data");
        assertEquals(1, after.path("items").size(), "检查项应整体覆盖");
        assertEquals(88, after.path("batteryHealth").asInt());
        assertEquals(0, after.path("normalCount").asInt());
        assertEquals(1, after.path("abnormalCount").asInt());
        assertEquals(reportNo, after.path("reportNo").asText(), "覆盖不应更换报告号");

        // 清空：items 为空即删除报告
        rest.exchange("/api/admin/goods/" + goodsId + "/inspection", HttpMethod.POST,
                new HttpEntity<>(Map.of("items", List.of()), admin()), String.class);
        JsonNode cleared = objectMapper.readTree(rest.exchange("/api/wx/goods/" + goodsId + "/inspection",
                HttpMethod.GET, new HttpEntity<>(user()), String.class).getBody());
        assertTrue(cleared.get("data") == null || cleared.get("data").isNull(), "清空后应为空");
    }
}
