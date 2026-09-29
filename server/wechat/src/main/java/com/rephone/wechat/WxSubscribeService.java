package com.rephone.wechat;

import com.rephone.common.web.HttpGuard;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

/**
 * 微信订阅消息推送（小程序 subscribe/send）。
 * 模板 ID 从 WX_SUBSCRIBE_TEMPLATE_ID 注入；未配置或 mock-login 时仅记录日志不发送。
 */
@Service
public class WxSubscribeService {

    private static final Logger log = LoggerFactory.getLogger(WxSubscribeService.class);
    private static final String SEND_URL = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token={token}";

    private final WxProperties properties;
    private final WxAccessTokenService accessTokenService;
    private final RestClient restClient;

    public WxSubscribeService(WxProperties properties, WxAccessTokenService accessTokenService) {
        this.properties = properties;
        this.accessTokenService = accessTokenService;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) 3_000);
        factory.setReadTimeout((int) 5_000);
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    /** 订单状态变更通知。任何失败只记日志，不影响主流程。 */
    public void notifyOrderStatus(String openid, String orderNo, String statusDesc) {
        if (properties.isMockLogin() || !StringUtils.hasText(properties.getSubscribeTemplateId())) {
            log.info("[wx-subscribe][mock] open={} order={} {}", openid, orderNo, statusDesc);
            return;
        }
        try {
            HttpGuard.requirePublicHttps(SEND_URL);
            Map<String, Object> data = Map.of(
                    "thing1", Map.of("value", trunc(statusDesc, 20)),
                    "character_string2", Map.of("value", trunc(orderNo, 32)));
            restClient.post().uri(SEND_URL, accessTokenService.getToken())
                    .body(Map.of("touser", openid, "template_id", properties.getSubscribeTemplateId(),
                            "page", "pages/recycle/order/list/index", "data", data))
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            log.warn("[wx-subscribe] 推送失败（不影响业务）: order={}, err={}", orderNo, e.getMessage());
        }
    }

    private static String trunc(String s, int max) {
        if (s == null) {
            return "";
        }
        return s.length() <= max ? s : s.substring(0, max);
    }
}
