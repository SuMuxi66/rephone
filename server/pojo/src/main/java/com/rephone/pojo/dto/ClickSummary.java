package com.rephone.pojo.dto;

/** 点击统计汇总：今日 / 近7天 / 近30天。 */
public record ClickSummary(
        ClickWindowStat today,
        ClickWindowStat last7,
        ClickWindowStat last30) {
}
