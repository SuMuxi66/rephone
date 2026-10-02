package com.rephone.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rephone.common.exception.BizException;
import com.rephone.mapper.TenantMapper;
import com.rephone.mapper.UserMapper;
import com.rephone.pojo.dto.AdminUserItem;
import com.rephone.pojo.entity.Tenant;
import com.rephone.pojo.entity.User;
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 管理端账号管理。普通用户仅允许启用/禁用；管理员账号允许创建（可指定角色/租户）、
 * 改角色与重置密码。管理端查询无租户上下文（可见全租户），与现有 admin 接口一致。
 */
@Service
public class AdminUserService {

    private static final String ROLE_ADMIN = "ADMIN";

    private final UserMapper userMapper;
    private final TenantMapper tenantMapper;
    private final AdminRoleService adminRoleService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AdminUserService(UserMapper userMapper, TenantMapper tenantMapper,
                            AdminRoleService adminRoleService) {
        this.userMapper = userMapper;
        this.tenantMapper = tenantMapper;
        this.adminRoleService = adminRoleService;
    }

    public Page<AdminUserItem> page(String keyword, String role, long pageNum, long pageSize) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>().orderByDesc(User::getId);
        if (StringUtils.hasText(keyword)) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like(User::getNickname, kw)
                    .or().like(User::getUsername, kw)
                    .or().like(User::getPhone, kw));
        }
        if (StringUtils.hasText(role)) {
            wrapper.eq(User::getRole, role);
        }
        Page<User> page = userMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        Page<AdminUserItem> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::toItem).toList());
        return result;
    }

    /** 启用/禁用：仅允许操作普通用户账号；管理员账号不提供禁用入口（防自锁）。 */
    public void setStatus(Long id, Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BizException(40054, "status 仅允许 0/1");
        }
        User user = requireUser(id);
        if (ROLE_ADMIN.equals(user.getRole())) {
            throw new BizException(40055, "管理员账号不允许禁用，如需回收权限请重置密码后交接");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    /** 创建管理员账号（默认平台超管，保持旧调用兼容）。 */
    public Long createAdmin(String username, String password, String nickname) {
        return createAdmin(username, password, nickname, null, null);
    }

    /**
     * 创建管理员账号并指定角色与所属租户。
     * 平台超管只能属于租户 0；租户管理员必须指定已启用的租户。
     */
    public Long createAdmin(String username, String password, String nickname, String roleCode, Long tenantId) {
        validateUsername(username);
        validatePassword(password);
        if (!StringUtils.hasText(nickname)) {
            throw new BizException(40056, "昵称不能为空");
        }
        String role = StringUtils.hasText(roleCode) ? roleCode.trim() : ROLE_ADMIN;
        if (!adminRoleService.isAdminRole(role)) {
            throw new BizException(40074, "角色不存在或已停用");
        }
        Long tid = tenantId == null ? 0L : tenantId;
        if (AdminRoleService.SUPER_ROLE.equals(role)) {
            if (tid != 0) {
                throw new BizException(40075, "平台超管只能属于平台租户 0");
            }
        } else if (AdminRoleService.TENANT_ADMIN_ROLE.equals(role)) {
            requireEnabledTenant(tid);
        }
        Long exists = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        if (exists != null && exists > 0) {
            throw new BizException(40057, "用户名已存在");
        }
        User user = new User();
        user.setTenantId(tid);
        user.setOpenid("admin-" + username);
        user.setUsername(username);
        user.setNickname(nickname.trim());
        user.setRole(role);
        user.setStatus(1);
        user.setGender(0);
        user.setAvatarUrl("");
        user.setPasswordHash(encoder.encode(password));
        userMapper.insert(user);
        return user.getId();
    }

    /** 修改管理员账号角色。不允许自行变更自己的角色（防误操作失去管理权）。 */
    public void changeRole(Long callerId, Long targetId, String roleCode) {
        if (!StringUtils.hasText(roleCode) || !adminRoleService.isAdminRole(roleCode.trim())) {
            throw new BizException(40074, "角色不存在或已停用");
        }
        String role = roleCode.trim();
        User target = requireUser(targetId);
        if (!StringUtils.hasText(target.getUsername())) {
            throw new BizException(40076, "普通用户无管理角色可改");
        }
        if (callerId != null && callerId.equals(targetId)) {
            throw new BizException(40077, "不允许修改自己的角色");
        }
        if (AdminRoleService.SUPER_ROLE.equals(role) && target.getTenantId() != null && target.getTenantId() != 0) {
            throw new BizException(40078, "租户账号不能提升为平台超管");
        }
        if (AdminRoleService.TENANT_ADMIN_ROLE.equals(role)
                && (target.getTenantId() == null || target.getTenantId() == 0)) {
            throw new BizException(40079, "平台账号不能设置为租户管理员，请先指定租户");
        }
        target.setRole(role);
        userMapper.updateById(target);
    }

    private void requireEnabledTenant(Long tenantId) {
        if (tenantId == null || tenantId <= 0) {
            throw new BizException(40080, "租户管理员必须指定租户");
        }
        Tenant tenant = tenantMapper.selectById(tenantId);
        if (tenant == null || !Integer.valueOf(1).equals(tenant.getStatus())) {
            throw new BizException(40081, "租户不存在或已停用");
        }
    }

    /** 重置管理员账号密码。 */
    public void resetPassword(Long id, String password) {
        validatePassword(password);
        User user = requireUser(id);
        if (!ROLE_ADMIN.equals(user.getRole())) {
            throw new BizException(40058, "仅管理员账号可重置密码");
        }
        user.setPasswordHash(encoder.encode(password));
        userMapper.updateById(user);
    }

    private User requireUser(Long id) {
        User user = id == null ? null : userMapper.selectById(id);
        if (user == null) {
            throw new BizException(40401, "用户不存在");
        }
        return user;
    }

    private void validateUsername(String username) {
        if (!StringUtils.hasText(username) || !username.matches("[a-zA-Z0-9_]{4,32}")) {
            throw new BizException(40059, "用户名须为 4-32 位字母/数字/下划线");
        }
    }

    private void validatePassword(String password) {
        if (!StringUtils.hasText(password) || password.length() < 8 || password.length() > 64) {
            throw new BizException(40060, "密码长度须为 8-64 位");
        }
    }

    private AdminUserItem toItem(User u) {
        String openid = u.getOpenid() == null ? "" : u.getOpenid();
        String masked = openid.length() <= 6 ? "***" : "***" + openid.substring(openid.length() - 6);
        return new AdminUserItem(u.getId(), u.getUsername(), u.getNickname(), u.getPhone(),
                u.getRole(), u.getStatus(), masked,
                u.getCreateTime() == null ? "" : u.getCreateTime().toString());
    }
}
