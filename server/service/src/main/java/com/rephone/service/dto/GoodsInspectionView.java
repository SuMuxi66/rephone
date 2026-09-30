package com.rephone.service.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品质检报告视图（出售端）。
 * {@code conditionLevel} 取自商品本身的成色，报告表不冗余该列，保证唯一来源。
 */
public record GoodsInspectionView(
        String conditionLevel,
        String reportNo,
        String inspector,
        LocalDateTime inspectedAt,
        Integer batteryHealth,
        String summary,
        List<String> images,
        List<Item> items,
        int normalCount,
        int abnormalCount) {

    /** 单条检查项。 */
    public record Item(String category, String name, String result, String note) {
    }
}
