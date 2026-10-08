package com.rephone.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.rephone.common.exception.BizException;
import com.rephone.mapper.PaymentOrderMapper;
import com.rephone.pojo.entity.PaymentOrder;
import com.rephone.wechat.WxPayClient;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 公共支付服务：出售（SALE）/维修（REPAIR）订单支付统一入口。
 * start：建/复用待支付支付单，返回小程序 wx.requestPayment 参数
 * （mock 模式返回 {payNo, mock:true}，由业务侧模拟回调完成闭环；真实模式返回签名参数，等待微信回调）。
 * markPaySuccess：支付单幂等入账（回调验签/金额校验由回调控制器完成后调用）；
 * 订单状态推进按 order_type 由回调控制器分派到对应业务服务。
 * 真实微信 JSAPI（V2）统一下单在 WxPayClient，商户凭据未配置时保持 mock。
 */
@Service
public class PaymentService {

    public static final String BIZ_SALE = "SALE";
    public static final String BIZ_REPAIR = "REPAIR";

    /** payment_order.order_type：与 order_status_log 取值一致 */
    public static final int ORDER_TYPE_SALE = 20;
    public static final int ORDER_TYPE_REPAIR = 30;

    private static final long MAX_AMOUNT_FEN = 100_000_000L;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final PaymentOrderMapper paymentOrderMapper;
    private final WxPayClient wxPayClient;

    public PaymentService(PaymentOrderMapper paymentOrderMapper, WxPayClient wxPayClient) {
        this.paymentOrderMapper = paymentOrderMapper;
        this.wxPayClient = wxPayClient;
    }

    /** 发起支付：建/复用待支付单并返回支付参数（mock 模式带 mock=true，业务侧完成闭环后订单即已支付）。 */
    @Transactional
    public Map<String, Object> start(int orderType, String orderNo, long amountFen, Long tenantId, String openid) {
        validate(orderType, amountFen);
        PaymentOrder pending = paymentOrderMapper.selectOne(new LambdaQueryWrapper<PaymentOrder>()
                .eq(PaymentOrder::getOrderType, orderType)
                .eq(PaymentOrder::getOrderNo, orderNo)
                .eq(PaymentOrder::getStatus, PaymentOrder.STATUS_PENDING)
                .orderByDesc(PaymentOrder::getId)
                .last("limit 1"));
        String payNo;
        if (pending != null) {
            payNo = pending.getPayNo();
        } else {
            payNo = nextPayNo();
            PaymentOrder order = new PaymentOrder();
            order.setTenantId(tenantId);
            order.setOrderNo(orderNo);
            order.setOrderType(orderType);
            order.setPayNo(payNo);
            order.setAmountFen(amountFen);
            order.setStatus(PaymentOrder.STATUS_PENDING);
            paymentOrderMapper.insert(order);
        }
        Map<String, Object> params = wxPayClient.jsapiParams(payNo, amountFen, openid);
        if (wxPayClient.isMock()) {
            params.put("mock", Boolean.TRUE);
        }
        return params;
    }

    /**
     * 出售订单 mock 支付流水号（既有路径，格式 MOCKPAY-订单号，P6 测试依赖此格式）。
     * 出售迁移到 payment_order + 回调前继续使用；校验逻辑与 start 同源。
     */
    public String mockPayNo(String bizType, String orderNo, long amountFen) {
        if (!BIZ_SALE.equals(bizType) && !BIZ_REPAIR.equals(bizType)) {
            throw new BizException(40080, "未知支付业务类型");
        }
        if (amountFen <= 0 || amountFen > MAX_AMOUNT_FEN) {
            throw new BizException(40081, "支付金额无效");
        }
        return "MOCKPAY-" + orderNo;
    }

    /** 待支付单校验：订单存在、待支付状态、金额一致（mock 自动闭环与微信回调共用）。 */
    public PaymentOrder requirePendingByPayNo(String payNo) {
        PaymentOrder order = paymentOrderMapper.selectOne(new LambdaQueryWrapper<PaymentOrder>()
                .eq(PaymentOrder::getPayNo, payNo)
                .last("limit 1"));
        if (order == null) {
            throw new BizException(40408, "支付单不存在");
        }
        return order;
    }

    /** 支付单幂等入账：PENDING→SUCCESS（CAS）；已支付返回 false（重复通知）。 */
    @Transactional
    public boolean markPaySuccess(String payNo, String transactionId) {
        PaymentOrder order = requirePendingByPayNo(payNo);
        if (order.getStatus() == PaymentOrder.STATUS_SUCCESS) {
            return false;
        }
        PaymentOrder patch = new PaymentOrder();
        patch.setStatus(PaymentOrder.STATUS_SUCCESS);
        patch.setTransactionId(transactionId);
        patch.setPayTime(LocalDateTime.now());
        int updated = paymentOrderMapper.update(patch, new LambdaUpdateWrapper<PaymentOrder>()
                .eq(PaymentOrder::getId, order.getId())
                .eq(PaymentOrder::getStatus, PaymentOrder.STATUS_PENDING));
        return updated > 0;
    }

    private void validate(int orderType, long amountFen) {
        if (orderType != ORDER_TYPE_SALE && orderType != ORDER_TYPE_REPAIR) {
            throw new BizException(40080, "未知支付业务类型");
        }
        if (amountFen <= 0 || amountFen > MAX_AMOUNT_FEN) {
            throw new BizException(40081, "支付金额无效");
        }
    }

    /** P + 时间戳 + 6 位随机（≤32 字符，满足微信 out_trade_no 规则；订单号不同但前缀可读）。 */
    private static String nextPayNo() {
        return "P" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%06d", RANDOM.nextInt(1000000));
    }
}
