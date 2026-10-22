package com.rephone.pojo.dto;

/** 单窗口点击统计。ctr=点击/曝光，无曝光时为 null。 */
public record ClickWindowStat(
        long appOpens,
        long exposes,
        long clicks,
        Double ctr,
        long saleOrders,
        long recycleOrders,
        long repairOrders) {
}
