package com.rephone.pojo.dto;

/** 回收订单列表项。 */
public record RecycleOrderItem(String orderNo, String brandName, String modelName, String storage,
                               String conditionLabel, Long quoteFen, Long finalFen, Integer status,
                               String statusDesc, String createTime) {
}
