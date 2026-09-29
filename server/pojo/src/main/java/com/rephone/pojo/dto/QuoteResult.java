package com.rephone.pojo.dto;

import java.util.List;

/** 估价结果。priceFen 为预估回收价（单位：分，由后端元换算），前端负责展示。 */
public record QuoteResult(Long modelId, String modelName, String brandName, String storage,
                          String condition, String conditionLabel, String screenLabel,
                          List<String> issueLabels, Long priceFen) {
}
