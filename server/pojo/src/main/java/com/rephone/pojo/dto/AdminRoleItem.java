package com.rephone.pojo.dto;

import java.util.List;

/** 管理端角色项（permissions 已解析为数组）。 */
public record AdminRoleItem(
        Long id,
        String roleCode,
        String roleName,
        List<String> permissions,
        Integer status,
        Integer builtIn,
        String remark,
        String createTime) {
}
