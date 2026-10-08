package com.rephone.service;

import com.rephone.common.exception.BizException;
import org.springframework.stereotype.Service;

/**
 * 公共支付服务：出售（SALE）/维修（REPAIR）订单支付统一入口。
 * 当前为 mock 模式（演示流水号，格式与既有出售侧一致：MOCKPAY-订单号）；
 * 真实微信支付 JSAPI 统一下单在此扩展（商户凭据就绪后），业务侧只消费 payNo 与状态回调。
 */
@Service
public class PaymentService {

    public static final String BIZ_SALE = "SALE";
    public static final String BIZ_REPAIR = "REPAIR";

    private static final long MAX_AMOUNT_FEN = 100_000_000L;

    /**
     * 生成支付流水号并校验金额（分）。
     * mock 模式直接返回演示流水号；真实模式下此处将调用微信统一下单返回支付参数。
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
}
