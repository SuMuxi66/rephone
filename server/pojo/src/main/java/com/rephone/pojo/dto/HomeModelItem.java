package com.rephone.pojo.dto;

/** 首页热门机型。maxPriceFen 为该机型最贵内存档的基准回收价（分），image 为机型图 URL。 */
public record HomeModelItem(Long brandId, String brandName, String modelName,
                            String image, Long maxPriceFen) {
}
