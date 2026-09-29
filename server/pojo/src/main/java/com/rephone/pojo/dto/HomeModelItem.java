package com.rephone.pojo.dto;

/** 首页热门机型。maxPriceFen 为该机型最贵内存档的基准回收价（分），前端负责展示。 */
public record HomeModelItem(Long brandId, String brandName, String modelName, Long maxPriceFen) {
}
