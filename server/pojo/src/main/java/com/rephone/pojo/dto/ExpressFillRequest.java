package com.rephone.pojo.dto;

/**
 * 填写/修改运单号请求。
 *
 * @param expressCompany 快递公司展示名；expressCom 为空时按名字反查编码（兼容旧客户端）
 * @param expressNo      运单号
 * @param expressCom     快递100 公司编码（小写），优先于 expressCompany
 */
public record ExpressFillRequest(String expressCompany, String expressNo, String expressCom) {
}
