package com.rephone.controller;

import com.rephone.pojo.entity.PaymentOrder;
import com.rephone.service.PaymentService;
import com.rephone.service.RepairOrderService;
import com.rephone.wechat.WxPayClient;
import com.rephone.wechat.WxPayProperties;
import com.rephone.wechat.WxProperties;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信支付 V2 回调（匿名入口）：验签 → 商户号/appid/金额比对 → 支付单幂等入账 → 按订单类型分派推进。
 * 协议要求无论处理结果都返回 XML（FAIL 时微信会重试通知）；订单推进 CAS 幂等，重复通知安全。
 * 分派：REPAIR → 维修单 38→30；SALE 预留（出售当前为 mock 内联支付）。
 */
@RestController
public class WxPayCallbackController {

    private static final Logger log = LoggerFactory.getLogger(WxPayCallbackController.class);

    private final WxPayProperties payProps;
    private final WxProperties wxProps;
    private final PaymentService paymentService;
    private final RepairOrderService repairOrderService;

    public WxPayCallbackController(WxPayProperties payProps, WxProperties wxProps,
                                   PaymentService paymentService, RepairOrderService repairOrderService) {
        this.payProps = payProps;
        this.wxProps = wxProps;
        this.paymentService = paymentService;
        this.repairOrderService = repairOrderService;
    }

    @PostMapping(value = "/api/callback/wxpay", produces = MediaType.TEXT_XML_VALUE)
    public ResponseEntity<String> notify(@RequestBody String body) {
        Map<String, String> params;
        try {
            params = WxPayClient.parseXml(body);
        } catch (Exception e) {
            log.error("[wxpay][callback] XML 解析失败");
            return xml(WxPayClient.failXml("报文解析失败"));
        }
        if (!WxPayClient.verifySign(params, payProps.getMchKey())) {
            log.error("[wxpay][callback] 签名验证失败 out_trade_no={}", params.get("out_trade_no"));
            return xml(WxPayClient.failXml("签名验证失败"));
        }
        if (!"SUCCESS".equals(params.get("return_code")) || !"SUCCESS".equals(params.get("result_code"))) {
            // 通讯成功但支付失败：无业务数据可入账，直接应答
            log.warn("[wxpay][callback] 非成功通知 result={}/{}", params.get("return_code"), params.get("result_code"));
            return xml(WxPayClient.successXml());
        }
        if (StringUtils.hasText(payProps.getMchId()) && !payProps.getMchId().equals(params.get("mch_id"))) {
            log.error("[wxpay][callback] 商户号不符 expect={} actual={}", payProps.getMchId(), params.get("mch_id"));
            return xml(WxPayClient.failXml("商户号不符"));
        }
        if (StringUtils.hasText(wxProps.getAppid()) && !wxProps.getAppid().equals(params.get("appid"))) {
            log.error("[wxpay][callback] appid 不符 expect={} actual={}", wxProps.getAppid(), params.get("appid"));
            return xml(WxPayClient.failXml("appid 不符"));
        }
        PaymentOrder payment;
        try {
            payment = paymentService.requirePendingByPayNo(params.get("out_trade_no"));
        } catch (Exception e) {
            log.error("[wxpay][callback] 支付单不存在 out_trade_no={}", params.get("out_trade_no"));
            return xml(WxPayClient.failXml("支付单不存在"));
        }
        long totalFee;
        try {
            totalFee = Long.parseLong(params.getOrDefault("total_fee", "0"));
        } catch (NumberFormatException e) {
            totalFee = -1;
        }
        if (totalFee != payment.getAmountFen()) {
            log.error("[wxpay][callback] 金额不符 payNo={} expect={} actual={}",
                    payment.getPayNo(), payment.getAmountFen(), totalFee);
            return xml(WxPayClient.failXml("金额不符"));
        }
        boolean changed = paymentService.markPaySuccess(payment.getPayNo(), params.get("transaction_id"));
        if (changed && payment.getOrderType() == PaymentService.ORDER_TYPE_REPAIR) {
            // 入账成功才推进订单；重复通知 changed=false 自然跳过
            repairOrderService.markPaid(payment.getOrderNo(), payment.getPayNo());
        }
        return xml(WxPayClient.successXml());
    }

    private static ResponseEntity<String> xml(String body) {
        return ResponseEntity.ok().contentType(MediaType.TEXT_XML).body(body);
    }
}
