package com.rephone.wechat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rephone.common.exception.BizException;
import com.rephone.common.web.HttpGuard;
import com.rephone.wechat.model.WxCode2SessionResponse;
import com.rephone.wechat.model.WxSession;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

/**
 * 微信 code2Session 客户端。
 * 出站目标固定为 api.weixin.qq.com（https），发请求前经 HttpGuard 校验；
 * appid/secret 仅来自环境变量；mock-login 仅限开发联调。
 */
@Component
public class WxApiClient {

    private static final Logger log = LoggerFactory.getLogger(WxApiClient.class);

    private static final String CODE2SESSION_URL =
            "https://api.weixin.qq.com/sns/jscode2session?appid={appid}&secret={secret}&js_code={code}&grant_type=authorization_code";

    private final WxProperties properties;
    private final RestClient restClient;

    public WxApiClient(WxProperties properties) {
        this.properties = properties;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(3).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(5).toMillis());
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    public WxSession code2Session(String code) {
        if (properties.isMockLogin()) {
            return mockSession(code);
        }
        if (!StringUtils.hasText(properties.getAppid()) || !StringUtils.hasText(properties.getSecret())) {
            throw new BizException(50001, "微信 AppID/Secret 未配置，请设置环境变量 WX_APPID / WX_SECRET");
        }
        HttpGuard.requirePublicHttps(CODE2SESSION_URL);
        WxCode2SessionResponse resp = restClient.get()
                .uri(CODE2SESSION_URL, properties.getAppid(), properties.getSecret(), code)
                .retrieve()
                .body(WxCode2SessionResponse.class);
        if (resp == null) {
            throw new BizException(50002, "微信登录响应为空");
        }
        if (resp.errcode() != null && resp.errcode() != 0) {
            throw new BizException(50003, "微信登录失败(" + resp.errcode() + "): " + resp.errmsg());
        }
        if (!StringUtils.hasText(resp.openid())) {
            throw new BizException(50004, "微信登录未返回 openid");
        }
        return new WxSession(resp.openid(), resp.sessionKey(), resp.unionid());
    }

    /** 开发模式：同一 code 稳定映射到同一模拟 openid，便于本地反复联调。 */
    private WxSession mockSession(String code) {
        log.warn("[wx] mock-login 已开启（仅限开发环境），使用模拟会话");
        String digest = sha256Hex(code == null ? "" : code);
        return new WxSession("mock-" + digest.substring(0, 16), null, null);
    }

    private static String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(Character.forDigit((b >> 4) & 0xF, 16)).append(Character.forDigit(b & 0xF, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 不可用", e);
        }
    }
}
