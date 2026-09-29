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
 * P5 管理控制台扩展验收：账号管理（列表/启停/创建管理员/重置密码）、
 * 机型管理（品牌/机型/内存基准价，估价引擎联动）、用户地址簿（用户端 CRUD + 管理端查看/删除）。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:rephone;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
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
class P5AdminConsoleTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    private HttpHeaders admin;
    private String userToken;
    private Long userId;

    @BeforeAll
    void init() throws Exception {
        admin = new HttpHeaders();
        admin.setBearerAuth("test-admin-token");

        ResponseEntity<String> resp = rest.postForEntity("/api/wx/login",
                Map.of("code", "p5c-user"), String.class);
        JsonNode body = objectMapper.readTree(resp.getBody());
        userToken = body.path("data").path("token").asText();
        userId = body.path("data").path("userId").asLong();
    }

    private HttpHeaders user() {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(userToken);
        return h;
    }

    @Test
    @Order(1)
    void account_management() throws Exception {
        // 列表可分页、含脱敏 openid
        ResponseEntity<String> list = rest.exchange("/api/admin/users?pageNum=1&pageSize=5",
                HttpMethod.GET, new HttpEntity<>(admin), String.class);
        JsonNode listBody = objectMapper.readTree(list.getBody());
        assertEquals(0, listBody.get("code").asInt());
        assertTrue(listBody.path("data").path("total").asLong() >= 1, "应能查到 wx 用户");
        String openidMasked = listBody.path("data").path("records").get(0).path("openidMasked").asText();
        assertTrue(openidMasked.startsWith("***"), "openid 应脱敏");

        // 创建新管理员 → 该账号可登录
        ResponseEntity<String> create = rest.exchange("/api/admin/account", HttpMethod.POST,
                new HttpEntity<>(Map.of("username", "ops_manager", "password", "ops-pass-2026",
                        "nickname", "运营经理"), admin), String.class);
        assertEquals(0, objectMapper.readTree(create.getBody()).get("code").asInt());
        ResponseEntity<String> newLogin = rest.postForEntity("/api/admin/auth/login",
                Map.of("username", "ops_manager", "password", "ops-pass-2026"), String.class);
        assertEquals(0, objectMapper.readTree(newLogin.getBody()).get("code").asInt(), "新建管理员应可登录");

        // 重置密码 → 旧密码失效新密码生效
        Long opsId = objectMapper.readTree(create.getBody()).path("data").path("userId").asLong();
        rest.exchange("/api/admin/account/" + opsId + "/password", HttpMethod.PUT,
                new HttpEntity<>(Map.of("password", "new-pass-2026"), admin), String.class);
        ResponseEntity<String> oldPw = rest.postForEntity("/api/admin/auth/login",
                Map.of("username", "ops_manager", "password", "ops-pass-2026"), String.class);
        assertEquals(40101, objectMapper.readTree(oldPw.getBody()).get("code").asInt());
        ResponseEntity<String> newPw = rest.postForEntity("/api/admin/auth/login",
                Map.of("username", "ops_manager", "password", "new-pass-2026"), String.class);
        assertEquals(0, objectMapper.readTree(newPw.getBody()).get("code").asInt());

        // 禁用普通用户 → 其 wx 登录被拒；启用后恢复
        ResponseEntity<String> disable = rest.exchange("/api/admin/user/" + userId + "/status",
                HttpMethod.PUT, new HttpEntity<>(Map.of("status", 0), admin), String.class);
        assertEquals(0, objectMapper.readTree(disable.getBody()).get("code").asInt());
        ResponseEntity<String> blocked = rest.postForEntity("/api/wx/login",
                Map.of("code", "p5c-user"), String.class);
        assertEquals(40301, objectMapper.readTree(blocked.getBody()).get("code").asInt(), "禁用后登录应被拒");
        rest.exchange("/api/admin/user/" + userId + "/status", HttpMethod.PUT,
                new HttpEntity<>(Map.of("status", 1), admin), String.class);
        ResponseEntity<String> restored = rest.postForEntity("/api/wx/login",
                Map.of("code", "p5c-user"), String.class);
        assertEquals(0, objectMapper.readTree(restored.getBody()).get("code").asInt(), "启用后应恢复登录");
    }

    @Test
    @Order(2)
    void model_management_and_quote_linkage() throws Exception {
        // 新品牌 + 新机型（两档内存价）
        ResponseEntity<String> brandResp = rest.exchange("/api/admin/brand", HttpMethod.POST,
                new HttpEntity<>(Map.of("name", "测试品牌P5C"), admin), String.class);
        Long brandId = objectMapper.readTree(brandResp.getBody()).path("data").path("brandId").asLong();
        assertNotEquals(0L, brandId);

        ResponseEntity<String> modelResp = rest.exchange("/api/admin/model", HttpMethod.POST,
                new HttpEntity<>(Map.of(
                        "brandId", brandId, "name", "P5C Pro", "releaseYear", 2026,
                        "prices", List.of(
                                Map.of("storage", "128GB", "priceYuan", new BigDecimal("1999")),
                                Map.of("storage", "256GB", "priceYuan", new BigDecimal("2699")))), admin),
                String.class);
        Long modelId = objectMapper.readTree(modelResp.getBody()).path("data").path("modelId").asLong();
        assertNotEquals(0L, modelId);

        // 用户端机型列表可见新机型与内存档
        ResponseEntity<String> models = rest.exchange("/api/wx/models?brandId=" + brandId,
                HttpMethod.GET, new HttpEntity<>(user()), String.class);
        JsonNode modelsBody = objectMapper.readTree(models.getBody());
        assertEquals(0, modelsBody.get("code").asInt());
        assertEquals(1, modelsBody.path("data").size());
        assertEquals(2, modelsBody.path("data").get(0).path("storages").size());

        // 用户端估价联动：1999 × 0.85(COND_95) = 1699.15 元 = 169915 分
        ResponseEntity<String> quote = rest.exchange("/api/wx/quote/calculate", HttpMethod.POST,
                new HttpEntity<>(Map.of("modelId", modelId, "storage", "128GB",
                        "condition", "COND_95", "issues", List.of()), user()), String.class);
        JsonNode quoteBody = objectMapper.readTree(quote.getBody());
        assertEquals(0, quoteBody.get("code").asInt());
        assertEquals(169915L, quoteBody.path("data").path("priceFen").asLong(), "新机型估价应按新基准价计算");

        // 改价联动：256GB 2699 → 3000，重估 3000 × 0.85 = 2550 元 = 255000 分
        rest.exchange("/api/admin/model/" + modelId + "/price", HttpMethod.PUT,
                new HttpEntity<>(Map.of("storage", "256GB", "priceYuan", new BigDecimal("3000")), admin),
                String.class);
        ResponseEntity<String> quote2 = rest.exchange("/api/wx/quote/calculate", HttpMethod.POST,
                new HttpEntity<>(Map.of("modelId", modelId, "storage", "256GB",
                        "condition", "COND_95", "issues", List.of()), user()), String.class);
        assertEquals(255000L, objectMapper.readTree(quote2.getBody()).path("data").path("priceFen").asLong(),
                "改价后估价应实时联动");
    }

    @Test
    @Order(3)
    void user_address_book_and_admin_view() throws Exception {
        // 用户端建 2 条地址，第 2 条设为默认
        ResponseEntity<String> a1 = rest.exchange("/api/wx/address", HttpMethod.POST,
                new HttpEntity<>(Map.of("name", "张三", "phone", "13800001111",
                        "region", "甘肃省 兰州市 城关区", "detail", "XX路1号", "isDefault", true), user()),
                String.class);
        assertEquals(0, objectMapper.readTree(a1.getBody()).get("code").asInt());
        ResponseEntity<String> a2 = rest.exchange("/api/wx/address", HttpMethod.POST,
                new HttpEntity<>(Map.of("name", "张三", "phone", "13800001111",
                        "region", "甘肃省 兰州市 七里河区", "detail", "YY路2号", "isDefault", true), user()),
                String.class);
        Long addr2 = objectMapper.readTree(a2.getBody()).path("data").path("addressId").asLong();

        ResponseEntity<String> list = rest.exchange("/api/wx/address", HttpMethod.GET,
                new HttpEntity<>(user()), String.class);
        JsonNode listBody = objectMapper.readTree(list.getBody());
        assertEquals(0, listBody.get("code").asInt());
        assertEquals(2, listBody.path("data").size());
        assertEquals(1, listBody.path("data").get(0).path("isDefault").asInt(), "默认地址应排首位且唯一");

        // 管理端可见并可删除
        ResponseEntity<String> adminList = rest.exchange("/api/admin/addresses?pageNum=1&pageSize=10",
                HttpMethod.GET, new HttpEntity<>(admin), String.class);
        JsonNode adminBody = objectMapper.readTree(adminList.getBody());
        assertEquals(0, adminBody.get("code").asInt());
        assertTrue(adminBody.path("data").path("total").asLong() >= 2, "管理端应能看到用户地址");

        rest.exchange("/api/admin/addresses/" + addr2, HttpMethod.DELETE,
                new HttpEntity<>(admin), String.class);
        ResponseEntity<String> afterDelete = rest.exchange("/api/wx/address", HttpMethod.GET,
                new HttpEntity<>(user()), String.class);
        assertEquals(1, objectMapper.readTree(afterDelete.getBody()).path("data").size(), "管理端删除后用户端应减少");

        // 无效手机号应被拒
        ResponseEntity<String> badPhone = rest.exchange("/api/wx/address", HttpMethod.POST,
                new HttpEntity<>(Map.of("name", "李四", "phone", "123",
                        "region", "甘肃省 兰州市 城关区", "detail", "ZZ路3号", "isDefault", false), user()),
                String.class);
        assertNotEquals(0, objectMapper.readTree(badPhone.getBody()).get("code").asInt(), "非法手机号应被拒");
    }
}
