package com.rephone.pojo.dto;

import java.util.List;

/** 管理端机型行（含图片、参数与各内存基准价，basePriceYuan 单位为元）。 */
public record AdminModelItem(Long id, String name, String image, Integer releaseYear,
                             AdminModelSpec spec, List<AdminModelPrice> prices) {

    /**
     * 机型参数（与 phone_model 参数列一一对应）。
     * 注意 chip 是参数 seed（data-model-specs.sql guarded UPDATE）的判空字段：
     * 管理端把 chip 清空后，下次 seed 重放会重新填充该行。
     */
    public record AdminModelSpec(Integer releaseMonth, String series, String chip, String ram,
                                 String storage, Integer batteryMah, Integer chargeW, Integer wirelessW,
                                 String screenSize, String resolution, String refreshRate, String rearCamera) {
    }

    /** 内存基准价。priceYuan 为元（与 quote_rule.numeric_value 同单位）。 */
    public record AdminModelPrice(String storage, java.math.BigDecimal priceYuan) {
    }
}
