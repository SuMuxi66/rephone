package com.rephone.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rephone.common.exception.BizException;
import com.rephone.mapper.RoleMapper;
import com.rephone.mapper.UserMapper;
import com.rephone.pojo.dto.AdminRoleItem;
import com.rephone.pojo.entity.Role;
import com.rephone.pojo.entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 管理端角色解析与角色管理：登录准入、权限点解析、角色 CRUD。
 * ADMIN 平台超管固定全权（代码写死），不读角色表，防止误改权限锁死平台。
 */
@Service
public class AdminRoleService {

    private static final Logger log = LoggerFactory.getLogger(AdminRoleService.class);

    /** 平台超管角色编码，与 user.role 取值一致。 */
    public static final String SUPER_ROLE = "ADMIN";

    /** 租户管理员角色编码。 */
    public static final String TENANT_ADMIN_ROLE = "TENANT_ADMIN";

    /** 自建角色不可占用的内置编码。 */
    private static final Set<String> RESERVED_CODES =
            Set.of("ADMIN", "TENANT_ADMIN", "OPERATOR", "FINANCE", "VIEWER");

    private final RoleMapper roleMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AdminRoleService(RoleMapper roleMapper, UserMapper userMapper) {
        this.roleMapper = roleMapper;
        this.userMapper = userMapper;
    }

    /** 当前启用中的全部管理角色编码（含固定的平台超管），供账号/租户模块做角色校验与统计。 */
    public List<String> adminRoleCodes() {
        List<String> codes = new ArrayList<>();
        codes.add(SUPER_ROLE);
        roleMapper.selectList(new LambdaQueryWrapper<Role>().eq(Role::getStatus, 1))
                .forEach(r -> codes.add(r.getRoleCode()));
        return List.copyOf(codes);
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

    // ===== 角色管理（管理页 CRUD） =====

    /** 全部角色（含停用），权限点解析为数组。 */
    public List<AdminRoleItem> listAll() {
        return roleMapper.selectList(new LambdaQueryWrapper<Role>().orderByAsc(Role::getId))
                .stream().map(this::toItem).toList();
    }

    /** 创建自定义角色。保留编码与内置编码不可用。 */
    public Long createRole(String roleCode, String roleName, List<String> permissions, String remark) {
        if (!StringUtils.hasText(roleCode) || !roleCode.trim().matches("[A-Z][A-Z0-9_]{1,15}")) {
            throw new BizException(40082, "角色编码须为 2-16 位大写字母/数字/下划线，字母开头");
        }
        String code = roleCode.trim();
        if (RESERVED_CODES.contains(code)) {
            throw new BizException(40083, "该角色编码为系统保留");
        }
        if (!StringUtils.hasText(roleName) || roleName.trim().length() > 32) {
            throw new BizException(40084, "角色名称须为 1-32 个字符");
        }
        String permissionsJson = validatePermissions(permissions);
        Long exists = roleMapper.selectCount(new LambdaQueryWrapper<Role>().eq(Role::getRoleCode, code));
        if (exists != null && exists > 0) {
            throw new BizException(40085, "角色编码已存在");
        }
        Role role = new Role();
        role.setRoleCode(code);
        role.setRoleName(roleName.trim());
        role.setPermissionsJson(permissionsJson);
        role.setStatus(1);
        role.setBuiltIn(0);
        role.setRemark(remark);
        roleMapper.insert(role);
        return role.getId();
    }

    /**
     * 编辑角色。ADMIN 平台超管不可改（权限代码固定全权）；其余内置角色可改名称/权限/启停，不可删除。
     */
    public void updateRole(Long id, String roleName, List<String> permissions, Integer status, String remark) {
        Role role = requireRole(id);
        if (SUPER_ROLE.equals(role.getRoleCode()) && (permissions != null || status != null)) {
            throw new BizException(40086, "平台超管权限固定全权，不可修改");
        }
        if (roleName != null) {
            if (!StringUtils.hasText(roleName) || roleName.trim().length() > 32) {
                throw new BizException(40084, "角色名称须为 1-32 个字符");
            }
            role.setRoleName(roleName.trim());
        }
        if (permissions != null) {
            role.setPermissionsJson(validatePermissions(permissions));
        }
        if (status != null) {
            if (status != 0 && status != 1) {
                throw new BizException(40087, "status 仅允许 0/1");
            }
            role.setStatus(status);
        }
        if (remark != null) {
            role.setRemark(remark);
        }
        roleMapper.updateById(role);
    }

    /** 删除自定义角色：内置不可删；仍有账号挂在该角色时拒绝。 */
    public void deleteRole(Long id) {
        Role role = requireRole(id);
        if (Integer.valueOf(1).equals(role.getBuiltIn())) {
            throw new BizException(40088, "内置角色不可删除，可停用");
        }
        Long inUse = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getRole, role.getRoleCode()));
        if (inUse != null && inUse > 0) {
            throw new BizException(40089, "仍有 " + inUse + " 个账号使用该角色，请先改派");
        }
        roleMapper.deleteById(id);
    }

    private Role requireRole(Long id) {
        Role role = id == null ? null : roleMapper.selectById(id);
        if (role == null) {
            throw new BizException(40403, "角色不存在");
        }
        return role;
    }

    /** 权限点格式校验：* 或 模块:动作；数量上限 20。返回 JSON 字符串。 */
    private String validatePermissions(List<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            throw new BizException(40090, "至少选择一个权限点");
        }
        if (permissions.size() > 20) {
            throw new BizException(40091, "权限点数量过多");
        }
        for (String p : permissions) {
            if (!StringUtils.hasText(p)) {
                throw new BizException(40092, "权限点不能为空");
            }
            String trimmed = p.trim();
            if (!"*".equals(trimmed) && !trimmed.matches("[a-z][a-z0-9]*:(manage|read)")) {
                throw new BizException(40093, "权限点格式不正确: " + trimmed);
            }
        }
        try {
            return objectMapper.writeValueAsString(permissions.stream().map(String::trim).distinct().toList());
        } catch (JsonProcessingException e) {
            throw new BizException(40094, "权限点序列化失败");
        }
    }

    private AdminRoleItem toItem(Role role) {
        List<String> permissions;
        try {
            permissions = StringUtils.hasText(role.getPermissionsJson())
                    ? objectMapper.readValue(role.getPermissionsJson(),
                            new TypeReference<List<String>>() { })
                    : List.of();
        } catch (JsonProcessingException e) {
            permissions = List.of();
        }
        return new AdminRoleItem(role.getId(), role.getRoleCode(), role.getRoleName(), permissions,
                role.getStatus(), role.getBuiltIn(), role.getRemark(),
                role.getCreateTime() == null ? "" : role.getCreateTime().toString());
    }
}
