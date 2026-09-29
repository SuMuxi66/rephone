package com.rephone.wechat;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

/**
 * 微信接口调用凭据（stable_token）。内存缓存，到期前 5 分钟刷新；
 * mock-login 模式下返回固定 mock token，不发起真实请求。
 */
@Service
public class WxAccessTokenService {

    private static final Logger log = LoggerFactory.getLogger(WxAccessTokenService.class);
    private static final String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/stable_token";

    private final WxProperties properties;
    private final RestClient restClient;
    private final AtomicReference<String> cached = new AtomicReference<>("");
    private volatile Instant expireAt = Instant.EPOCH;

    public WxAccessTokenService(WxProperties properties) {
        this.properties = properties;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) 3_000);
        factory.setReadTimeout((int) 5_000);
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    public synchronized String getToken() {
        if (properties.isMockLogin()) {
            return "mock-access-token";
        }
        if (!StringUtils.hasText(properties.getAppid()) || !StringUtils.hasText(properties.getSecret())) {
            throw new IllegalStateException("微信 AppID/Secret 未配置，请设置 WX_APPID / WX_SECRET");
        }
        if (StringUtils.hasText(cached.get()) && Instant.now().isBefore(expireAt)) {
            return cached.get();
        }
        com.rephone.common.web.HttpGuard.requirePublicHttps(TOKEN_URL);
        var resp = restClient.post().uri(TOKEN_URL)
                .body(java.util.Map.of("grant_type", "client_credential",
                        "appid", properties.getAppid(), "secret", properties.getSecret()))
                .retrieve()
                .body(java.util.Map.class);
        Object token = resp == null ? null : resp.get("access_token");
        Object expiresIn = resp == null ? null : resp.get("expires_in");
        if (!(token instanceof String t) || t.isBlank()) {
            throw new IllegalStateException("获取 access_token 失败: " + resp);
        }
        long seconds = expiresIn instanceof Number n ? n.longValue() : 7200;
        cached.set(t);
        expireAt = Instant.now().plusSeconds(Math.max(60, seconds - 300));
        log.info("[wx] access_token 已刷新，有效期 {}s", seconds);
        return t;
    }
}
