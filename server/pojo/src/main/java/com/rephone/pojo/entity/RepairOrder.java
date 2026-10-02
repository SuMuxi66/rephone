package com.rephone.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/**
 * 维修工单。service_type=10 上门：10 待确认 → 20 已预约 → 30 维修中 → 40 待验收 → 50 已完成；
 * service_type=20 寄修：10 待确认 → 20 待寄出 → 25 已寄出 → 30 维修中 → 40 待回寄 → 45 回寄中 → 50 已完成。
 * 取消：上门仅 10；寄修 10/20（寄出前）。所有状态流转必须写 order_status_log（order_type=30）。
 */
@TableName("repair_order")
public class RepairOrder {

    public static final int STATUS_WAIT_CONFIRM = 10;
    public static final int STATUS_APPOINTED = 20;
    public static final int STATUS_SHIPPED = 25;
    public static final int STATUS_REPAIRING = 30;
    public static final int STATUS_WAIT_ACCEPT = 40;
    public static final int STATUS_RETURNING = 45;
    public static final int STATUS_DONE = 50;
    public static final int STATUS_CANCELED = 80;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private String orderNo;

    private Long userId;

    private String openid;

    private Long modelId;

    private String brandName;

    private String modelName;

    private String itemsJson;

    private Long totalFen;

    private Integer status;

    private Integer serviceType;

    private String contactName;

    private String contactPhone;

    private String address;

    private String appointTime;

    private String expressCom;

    private String expressCompany;

    private String expressNo;

    private String expressTrace;

    private LocalDateTime traceAt;

    private String returnExpressCom;

    private String returnExpressCompany;

    private String returnExpressNo;

    private String returnExpressTrace;

    private LocalDateTime returnTraceAt;

    private String remark;

    private String imagesJson;

    private Integer warrantyDays;

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

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getItemsJson() {
        return itemsJson;
    }

    public void setItemsJson(String itemsJson) {
        this.itemsJson = itemsJson;
    }

    public Long getTotalFen() {
        return totalFen;
    }

    public void setTotalFen(Long totalFen) {
        this.totalFen = totalFen;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getServiceType() {
        return serviceType;
    }

    public void setServiceType(Integer serviceType) {
        this.serviceType = serviceType;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAppointTime() {
        return appointTime;
    }

    public void setAppointTime(String appointTime) {
        this.appointTime = appointTime;
    }

    public String getExpressCom() {
        return expressCom;
    }

    public void setExpressCom(String expressCom) {
        this.expressCom = expressCom;
    }

    public String getExpressCompany() {
        return expressCompany;
    }

    public void setExpressCompany(String expressCompany) {
        this.expressCompany = expressCompany;
    }

    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }

    public String getExpressTrace() {
        return expressTrace;
    }

    public void setExpressTrace(String expressTrace) {
        this.expressTrace = expressTrace;
    }

    public LocalDateTime getTraceAt() {
        return traceAt;
    }

    public void setTraceAt(LocalDateTime traceAt) {
        this.traceAt = traceAt;
    }

    public String getReturnExpressCom() {
        return returnExpressCom;
    }

    public void setReturnExpressCom(String returnExpressCom) {
        this.returnExpressCom = returnExpressCom;
    }

    public String getReturnExpressCompany() {
        return returnExpressCompany;
    }

    public void setReturnExpressCompany(String returnExpressCompany) {
        this.returnExpressCompany = returnExpressCompany;
    }

    public String getReturnExpressNo() {
        return returnExpressNo;
    }

    public void setReturnExpressNo(String returnExpressNo) {
        this.returnExpressNo = returnExpressNo;
    }

    public String getReturnExpressTrace() {
        return returnExpressTrace;
    }

    public void setReturnExpressTrace(String returnExpressTrace) {
        this.returnExpressTrace = returnExpressTrace;
    }

    public LocalDateTime getReturnTraceAt() {
        return returnTraceAt;
    }

    public void setReturnTraceAt(LocalDateTime returnTraceAt) {
        this.returnTraceAt = returnTraceAt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getImagesJson() {
        return imagesJson;
    }

    public void setImagesJson(String imagesJson) {
        this.imagesJson = imagesJson;
    }

    public Integer getWarrantyDays() {
        return warrantyDays;
    }

    public void setWarrantyDays(Integer warrantyDays) {
        this.warrantyDays = warrantyDays;
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
