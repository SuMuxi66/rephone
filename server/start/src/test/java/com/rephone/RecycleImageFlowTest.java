package com.rephone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;

/**
 * 回收图片上传验收：下单携带机器照片（front/back/screen/damage）保存与详情回显、
 * 数量限制、类型/路径非法拒绝、越权查看 403、租户隔离。
 * 下单走真实估价复核链（schema-quote 种子）；独立 H2 库不污染其他测试。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:rimg;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/schema-h2.sql,classpath:db/schema-quote.sql,"
                + "classpath:db/schema-role.sql,classpath:db/schema-recycle.sql",
        "rephone.wx.mock-login=true",
        "rephone.jwt.ttl-seconds=3600",
        "rephone.admin.token=test-admin-token",
        "rephone.express.mock=true",
        "rephone.cos.mock=false",
        "rephone.cos.secret-id=test-cos-id",
        "rephone.cos.secret-key=test-cos-key",
        "rephone.cos.bucket=test-bucket-1250000000",
        "rephone.cos.region=ap-guangzhou"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RecycleImageFlowTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private String userToken;
    private long userId;
    private String tenantAdminToken;

    @BeforeAll
    void loginAndSeed() throws Exception {
        ResponseEntity<String> resp = rest.postForEntity("/api/wx/login",
                Map.of("code", "rimg-code"), String.class);
        JsonNode data = objectMapper.readTree(resp.getBody()).path("data");
        userToken = data.path("token").asText();
        assertNotEquals("", userToken);
        userId = data.path("userId").asLong();

        // 他人单 + 租户 5 单（含图片），供越权与隔离断言
        insertForeignOrder();
        insertTenant5Order();

        // 租户 5 管理员（recycle:manage）
        jdbcTemplate.update("merge into tenant (id, name, status) key (id) values (5, '图片测试租户', 1)");
        jdbcTemplate.update("merge into role (role_code, role_name, permissions_json, status) key (role_code) "
                + "values ('TENANT_ADMIN', '租户管理员', '[\"recycle:manage\"]', 1)");
        jdbcTemplate.update("delete from `user` where username = 'rimg_tadmin'");
        jdbcTemplate.update("insert into `user` (tenant_id, openid, nickname, status, username, password_hash, role) "
                        + "values (5, 'rimg-tadmin-openid', '图片租户管理员', 1, 'rimg_tadmin', ?, 'TENANT_ADMIN')",
                encoder.encode("password123"));
        ResponseEntity<String> loginResp = rest.postForEntity("/api/admin/auth/login",
                Map.of("username", "rimg_tadmin", "password", "password123"), String.class);
        tenantAdminToken = objectMapper.readTree(loginResp.getBody()).path("data").path("token").asText();
        assertNotEquals("", tenantAdminToken);
    }

    private void insertForeignOrder() {
        jdbcTemplate.update("insert into recycle_order (tenant_id, order_no, user_id, openid, model_id, "
                        + "brand_name, model_name, storage, condition_key, condition_label, quote_fen, status) "
                        + "values (0, 'R-IMG-FOREIGN', 999999, 'seed-rimg', 1, 'Apple', 'iPhone 15', '128GB', "
                        + "'COND_95', '95新', 566000, 10)");
    }

    private void insertTenant5Order() {
        jdbcTemplate.update("insert into recycle_order (tenant_id, order_no, user_id, openid, model_id, "
                        + "brand_name, model_name, storage, condition_key, condition_label, quote_fen, status, "
                        + "images_json) values (5, 'R-IMG-T5', 888888, 'seed-rimg', 1, 'Apple', 'iPhone 15', "
                        + "'128GB', 'COND_95', '95新', 566000, 10, "
                        + "'[{\"type\":\"front\",\"url\":\"recycle/2026-10-08/t5f.jpg\"}]')");
    }

    private HttpHeaders user() {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(userToken);
        return h;
    }

    private HttpHeaders tenantAdmin() {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(tenantAdminToken);
        return h;
    }

    private List<Map<String, String>> images(String type, String url) {
        return List.of(Map.of("type", type, "url", url));
    }

    private ResponseEntity<String> createOrder(List<Map<String, String>> images) {
        Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("modelId", 1);
        payload.put("storage", "128GB");
        payload.put("condition", "COND_95");
        payload.put("issues", List.of("SCREEN"));
        payload.put("quoteFen", 566000);
        payload.put("pickupType", 10);
        payload.put("pickupName", "张三");
        payload.put("pickupPhone", "13800000001");
        payload.put("pickupAddress", "甘肃省兰州市城关区XX路1号");
        payload.put("remark", "测试单");
        payload.put("images", images);
        return rest.exchange("/api/wx/recycle/order", HttpMethod.POST,
                new HttpEntity<>(payload, user()), String.class);
    }

    private JsonNode detail(String orderNo) throws Exception {
        ResponseEntity<String> resp = rest.exchange("/api/wx/recycle/order/" + orderNo, HttpMethod.GET,
                new HttpEntity<>(user()), String.class);
        JsonNode body = objectMapper.readTree(resp.getBody());
        assertEquals(0, body.get("code").asInt(), resp.getBody());
        return body.path("data");
    }

    @Test
    @Order(1)
    void images_are_saved_and_returned_in_detail() throws Exception {
        ResponseEntity<String> createResp = createOrder(List.of(
                Map.of("type", "front", "url", "recycle/2026-10-08/front.jpg"),
                Map.of("type", "damage", "url", "recycle/2026-10-08/damage.jpg")));
        JsonNode created = objectMapper.readTree(createResp.getBody());
        assertEquals(0, created.get("code").asInt(), createResp.getBody());
        String orderNo = created.path("data").path("orderNo").asText();

        JsonNode detail = detail(orderNo);
        assertEquals(2, detail.path("images").size(), "详情应返回两张图片");
        assertEquals("front", detail.path("images").get(0).path("type").asText());
        // 图片统一换 COS GET 预签名 URL（CosImageService 口径），mock 不再返回裸 key
        String url = detail.path("images").get(0).path("url").asText();
        assertTrue(url.startsWith("https://test-bucket-1250000000.cos.ap-guangzhou.myqcloud.com/recycle/2026-10-08/front.jpg?q-sign-algorithm=sha1"),
                "详情图片应为 COS 预签名 URL，实际: " + url);
        assertEquals("damage", detail.path("images").get(1).path("type").asText());
    }

    @Test
    @Order(2)
    void more_than_nine_images_are_rejected() throws Exception {
        List<Map<String, String>> tooMany = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tooMany.add(Map.of("type", "damage", "url", "recycle/2026-10-08/p" + i + ".jpg"));
        }
        assertNotEquals(0, objectMapper.readTree(createOrder(tooMany).getBody()).path("code").asInt(),
                "超过 9 张应被拒绝");
    }

    @Test
    @Order(3)
    void invalid_type_or_path_is_rejected() throws Exception {
        // 类型不在白名单
        assertNotEquals(0, objectMapper.readTree(
                createOrder(images("top", "recycle/2026-10-08/a.jpg")).getBody()).path("code").asInt());
        // 路径目录穿越
        assertNotEquals(0, objectMapper.readTree(
                createOrder(images("front", "../evil.jpg")).getBody()).path("code").asInt());
        // 路径含非法字符
        assertNotEquals(0, objectMapper.readTree(
                createOrder(images("front", "recycle/a b.jpg?x=1")).getBody()).path("code").asInt());
    }

    @Test
    @Order(4)
    void foreign_user_cannot_view_detail() throws Exception {
        // 他人回收单：详情 403
        ResponseEntity<String> resp = rest.exchange("/api/wx/recycle/order/R-IMG-FOREIGN", HttpMethod.GET,
                new HttpEntity<>(user()), String.class);
        assertNotEquals(0, objectMapper.readTree(resp.getBody()).path("code").asInt());
    }

    @Test
    @Order(5)
    void tenant_isolation_on_admin_detail() throws Exception {
        // 租户 5 管理员查看租户 0 订单 → 查无此单
        ResponseEntity<String> foreign = rest.exchange("/api/admin/recycle/order/R-IMG-FOREIGN", HttpMethod.GET,
                new HttpEntity<>(tenantAdmin()), String.class);
        assertNotEquals(0, objectMapper.readTree(foreign.getBody()).path("code").asInt(), foreign.getBody());

        // 本租户（5）订单正常可见且图片回显（COS 预签名 URL）
        ResponseEntity<String> own = rest.exchange("/api/admin/recycle/order/R-IMG-T5", HttpMethod.GET,
                new HttpEntity<>(tenantAdmin()), String.class);
        JsonNode body = objectMapper.readTree(own.getBody());
        assertEquals(0, body.get("code").asInt(), own.getBody());
        assertEquals(1, body.path("data").path("images").size());
        assertEquals("front", body.path("data").path("images").get(0).path("type").asText());
        assertTrue(body.path("data").path("images").get(0).path("url").asText()
                .startsWith("https://test-bucket-1250000000.cos.ap-guangzhou.myqcloud.com/"));
    }
}
