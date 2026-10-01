package com.rephone.wechat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * 订阅消息字段映射与错误码解析。
 * 纯单元测试（不起 Spring 上下文）：这里校验的是「字段名来自配置」这条硬约束，
 * 写死 thing1 这类随机编号会直接导致线上推送 47003。
 */
class WxSubscribeServiceTest {

    @Test
    void buildsPayloadFromConfiguredFieldNames() {
        Map<String, Object> data = WxSubscribeService.buildData("thing1", "character_string2", "time3",
                "待寄出", "R20261001000000000001", "2026-10-01 14:30");
        assertEquals(3, data.size());
        assertEquals(Map.of("value", "待寄出"), data.get("thing1"));
        assertEquals(Map.of("value", "R20261001000000000001"), data.get("character_string2"));
        assertEquals(Map.of("value", "2026-10-01 14:30"), data.get("time3"));
    }

    @Test
    void skipsUnconfiguredAndMalformedFields() {
        Map<String, Object> data = WxSubscribeService.buildData("thing1", "", "THING-3",
                "待寄出", "R1", "2026-10-01 14:30");
        assertEquals(1, data.size(), "只应保留合法且已配置的字段");
        assertTrue(data.containsKey("thing1"));
    }

    @Test
    void truncatesThingFieldTo20Chars() {
        String longText = "您的回收订单 待寄出 → 运输中，感谢使用 RePhone 服务";
        Map<String, Object> data = WxSubscribeService.buildData("thing1", null, null, longText, "R1", null);
        String value = (String) ((Map<?, ?>) data.get("thing1")).get("value");
        assertEquals(20, value.length(), "微信 thing 类字段上限 20 字符，超出会被拒");
    }

    @Test
    void emptyValueIsNotSent() {
        Map<String, Object> data = WxSubscribeService.buildData("thing1", "character_string2", null,
                null, "R1", null);
        assertFalse(data.containsKey("thing1"), "空值不应发送，否则微信按长度校验会拒");
        assertTrue(data.containsKey("character_string2"));
    }

    @Test
    void parsesWechatErrorCodes() {
        assertNull(WxSubscribeService.describeError(Map.of("errcode", 0, "errmsg", "ok")));
        assertTrue(WxSubscribeService.describeError(Map.of("errcode", 47003)).contains("47003"));
        assertTrue(WxSubscribeService.describeError(Map.of("errcode", 43101)).contains("43101"));
        assertTrue(WxSubscribeService.describeError(Map.of("errcode", 40037)).contains("40037"));
        assertTrue(WxSubscribeService.describeError(Map.of("errcode", 99999, "errmsg", "weird")).contains("99999"));
        assertNotNull(WxSubscribeService.describeError(null));
    }
}
