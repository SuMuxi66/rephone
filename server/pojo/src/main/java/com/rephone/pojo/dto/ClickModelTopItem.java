package com.rephone.pojo.dto;

/** 机型点击榜单行（机型无曝光口径，只有点击与两类订单转化）。 */
public record ClickModelTopItem(
        Long modelId,
        String name,
        long clicks,
        long recycleOrders,
        long repairOrders) {
}
