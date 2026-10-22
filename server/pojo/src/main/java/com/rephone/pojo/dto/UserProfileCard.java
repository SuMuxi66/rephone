package com.rephone.pojo.dto;

/** 用户画像卡片行（列表页）。 */
public record UserProfileCard(
        Long userId,
        String nickname,
        String avatarUrl,
        String phone,
        long openCount,
        long viewCount,
        String lastOpenTime,
        String topBrand,
        String topPriceBand,
        Long avgPriceFen,
        String aiSummary) {
}
