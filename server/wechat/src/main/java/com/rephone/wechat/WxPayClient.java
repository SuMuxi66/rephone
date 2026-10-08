package com.rephone.wechat;

import com.rephone.common.exception.BizException;
import com.rephone.common.web.HttpGuard;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.parsers.DocumentBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.w3c.dom.Element;

/**
 * 微信支付 V2 客户端：JSAPI 统一下单 + 回调 XML 解析/验签。
 * - mock 模式（WXPAY_MOCK=true 或 wx mock-login）：不下单不出网，返回演示 prepay_id，开发闭环用；
 * - 真实模式：要求 MCH_ID / MCH_KEY / NOTIFY_URL 齐备，走 pay/unifiedorder（V2 XML，签名 MD5 为协议强制）。
 * 签名/验签为纯函数（signV2/verifySign），回调侧校验（金额/商户号/状态）由调用方完成。
 */
@Component
public class WxPayClient {

    private static final Logger log = LoggerFactory.getLogger(WxPayClient.class);

    public static final String UNIFIEDORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    private static final SecureRandom RANDOM = new SecureRandom();

    private final WxPayProperties payProps;
    private final WxProperties wxProps;
    private final RestClient restClient;

    public WxPayClient(WxPayProperties payProps, WxProperties wxProps) {
        this.payProps = payProps;
        this.wxProps = wxProps;
        org.springframework.http.client.SimpleClientHttpRequestFactory factory =
                new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(5).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(10).toMillis());
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    /** mock 模式判定：显式开关或未配真实登录（开发环境）。 */
    public boolean isMock() {
        return payProps.isMock() || wxProps.isMockLogin();
    }

    /** V2 JSAPI 统一下单，返回 prepay_id。mock 模式返回演示值，不出网。 */
    public String unifiedorder(String payNo, long totalFen, String openid) {
        if (isMock()) {
            return "MOCKPREPAY-" + payNo;
        }
        if (!StringUtils.hasText(payProps.getMchId()) || !StringUtils.hasText(payProps.getMchKey())
                || !StringUtils.hasText(payProps.getNotifyUrl())) {
            throw new BizException(50012, "微信支付未配置（需 WXPAY_MCH_ID/WXPAY_MCH_KEY/WXPAY_NOTIFY_URL）");
        }
        Map<String, String> params = new TreeMap<>();
        params.put("appid", wxProps.getAppid());
        params.put("mch_id", payProps.getMchId());
        params.put("nonce_str", nonce());
        params.put("body", "RePhone维修服务");
        params.put("out_trade_no", payNo);
        params.put("total_fee", String.valueOf(totalFen));
        params.put("spbill_create_ip", "127.0.0.1");
        params.put("notify_url", payProps.getNotifyUrl());
        params.put("trade_type", "JSAPI");
        params.put("openid", openid);
        params.put("sign", signV2(params, payProps.getMchKey()));
        HttpGuard.requirePublicHttps(UNIFIEDORDER_URL);
        String respXml = restClient.post()
                .uri(UNIFIEDORDER_URL)
                .contentType(MediaType.valueOf("text/xml;charset=UTF-8"))
                .body(buildXml(params))
                .retrieve()
                .body(String.class);
        Map<String, String> result = parseXml(respXml);
        if (!"SUCCESS".equals(result.get("return_code")) || !StringUtils.hasText(result.get("prepay_id"))) {
            log.error("[wxpay] 统一下单失败 payNo={} resp={}", payNo, result);
            throw new BizException(50013, "微信统一下单失败：" + result.getOrDefault("return_msg", "无返回"));
        }
        return result.get("prepay_id");
    }

    /** 小程序 wx.requestPayment 参数（含 payNo；真实模式含签名）。 */
    public Map<String, Object> jsapiParams(String payNo, long totalFen, String openid) {
        String prepayId = unifiedorder(payNo, totalFen, openid);
        String pkg = "prepay_id=" + prepayId;
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("payNo", payNo);
        if (isMock()) {
            result.put("mock", Boolean.TRUE);
            return result;
        }
        String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = nonce();
        Map<String, String> signParams = new TreeMap<>();
        signParams.put("appId", wxProps.getAppid());
        signParams.put("timeStamp", timeStamp);
        signParams.put("nonceStr", nonceStr);
        signParams.put("package", pkg);
        signParams.put("signType", "MD5");
        result.put("appId", wxProps.getAppid());
        result.put("timeStamp", timeStamp);
        result.put("nonceStr", nonceStr);
        result.put("package", pkg);
        result.put("signType", "MD5");
        result.put("paySign", signV2(signParams, payProps.getMchKey()));
        return result;
    }

    /** 微信支付 V2 签名：字典序拼 key，MD5 大写（签名算法由协议强制，非安全选型）。 */
    public static String signV2(Map<String, String> params, String key) {
        StringBuilder sb = new StringBuilder();
        new TreeMap<>(params).forEach((k, v) -> {
            if (StringUtils.hasText(v)) {
                sb.append(k).append('=').append(v).append('&');
            }
        });
        sb.append("key=").append(key);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5"); // mimosa-ignore
            byte[] digest = md.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : digest) {
                hex.append(Character.forDigit((b >> 4) & 0xF, 16)).append(Character.forDigit(b & 0xF, 16));
            }
            return hex.toString().toUpperCase();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /** 回调验签：剔除 sign 字段重算比对；密钥缺失一律失败。 */
    public static boolean verifySign(Map<String, String> params, String key) {
        if (!StringUtils.hasText(key) || !StringUtils.hasText(params.get("sign"))) {
            return false;
        }
        Map<String, String> copy = new TreeMap<>(params);
        copy.remove("sign");
        return signV2(copy, key).equals(params.get("sign"));
    }

    /** 解析微信 V2 XML 为扁平键值（仅一级元素，无嵌套）。 */
    public static Map<String, String> parseXml(String xml) {
        Map<String, String> result = new LinkedHashMap<>();
        if (!StringUtils.hasText(xml)) {
            return result;
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            Element root = factory.newDocumentBuilder()
                    .parse(new org.xml.sax.InputSource(new java.io.StringReader(xml))).getDocumentElement();
            var nodes = root.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                if (nodes.item(i) instanceof Element el) {
                    result.put(el.getTagName(), el.getTextContent());
                }
            }
            return result;
        } catch (Exception e) {
            throw new BizException(50014, "微信回调 XML 解析失败");
        }
    }

    /** 通用参数拼 V2 XML（值统一 CDATA，测试与下单共用）。 */
    public static String buildXml(Map<String, String> params) {
        StringBuilder sb = new StringBuilder("<xml>");
        new TreeMap<>(params).forEach((k, v) ->
                sb.append('<').append(k).append("><![CDATA[").append(v).append("]]></").append(k).append('>'));
        return sb.append("</xml>").toString();
    }

    public static String successXml() {
        return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    }

    public static String failXml(String message) {
        return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA["
                + message + "]]></return_msg></xml>";
    }

    private static String nonce() {
        byte[] buf = new byte[16];
        RANDOM.nextBytes(buf);
        return java.util.HexFormat.of().formatHex(buf);
    }
}
