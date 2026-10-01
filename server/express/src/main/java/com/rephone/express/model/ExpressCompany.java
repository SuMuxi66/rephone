package com.rephone.express.model;

/**
 * 快递公司字典项。
 *
 * @param com       快递100 公司编码，一律小写（见官方《快递公司编码表》）
 * @param name      公司名称（展示用）
 * @param needPhone true 表示查询物流轨迹时必须带收/寄件人手机号
 */
public record ExpressCompany(String com, String name, boolean needPhone) {
}
