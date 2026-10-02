package com.rephone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rephone.mapper.AfterSaleMapper;
import com.rephone.mapper.RecycleOrderMapper;
import com.rephone.mapper.RepairOrderMapper;
import com.rephone.mapper.SaleOrderMapper;
import com.rephone.pojo.entity.AfterSale;
import com.rephone.pojo.entity.RecycleOrder;
import com.rephone.pojo.entity.RepairOrder;
import com.rephone.pojo.entity.SaleOrder;
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

import java.util.Map;

/**
 * P7 财务对账与数据看板验收：三线资金口径（回收打款 50/出售收款 20-40/退款 30/维修收款 50）、
 * 汇总净额、流水过滤与分页、看板汇总/趋势/榜单、FINANCE 角色可读、OPERATOR 不可读。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:rephone;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/schema-h2.sql,classpath:db/schema-role.sql,classpath:db/schema-recycle.sql,classpath:db/schema-repair.sql,classpath:db/schema-sale.sql",
        "rephone.wx.mock-login=true",
        "rephone.jwt.ttl-seconds=3600",
        "rephone.admin.token=test-admin-token"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FinanceDashboardTest {

    private static final String MARKER = "FD";

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RecycleOrderMapper recycleOrderMapper;

    @Autowired
    private SaleOrderMapper saleOrderMapper;

    @Autowired
    private AfterSaleMapper afterSaleMapper;

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

    private RecycleOrder recycle(long tenantId, int status, Long finalFen) {
        RecycleOrder o = new RecycleOrder();
        o.setTenantId(tenantId);
        o.setOrderNo("R" + uniqueSuffix() + status);
        o.setUserId(88000L);
        o.setOpenid("fd-openid");
        o.setModelId(1L);
        o.setBrandName("Apple");
        o.setModelName("iPhone 15 Pro Max");
        o.setStorage("256GB");
        o.setConditionKey("99new");
        o.setConditionLabel("99新");
        o.setQuoteFen(500000L);
        o.setStatus(status);
        o.setPickupType(10);
        o.setFinalFen(finalFen);
        recycleOrderMapper.insert(o);
        return o;
    }

    private SaleOrder sale(int status, long totalFen, Long refundFen) {
        SaleOrder o = new SaleOrder();
        o.setTenantId(0L);
        o.setOrderNo("S" + uniqueSuffix());
        o.setUserId(88000L);
        o.setOpenid("fd-openid");
        o.setGoodsId(1L);
        o.setGoodsName("测试商品" + MARKER);
        o.setPriceFen(totalFen);
        o.setQuantity(1);
        o.setTotalFen(totalFen);
        o.setStatus(status);
        o.setRefundFen(refundFen);
        o.setReceiverName("测试收件人");
        o.setReceiverPhone("13800000000");
        o.setReceiverAddr("测试地址 1 号");
        saleOrderMapper.insert(o);
        return o;
    }

    @Test
    @Order(1)
    void financeSummarySumsThreeBusinessLines() throws Exception {
        // 回收打款 10000；出售收款 25000（一笔 20 一笔 40）；退款 3000；维修收款 15000；一笔回收 10（非终态）不计
        recycle(0, RecycleOrder.STATUS_PAID, 10000L);
        recycle(0, RecycleOrder.STATUS_WAIT_SEND, 99999L);
        sale(SaleOrder.STATUS_PAID, 10000L, null);
        sale(SaleOrder.STATUS_DONE, 15000L, null);
        SaleOrder refunded = sale(SaleOrder.STATUS_REFUNDED, 8000L, 3000L);
        AfterSale as = new AfterSale();
        as.setTenantId(0L);
        as.setAsNo("A" + uniqueSuffix());
        as.setOrderNo(refunded.getOrderNo());
        as.setUserId(88000L);
        as.setType(10);
        as.setReason("测试退款");
        as.setStatus(AfterSale.STATUS_REFUNDED);
        as.setRefundFen(3000L);
        afterSaleMapper.insert(as);
        RepairOrder repair = new RepairOrder();
        repair.setTenantId(0L);
        repair.setOrderNo("F" + uniqueSuffix());
        repair.setUserId(88000L);
        repair.setOpenid("fd-openid");
        repair.setModelId(1L);
        repair.setBrandName("Apple");
        repair.setModelName("iPhone 15 Pro Max");
        repair.setItemsJson("[{\"itemId\":1,\"name\":\"换外屏\",\"priceFen\":15000}]");
        repair.setTotalFen(15000L);
        repair.setStatus(RepairOrder.STATUS_DONE);
        repair.setServiceType(10);
        repair.setContactName("测试");
        repair.setContactPhone("13800000000");
        repair.setAddress("测试地址");
        repair.setAppointTime("今天 上午");
        repair.setWarrantyDays(180);
        repairOrderMapper.insert(repair);

        ResponseEntity<String> resp = machineGet("/api/admin/finance/summary");
        JsonNode data = objectMapper.readTree(resp.getBody()).path("data");
        assertEquals(0, objectMapper.readTree(resp.getBody()).get("code").asInt());
        assertEquals(10000L, data.path("recyclePayFen").asLong());
        assertEquals(25000L, data.path("saleIncomeFen").asLong());
        assertEquals(3000L, data.path("saleRefundFen").asLong());
        assertEquals(15000L, data.path("repairIncomeFen").asLong());
        // 净额 = 25000 + 15000 - 3000 - 10000 = 27000
        assertEquals(27000L, data.path("netFen").asLong());
    }

    @Test
    @Order(2)
    void financeFlowsFilteredAndPaged() throws Exception {
        ResponseEntity<String> all = machineGet("/api/admin/finance/flows?pageNum=1&pageSize=50");
        JsonNode page = objectMapper.readTree(all.getBody()).path("data");
        assertTrue(page.get("total").asLong() >= 5);
        for (JsonNode item : page.path("records")) {
            String direction = item.path("direction").asText();
            int status = item.path("status").asInt();
            if ("payout".equals(direction) || "income".equals(direction)) {
                assertTrue(status == 50 || status == 20 || status == 30 || status == 40 || status == 60);
            }
        }
        ResponseEntity<String> recycleOnly = machineGet(
                "/api/admin/finance/flows?biz=10&pageNum=1&pageSize=50");
        for (JsonNode item : objectMapper.readTree(recycleOnly.getBody()).path("data").path("records")) {
            assertEquals(10, item.path("biz").asInt());
        }
    }

    @Test
    @Order(3)
    void dashboardSummaryTrendTop() throws Exception {
        ResponseEntity<String> summary = machineGet("/api/admin/dashboard/summary");
        JsonNode data = objectMapper.readTree(summary.getBody()).path("data");
        assertTrue(data.path("last30Orders").asLong() >= 5);
        assertTrue(data.path("recycleTotal").asLong() >= 2);

        ResponseEntity<String> trend = machineGet("/api/admin/dashboard/trend?days=7");
        assertEquals(7, objectMapper.readTree(trend.getBody()).path("data").size());

        ResponseEntity<String> top = machineGet("/api/admin/dashboard/top?limit=5");
        JsonNode topData = objectMapper.readTree(top.getBody()).path("data");
        assertTrue(topData.path("recycleModels").size() >= 1);
        assertTrue(topData.path("goods").size() >= 1);
    }

    @Test
    @Order(4)
    void financeReadableByFinanceRoleOnly() throws Exception {
        String suffix = uniqueSuffix();
        HttpHeaders machine = new HttpHeaders();
        machine.setBearerAuth("test-admin-token");
        rest.exchange("/api/admin/account", HttpMethod.POST,
                new HttpEntity<>(Map.of("username", "fd_fin_" + suffix, "password", "password123",
                        "nickname", "对账员", "roleCode", "FINANCE"), machine), String.class);
        ResponseEntity<String> login = rest.postForEntity("/api/admin/auth/login",
                Map.of("username", "fd_fin_" + suffix, "password", "password123"), String.class);
        String financeToken = objectMapper.readTree(login.getBody()).path("data").path("token").asText();

        HttpHeaders finance = new HttpHeaders();
        finance.setBearerAuth(financeToken);
        ResponseEntity<String> allowed = rest.exchange("/api/admin/finance/summary", HttpMethod.GET,
                new HttpEntity<>(finance), String.class);
        assertEquals(0, objectMapper.readTree(allowed.getBody()).get("code").asInt());

        rest.exchange("/api/admin/account", HttpMethod.POST,
                new HttpEntity<>(Map.of("username", "fd_ops_" + suffix, "password", "password123",
                        "nickname", "运营", "roleCode", "OPERATOR"), machine), String.class);
        ResponseEntity<String> opsLogin = rest.postForEntity("/api/admin/auth/login",
                Map.of("username", "fd_ops_" + suffix, "password", "password123"), String.class);
        String opsToken = objectMapper.readTree(opsLogin.getBody()).path("data").path("token").asText();
        HttpHeaders ops = new HttpHeaders();
        ops.setBearerAuth(opsToken);
        ResponseEntity<String> denied = rest.exchange("/api/admin/finance/summary", HttpMethod.GET,
                new HttpEntity<>(ops), String.class);
        assertEquals(40300, objectMapper.readTree(denied.getBody()).get("code").asInt());
    }
}
