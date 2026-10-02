package com.rephone.pojo.dto;

/** 数据看板汇总。金额单位分。 */
public record DashboardSummary(
        Long todayOrders,
        Long yesterdayOrders,
        Long last7Orders,
        Long last30Orders,
        Long recycleTotal,
        Long saleTotal,
        Long repairTotal,
        Long pendingRecycle,
        Long pendingRepair,
        Long pendingShip,
        Long pendingReview,
        Long netFen30,
        Long recyclePayFen30,
        Long saleIncomeFen30,
        Long repairIncomeFen30) {
}
