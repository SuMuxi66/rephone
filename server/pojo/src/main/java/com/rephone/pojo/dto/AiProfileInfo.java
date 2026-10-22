package com.rephone.pojo.dto;

/** AI 画像分析结果。 */
public record AiProfileInfo(
        Long userId,
        String analysis,
        String model,
        String generatedAt) {
}
