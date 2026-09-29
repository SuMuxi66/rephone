package com.rephone.pojo.dto;

import java.util.List;

/**
 * 估价计算请求。condition 为成色代码（如 COND_95），screenCondition 为屏幕状态代码（如 SCR_LIGHT，
 * 可空=无瑕疵），issues 为故障代码列表。
 */
public record QuoteCalculateRequest(Long modelId, String storage, String condition,
                                    String screenCondition, List<String> issues) {
}
