package com.rephone.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

/** 用户行为事件（小程序启动/商品曝光/商品浏览/机型点击），PV 口径长期留存。 */
@TableName("click_event")
public class ClickEvent {

    public static final int TYPE_APP_OPEN = 1;
    public static final int TYPE_GOODS_EXPOSE = 2;
    public static final int TYPE_GOODS_VIEW = 3;
    public static final int TYPE_MODEL_CLICK = 4;

    public static final int TARGET_NONE = 0;
    public static final int TARGET_GOODS = 1;
    public static final int TARGET_MODEL = 2;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private Long userId;

    private Integer eventType;

    private Integer targetType;

    private Long targetId;

    /** 商品品牌快照（写入时按商品快照） */
    private String brand;

    /** 商品价格快照（分） */
    private Long priceFen;

    private String deviceId;

    private String page;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getEventType() {
        return eventType;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    public Integer getTargetType() {
        return targetType;
    }

    public void setTargetType(Integer targetType) {
        this.targetType = targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Long getPriceFen() {
        return priceFen;
    }

    public void setPriceFen(Long priceFen) {
        this.priceFen = priceFen;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
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
