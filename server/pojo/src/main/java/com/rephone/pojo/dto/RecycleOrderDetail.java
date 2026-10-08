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
        /** 快递100 取件任务号；上门取件单为空即代表预约没成功。 */
        String expressTaskNo,
        String remark,
        String adminRemark,
        /** 用户上传机器照片（type: front/back/screen/damage） */
        List<ImageItem> images,
        String createTime,
        List<InspectionItem> inspections) {

    /** 用户机器照片项。url 实为 COS key（mock 模式为本地临时路径）。 */
    public record ImageItem(String type, String url) {
    }

    public record InspectionItem(String result, Long finalFen, java.util.List<String> images, String createTime) {
    }
}
