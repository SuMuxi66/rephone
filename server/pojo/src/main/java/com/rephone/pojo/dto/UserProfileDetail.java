package com.rephone.pojo.dto;

import java.util.List;
import java.util.Map;

/** 用户画像明细：打开时间线、浏览记录、偏好分布、三线订单数与 AI 分析。 */
public record UserProfileDetail(
        Long userId,
        String nickname,
        String avatarUrl,
        String phone,
        List<String> openTimes,
        List<UserViewItem> views,
        Map<String, Long> brandDist,
        Map<String, Long> priceBandDist,
        long saleOrders,
        long recycleOrders,
        long repairOrders,
        String aiAnalysis,
        String aiModel,
        String aiGeneratedAt) {
}
