package com.rephone.pojo.dto;

/** 维修单列表行。totalFen 为合计费用（分）。 */
public record RepairOrderItem(String orderNo, String brandName, String modelName,
                              Long totalFen, Integer status, String createTime) {
}
