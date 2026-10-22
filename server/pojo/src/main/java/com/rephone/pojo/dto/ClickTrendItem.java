package com.rephone.pojo.dto;

/** 点击统计单日趋势行。 */
public record ClickTrendItem(
        String date,
        long appOpens,
        long exposes,
        long clicks) {
}
