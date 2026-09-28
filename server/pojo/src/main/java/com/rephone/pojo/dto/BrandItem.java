package com.rephone.pojo.dto;

import java.util.List;

/** 品牌列表项。 */
public record BrandItem(Long id, String name, String logo) {

    public static List<BrandItem> emptyList() {
        return List.of();
    }
}
