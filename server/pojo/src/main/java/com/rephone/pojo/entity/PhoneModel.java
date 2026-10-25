package com.rephone.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/** 手机机型。 */
@TableName("phone_model")
public class PhoneModel {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long tenantId;

    private Long brandId;

    private String name;

    /** 机型图片 URL（空则前端回退品牌 logo） */
    private String image;

    private Integer releaseYear;

    /** 发布月份 1-12 */
    private Integer releaseMonth;

    /** 产品系列（如 Mate系列/数字系列） */
    private String series;

    /** 处理器 */
    private String chip;

    /** 运行内存（如 12GB/16GB） */
    private String ram;

    /** 机身存储（如 256GB/512GB/1TB） */
    private String storage;

    /** 电池容量(mAh) */
    private Integer batteryMah;

    /** 有线快充功率(W) */
    private Integer chargeW;

    /** 无线充电功率(W)，0=不支持 */
    private Integer wirelessW;

    /** 屏幕尺寸（如 6.7英寸） */
    private String screenSize;

    /** 分辨率（如 2832×1280） */
    private String resolution;

    /** 刷新率（如 1-120 LTPO / 144） */
    private String refreshRate;

    /** 后置主摄 */
    private String rearCamera;

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

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Integer getReleaseMonth() {
        return releaseMonth;
    }

    public void setReleaseMonth(Integer releaseMonth) {
        this.releaseMonth = releaseMonth;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getChip() {
        return chip;
    }

    public void setChip(String chip) {
        this.chip = chip;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public Integer getBatteryMah() {
        return batteryMah;
    }

    public void setBatteryMah(Integer batteryMah) {
        this.batteryMah = batteryMah;
    }

    public Integer getChargeW() {
        return chargeW;
    }

    public void setChargeW(Integer chargeW) {
        this.chargeW = chargeW;
    }

    public Integer getWirelessW() {
        return wirelessW;
    }

    public void setWirelessW(Integer wirelessW) {
        this.wirelessW = wirelessW;
    }

    public String getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(String screenSize) {
        this.screenSize = screenSize;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getRefreshRate() {
        return refreshRate;
    }

    public void setRefreshRate(String refreshRate) {
        this.refreshRate = refreshRate;
    }

    public String getRearCamera() {
        return rearCamera;
    }

    public void setRearCamera(String rearCamera) {
        this.rearCamera = rearCamera;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
