package com.rephone.service.dto;

import java.util.List;

/**
 * 管理端保存商品质检报告入参（整体覆盖语义）。
 * {@code items} 为空表示清空该商品的质检报告。
 */
public record GoodsInspectionSaveRequest(
        String inspector,
        String inspectedAt,
        Integer batteryHealth,
        String summary,
        List<String> images,
        List<Item> items) {

    /** 单条检查项；name 与 result 必填，缺一的行会被忽略。 */
    public record Item(String category, String name, String result, String note) {
    }
}
