package com.rephone.pojo.dto;

/** 商品点击榜单行。ctr=点击/曝光，无曝光时为 null。 */
public record ClickTopItem(
        Long goodsId,
        String name,
        long exposes,
        long clicks,
        Double ctr,
        long orders) {
}
