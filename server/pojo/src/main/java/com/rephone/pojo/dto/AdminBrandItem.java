package com.rephone.pojo.dto;

/** 管理端品牌行（modelCount 为该品牌下机型数量）。 */
public record AdminBrandItem(Long id, String name, Long modelCount) {
}
