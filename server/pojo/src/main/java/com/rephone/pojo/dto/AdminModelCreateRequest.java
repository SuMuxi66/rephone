package com.rephone.pojo.dto;

import java.math.BigDecimal;
import java.util.List;

/** 管理端创建机型请求：brandId + 机型信息 + 各内存基准价（元）。 */
public record AdminModelCreateRequest(Long brandId, String name, Integer releaseYear,
                                      List<AdminModelItem.AdminModelPrice> prices) {
}
