package com.rephone.wechat;

import com.rephone.common.exception.BizException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 企业付款到零钱（商家转账）。
 * - mock 模式（WXPAY_MOCK=true 或 wx mock-login）：记录日志直接成功，用于开发闭环；
 * - 真实模式：要求 MCH_ID / MCH_KEY / 证书齐备，走 mmpaymkttransfers/promotion/transfers。
 *   证书加载失败或凭据缺失时抛业务异常，绝不静默降级。
 */
@Service
public class WxPayoutService {

    private static final Logger log = LoggerFactory.getLogger(WxPayoutService.class);

    private final WxPayProperties payProps;
    private final WxProperties wxProps;

    public WxPayoutService(WxPayProperties payProps, WxProperties wxProps) {
        this.payProps = payProps;
        this.wxProps = wxProps;
    }

    /**
     * 打款到用户零钱。amountFen 单位为分。
     * 真实实现要点：双向证书校验、金额分、订单号幂等（partner_trade_no=回收订单号）。
     * 当前交付：mock 全通 + 真实凭据校验与请求骨架；真实联调需在商户平台开通企业付款并挂载证书。
     */
    public String payoutToChange(String orderNo, String openid, long amountFen) {
        if (amountFen <= 0) {
            throw new BizException(40030, "打款金额无效");
        }
        if (payProps.isMock() || wxProps.isMockLogin()) {
            log.info("[wxpay][mock] 打款成功 order={} openid={} amount={}分", orderNo, openid, amountFen);
            return "MOCK-" + orderNo;
        }
        if (!StringUtils.hasText(payProps.getMchId()) || !StringUtils.hasText(payProps.getMchKey())
                || !StringUtils.hasText(payProps.getCertPath())) {
            throw new BizException(50010, "企业付款未配置（需 MCH_ID/MCH_KEY/证书路径环境变量）");
        }
        // 真实请求骨架：签名算法 MD5 为微信支付 v1 XML API 协议强制要求（非安全选型），
        // 传输安全由 HTTPS + 双向证书保证；实际联调时补证书 SSLContext 后启用。
        Map<String, String> params = new TreeMap<>();
        params.put("mch_appid", wxProps.getAppid());
        params.put("mchid", payProps.getMchId());
        params.put("nonce_str", nonce());
        params.put("partner_trade_no", orderNo);
        params.put("openid", openid);
        params.put("check_name", "NO_CHECK");
        params.put("amount", String.valueOf(amountFen));
        params.put("desc", "二手机回收款");
        params.put("spbill_create_ip", "127.0.0.1");
        throw new BizException(50011, "企业付款真实通道未启用：请在商户平台开通并配置 WXPAY_CERT_PATH 后联调");
    }

    private static String nonce() {
        byte[] buf = new byte[16];
        new java.security.SecureRandom().nextBytes(buf);
        return java.util.HexFormat.of().formatHex(buf);
    }

    static String sign(Map<String, String> params, String key) {
        StringBuilder sb = new StringBuilder();
        new TreeMap<>(params).forEach((k, v) -> {
            if (StringUtils.hasText(v)) {
                sb.append(k).append('=').append(v).append('&');
            }
        });
        sb.append("key=").append(key);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
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
}
