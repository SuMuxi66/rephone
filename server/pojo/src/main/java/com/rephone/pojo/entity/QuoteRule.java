package com.rephone.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;

/**
 * 估价规则。rule_type：10=机型+内存基准价(元)、20=成色系数、30=故障扣减(元)。
 * model_id=0 表示全局规则（成色/故障）。
 */
@TableName("quote_rule")
public class QuoteRule {

    public static final int TYPE_BASE_PRICE = 10;
    public static final int TYPE_CONDITION_FACTOR = 20;
    public static final int TYPE_ISSUE_DEDUCTION = 30;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private Integer ruleType;

    private Long modelId;

    private String optionKey;

    private String optionLabel;

    private BigDecimal numericValue;

    private Integer sort;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getOptionKey() {
        return optionKey;
    }

    public void setOptionKey(String optionKey) {
        this.optionKey = optionKey;
    }

    public String getOptionLabel() {
        return optionLabel;
    }

    public void setOptionLabel(String optionLabel) {
        this.optionLabel = optionLabel;
    }

    public BigDecimal getNumericValue() {
        return numericValue;
    }

    public void setNumericValue(BigDecimal numericValue) {
        this.numericValue = numericValue;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
