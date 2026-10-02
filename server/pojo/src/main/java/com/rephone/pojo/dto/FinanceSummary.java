package com.rephone.pojo.dto;

/**
 * 财务对账汇总（口径见 AdminFinanceController/FinanceService 注释）。
 * 金额单位分；netFen = 出售收款 + 维修收款 − 出售退款 − 回收打款。
 */
public record FinanceSummary(
        Long recyclePayFen,
        Long recycleCount,
        Long saleIncomeFen,
        Long saleCount,
        Long saleRefundFen,
        Long refundCount,
        Long repairIncomeFen,
        Long repairCount,
        Long netFen,
        String from,
        String to) {
}
