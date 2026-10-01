package com.rephone.wechat;

import com.rephone.common.web.HttpGuard;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

/**
 * 微信订阅消息推送（小程序 subscribe/send）。
 *
 * <p>模板 ID 与**字段名**都来自配置：微信申请模板时字段编号是随机分配的
 * （thing1 / character_string2 / time3 …），写死必然对不上，发送会返回 47003
 * 而接口表面“成功”。所以这里必须显式校验微信返回的 errcode，失败要打 ERROR 日志。
 *
 * <p>未配置模板或 mock-login 时只记日志不发送。
 */
@Service
public class WxSubscribeService {

    private static final Logger log = LoggerFactory.getLogger(WxSubscribeService.class);

    private static final String SEND_URL = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token={token}";

    /** 微信模板字段名的合法形态：小写字母/下划线 + 数字，如 thing1、character_string2、time3。 */
    private static final Pattern FIELD_NAME = Pattern.compile("^[a-z][a-z_]*\\d+$");

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /** 微信 thing 类字段限制 20 个字符，超出会被拒。 */
    private static final int THING_MAX = 20;

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
            Map<String, Object> data = buildData(properties.getSubscribeFieldStatus(),
                    properties.getSubscribeFieldOrderNo(), properties.getSubscribeFieldTime(),
                    statusDesc, orderNo, LocalDateTime.now().format(TIME_FMT));
            if (data.isEmpty()) {
                log.error("[wx-subscribe] subscribe-fields 未配置，无法推送 order={}。"
                        + "请按小程序后台模板的实际字段名配置 WX_SUBSCRIBE_FIELD_STATUS / _ORDER_NO", orderNo);
                return;
            }
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("touser", openid);
            body.put("template_id", properties.getSubscribeTemplateId());
            if (StringUtils.hasText(properties.getSubscribePage())) {
                body.put("page", properties.getSubscribePage());
            }
            body.put("data", data);

            Map<String, Object> resp = restClient.post()
                    .uri(SEND_URL, accessTokenService.getToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            String error = describeError(resp);
            if (error != null) {
                log.error("[wx-subscribe] 推送失败 order={} {}；实际发送字段={}", orderNo, error, data.keySet());
            } else {
                log.info("[wx-subscribe] 推送成功 order={} fields={}", orderNo, data.keySet());
            }
        } catch (Exception e) {
            log.warn("[wx-subscribe] 推送异常（不影响业务）: order={}, err={}", orderNo, e.getMessage());
        }
    }

    /** 本服务是否具备真实推送条件（配置齐 + 非 mock）。 */
    public boolean isSendable() {
        return !properties.isMockLogin() && StringUtils.hasText(properties.getSubscribeTemplateId());
    }

    /**
     * 按配置组装 data。
     *
     * <p>未配置或字段名格式非法的键一律跳过：微信要求 data 的键与模板字段严格对应，
     * 多传/少传都会报 47003，宁可少传也不要塞错。
     */
    static Map<String, Object> buildData(String statusField, String orderNoField, String timeField,
                                         String statusDesc, String orderNo, String time) {
        Map<String, Object> data = new LinkedHashMap<>();
        put(data, statusField, trunc(statusDesc, THING_MAX));
        put(data, orderNoField, trunc(orderNo, 32));
        put(data, timeField, time);
        return data;
    }

    private static void put(Map<String, Object> data, String field, String value) {
        if (!StringUtils.hasText(field)) {
            return;
        }
        if (!FIELD_NAME.matcher(field).matches()) {
            log.warn("[wx-subscribe] 字段名格式非法已跳过：{}（应形如 thing1 / character_string2）", field);
            return;
        }
        if (StringUtils.hasText(value)) {
            data.put(field, Map.of("value", value));
        }
    }

    /** 把微信返回翻成可读原因；errcode=0 返回 null 表示成功。 */
    static String describeError(Map<?, ?> resp) {
        if (resp == null) {
            return "微信未返回内容";
        }
        Object code = resp.get("errcode");
        int errcode = code instanceof Number n ? n.intValue() : 0;
        if (errcode == 0) {
            return null;
        }
        return switch (errcode) {
            case 47003 -> "模板字段与配置不一致（47003）：请核对 subscribe-fields 与申请到的模板字段名及类型";
            case 43101 -> "用户未订阅或订阅次数已用完（43101）：需小程序侧 requestSubscribeMessage 授权";
            case 40037 -> "模板 ID 无效（40037）：核对 WX_SUBSCRIBE_TEMPLATE_ID 是否为该小程序的模板";
            case 41030 -> "page 路径不正确（41030）：核对 WX_SUBSCRIBE_PAGE";
            default -> "errcode=" + errcode + " errmsg=" + resp.get("errmsg");
        };
    }

    private static String trunc(String s, int max) {
        if (s == null) {
            return "";
        }
        return s.length() <= max ? s : s.substring(0, max);
    }
}
