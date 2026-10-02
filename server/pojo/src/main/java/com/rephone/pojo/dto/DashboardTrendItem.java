package com.rephone.pojo.dto;

/** 看板趋势行：某天三线单量。 */
public record DashboardTrendItem(
        String date,
        Long recycle,
        Long sale,
        Long repair) {
}
