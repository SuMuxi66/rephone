package com.rephone.pojo.dto;

import java.util.List;

/**
 * 创建维修预约单请求。只传维修项目 ID，金额由服务端按 repair_item_price 实时计算（防篡改）；
 * MVP 仅支持上门维修（serviceType=10）。
 */
public record RepairOrderCreateRequest(
        Long modelId,
        List<Long> itemIds,
        Integer serviceType,
        String contactName,
        String contactPhone,
        String address,
        String appointTime,
        String remark,
        List<String> images) {
}
