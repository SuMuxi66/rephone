package com.rephone.pojo.dto;

/** 管理端租户列表项。 */
public record AdminTenantItem(
        Long id,
        String name,
        Integer status,
        Long adminCount,
        String createTime) {
}
