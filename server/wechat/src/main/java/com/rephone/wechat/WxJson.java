package com.rephone.wechat;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 微信开放接口的响应体是 JSON，但 Content-Type 返回 text/plain，
 * RestClient 默认的 Jackson 转换器不认 text/plain 会抛
 * UnknownContentTypeException —— 因此统一取原始字符串后手动解析。
 */
final class WxJson {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private WxJson() {
    }

    static <T> T parse(String body, Class<T> type) {
        if (body == null || body.isBlank()) {
            throw new IllegalStateException("微信接口返回空响应");
        }
        try {
            return MAPPER.readValue(body, type);
        } catch (Exception e) {
            throw new IllegalStateException("微信接口响应解析失败: " + body, e);
        }
    }
}
