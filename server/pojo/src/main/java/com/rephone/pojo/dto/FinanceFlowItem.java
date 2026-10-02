package com.rephone.pojo.dto;

/**
 * 财务流水行（每笔订单一行）。
 * biz：10 回收（打款支出） 20 出售（货款收入） 30 维修（服务收入）；
 * direction：income 收入 / payout 支出（回收打款）/ refund 退款。
 */
public record FinanceFlowItem(
        String orderNo,
        Integer biz,
        Long amountFen,
        String direction,
        Integer status,
        String createTime) {
}
