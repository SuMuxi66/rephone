package com.rephone.pojo.dto;

import java.util.List;

/** 估价计算请求。condition 为成色代码（如 COND_95），issues 为故障代码列表。 */
public record QuoteCalculateRequest(Long modelId, String storage, String condition, List<String> issues) {
}
