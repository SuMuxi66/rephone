package com.rephone.pojo.dto;

import java.util.List;

/** 回收订单详情（含质检记录）。 */
public record RecycleOrderDetail(
        String orderNo,
        String brandName,
        String modelName,
        String storage,
        String conditionLabel,
        java.util.List<String> issues,
        Long quoteFen,
        Long finalFen,
        Integer status,
        String statusDesc,
        Integer pickupType,
        String pickupName,
        String pickupPhone,
        String pickupAddress,
        String expressCompany,
        String expressNo,
        String remark,
        String adminRemark,
        String createTime,
        List<InspectionItem> inspections) {

    public record InspectionItem(String result, Long finalFen, java.util.List<String> images, String createTime) {
    }
}
