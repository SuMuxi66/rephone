package com.rephone.pojo.dto;

/** 管理端用户地址列表行（userName 为冗余展示字段）。 */
public record AdminAddressItem(Long id, Long userId, String userName, String name, String phone,
                               String region, String detail, Integer isDefault, String createTime) {
}
