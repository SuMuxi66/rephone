package com.rephone.pojo.dto;

import java.util.List;

/** 维修单详情。items 为下单时项目快照；warrantyDays 质保天数（自维修完成日起）。 */
public record RepairOrderDetail(
        String orderNo,
        String brandName,
        String modelName,
        List<RepairLine> items,
        Long totalFen,
        Integer status,
        Integer serviceType,
        String contactName,
        String contactPhone,
        String address,
        String appointTime,
        String remark,
        List<String> images,
        Integer warrantyDays,
        String createTime) {

    /** 维修项目行快照。 */
    public record RepairLine(Long itemId, String name, Long priceFen) {
    }
}
