package com.rephone.pojo.dto;

/** 用户画像明细页的单条商品浏览记录。 */
public record UserViewItem(
        Long goodsId,
        String goodsName,
        String brand,
        Long priceFen,
        String time) {
}
