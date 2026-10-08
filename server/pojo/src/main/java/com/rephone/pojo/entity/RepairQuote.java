package com.rephone.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 维修报价单（检测→报价→用户确认→支付→维修）。金额存元 DECIMAL(10,2)，API 传输一律分。 */
@TableName("repair_quote")
public class RepairQuote {

    /** 报价单状态：待用户确认 */
    public static final int STATUS_PENDING = 1;
    /** 报价单状态：用户已接受 */
    public static final int STATUS_ACCEPTED = 2;
    /** 报价单状态：用户已拒绝 */
    public static final int STATUS_REJECTED = 3;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private String repairOrderNo;

    /** 报价金额（元） */
    private BigDecimal quoteAmount;

    /** 维修说明（故障原因/维修方案） */
    private String description;

    private String imagesJson;

    /** 报价操作人（管理端账号；机器凭证为 NULL） */
    private Long operatorId;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

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

    public String getRepairOrderNo() {
        return repairOrderNo;
    }

    public void setRepairOrderNo(String repairOrderNo) {
        this.repairOrderNo = repairOrderNo;
    }

    public BigDecimal getQuoteAmount() {
        return quoteAmount;
    }

    public void setQuoteAmount(BigDecimal quoteAmount) {
        this.quoteAmount = quoteAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagesJson() {
        return imagesJson;
    }

    public void setImagesJson(String imagesJson) {
        this.imagesJson = imagesJson;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
