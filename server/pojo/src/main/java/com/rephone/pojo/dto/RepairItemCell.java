package com.rephone.pojo.dto;

/** 维修项目单元。priceFen 为该机型上门维修价（分），后端由 repair_item_price 实时换算。 */
public record RepairItemCell(Long itemId, String name, Long priceFen) {
}
