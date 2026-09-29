package com.rephone.pojo.dto;

import java.util.List;

/** 管理端机型行（含图片与各内存基准价，basePriceYuan 单位为元）。 */
public record AdminModelItem(Long id, String name, String image, Integer releaseYear,
                             List<AdminModelPrice> prices) {

    /** 内存基准价。priceYuan 为元（与 quote_rule.numeric_value 同单位）。 */
    public record AdminModelPrice(String storage, java.math.BigDecimal priceYuan) {
    }
}
