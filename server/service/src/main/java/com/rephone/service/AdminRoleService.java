package com.rephone.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rephone.mapper.RoleMapper;
import com.rephone.pojo.entity.Role;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 管理端角色解析：登录准入（角色是否为启用的管理角色）与权限点解析。
 * ADMIN 平台超管固定全权（代码写死），不读角色表，防止误改权限锁死平台。
 */
@Service
public class AdminRoleService {

    private static final Logger log = LoggerFactory.getLogger(AdminRoleService.class);

    /** 平台超管角色编码，与 user.role 取值一致。 */
    public static final String SUPER_ROLE = "ADMIN";

    private final RoleMapper roleMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AdminRoleService(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    /** 该角色编码是否允许登录管理端（存在且启用的角色，或平台超管）。 */
    public boolean isAdminRole(String roleCode) {
        if (!StringUtils.hasText(roleCode)) {
            return false;
        }
        if (SUPER_ROLE.equals(roleCode)) {
            return true;
        }
        Role role = roleMapper.selectOne(new LambdaQueryWrapper<Role>()
                .eq(Role::getRoleCode, roleCode)
                .eq(Role::getStatus, 1)
                .last("limit 1"));
        return role != null;
    }

    /** 权限点集合：ADMIN 固定 ["*"]；角色缺失/停用/JSON 异常返回空列表（无权限）。 */
    public List<String> permissionsOf(String roleCode) {
        if (SUPER_ROLE.equals(roleCode)) {
            return List.of("*");
        }
        if (!StringUtils.hasText(roleCode)) {
            return List.of();
        }
        Role role = roleMapper.selectOne(new LambdaQueryWrapper<Role>()
                .eq(Role::getRoleCode, roleCode)
                .eq(Role::getStatus, 1)
                .last("limit 1"));
        if (role == null || !StringUtils.hasText(role.getPermissionsJson())) {
            return List.of();
        }
        try {
            List<String> permissions = objectMapper.readValue(role.getPermissionsJson(),
                    new TypeReference<List<String>>() { });
            List<String> cleaned = new ArrayList<>();
            for (String p : permissions) {
                if (StringUtils.hasText(p)) {
                    cleaned.add(p.trim());
                }
            }
            return List.copyOf(cleaned);
        } catch (Exception e) {
            log.error("[rbac] 角色权限 JSON 解析失败 roleCode={} json={}", roleCode, role.getPermissionsJson(), e);
            return List.of();
        }
    }

    /** 是否满足所需权限点；required 为 null 表示仅需登录。 */
    public boolean hasPermission(String roleCode, String required) {
        if (required == null) {
            return true;
        }
        for (String p : permissionsOf(roleCode)) {
            if ("*".equals(p) || p.equals(required)) {
                return true;
            }
        }
        return false;
    }
}
