package com.rephone;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

/**
 * 点击统计空数据回归：无任何事件/订单时三个接口都必须 200（回归
 * selectBatchIds 空集合生成非法 IN () 导致 top 500 的事故）。
 * 独立命名 H2 库，不种任何数据。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:clickstatsempty;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:db/schema-h2.sql,classpath:db/schema-role.sql,classpath:db/schema-quote.sql,classpath:db/schema-recycle.sql,classpath:db/schema-repair.sql,classpath:db/schema-sale.sql,classpath:db/schema-click-event.sql",
        "rephone.wx.mock-login=true",
        "rephone.jwt.ttl-seconds=3600",
        "rephone.admin.token=test-admin-token"
})
class ClickStatsEmptyTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    private ResponseEntity<String> machineGet(String uri) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth("test-admin-token");
        return rest.exchange(uri, HttpMethod.GET, new HttpEntity<>(h), String.class);
    }

    @Test
    void emptyDataReturnsZeroedAggregates() throws Exception {
        JsonNode summary = objectMapper.readTree(machineGet("/api/admin/stats/click/summary").getBody());
        assertEquals(0, summary.get("code").asInt());
        assertEquals(0, summary.path("data").path("last30").path("clicks").asLong());

        JsonNode trend = objectMapper.readTree(machineGet("/api/admin/stats/click/trend?days=7").getBody());
        assertEquals(0, trend.get("code").asInt());
        assertEquals(7, trend.path("data").size());

        JsonNode top = objectMapper.readTree(machineGet("/api/admin/stats/click/top?days=30&limit=10").getBody());
        assertEquals(0, top.get("code").asInt());
        assertEquals(0, top.path("data").path("goods").size());
        assertEquals(0, top.path("data").path("models").size());
    }
}
