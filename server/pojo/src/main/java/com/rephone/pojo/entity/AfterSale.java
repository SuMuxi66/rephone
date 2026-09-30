package com.rephone.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 售后单（仅退款 MVP）。状态：10 待审核 → 30 已退款（管理端同意即 mock 退款到账）；
 * 10 可被拒绝为 40、被用户撤销为 80。
 */
@TableName("after_sale")
public class AfterSale {

    public static final int STATUS_WAIT_REVIEW = 10;
    public static final int STATUS_REFUNDED = 30;
    public static final int STATUS_REJECTED = 40;
    public static final int STATUS_CANCELED = 80;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private String asNo;

    private String orderNo;

    private Long userId;

    /** 10 仅退款 */
    private Integer type;

    private String reason;

    private Integer status;

    private Long refundFen;

    private String adminRemark;

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

    public String getAsNo() {
        return asNo;
    }

    public void setAsNo(String asNo) {
        this.asNo = asNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getRefundFen() {
        return refundFen;
    }

    public void setRefundFen(Long refundFen) {
        this.refundFen = refundFen;
    }

    public String getAdminRemark() {
        return adminRemark;
    }

    public void setAdminRemark(String adminRemark) {
        this.adminRemark = adminRemark;
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
