package com.rephone.pojo.dto;

import java.util.List;

/** 创建回收订单请求。估价信息由前端回传，服务端按 quote_rule 复核（复核失败拒绝下单）。 */
public record RecycleOrderCreateRequest(
        Long modelId,
        String storage,
        String condition,
        String screenCondition,
        List<String> issues,
        Long quoteFen,
        Integer pickupType,
        String pickupName,
        String pickupPhone,
        String pickupAddress,
        String remark) {
}
