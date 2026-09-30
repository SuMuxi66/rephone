package com.rephone.service.dto;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;

/**
 * 商品机型属性（出售端增强）：品牌 / 内存 / 成色 / 标签。
 * 全部可空，空白串归一为 null；标签允许以英文逗号分隔多项，写入前 trim + 去重。
 */
public record GoodsAttrs(String brand, String storage, String conditionLevel, String tags) {

    /** 归一化：去空白、空串转 null、标签去重。 */
    public static GoodsAttrs of(String brand, String storage, String conditionLevel, String tags) {
        return new GoodsAttrs(
                trimToNull(brand),
                trimToNull(storage),
                trimToNull(conditionLevel),
                normalizeTags(tags));
    }

    /** 空属性（整体覆盖语义下用于清空）。 */
    public static GoodsAttrs empty() {
        return new GoodsAttrs(null, null, null, null);
    }

    private static String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String normalizeTags(String tags) {
        if (!StringUtils.hasText(tags)) {
            return null;
        }
        String joined = Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.joining(","));
        return joined.isEmpty() ? null : joined;
    }
}
