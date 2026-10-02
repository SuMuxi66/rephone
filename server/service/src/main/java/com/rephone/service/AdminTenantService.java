package com.rephone.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rephone.common.exception.BizException;
import com.rephone.mapper.TenantMapper;
import com.rephone.mapper.UserMapper;
import com.rephone.pojo.dto.AdminTenantItem;
import com.rephone.pojo.entity.Tenant;
import com.rephone.pojo.entity.User;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 管理端租户管理。租户 0 为平台自营：不可停用。
 * 创建租户可选附带一个租户管理员账号（TENANT_ADMIN，数据自动限定本租户）。
 */
@Service
public class AdminTenantService {

    public static final long PLATFORM_TENANT_ID = 0L;

    private final TenantMapper tenantMapper;
    private final UserMapper userMapper;
    private final AdminUserService adminUserService;
    private final AdminRoleService adminRoleService;

    public AdminTenantService(TenantMapper tenantMapper, UserMapper userMapper,
                              AdminUserService adminUserService, AdminRoleService adminRoleService) {
        this.tenantMapper = tenantMapper;
        this.userMapper = userMapper;
        this.adminUserService = adminUserService;
        this.adminRoleService = adminRoleService;
    }

    public Page<AdminTenantItem> page(String keyword, long pageNum, long pageSize) {
        LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<Tenant>().orderByAsc(Tenant::getId);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Tenant::getName, keyword.trim());
        }
        Page<Tenant> page = tenantMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        Page<AdminTenantItem> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(page.getRecords().stream().map(this::toItem).toList());
        return result;
    }

    /** 创建租户；adminUsername 非空时同步创建该租户的管理员账号（TENANT_ADMIN）。 */
    public Long create(String name, String adminUsername, String adminPassword, String adminNickname) {
        if (!StringUtils.hasText(name) || name.trim().length() > 64) {
            throw new BizException(40070, "租户名称须为 1-64 个字符");
        }
        String trimmed = name.trim();
        Long exists = tenantMapper.selectCount(new LambdaQueryWrapper<Tenant>().eq(Tenant::getName, trimmed));
        if (exists != null && exists > 0) {
            throw new BizException(40071, "租户名称已存在");
        }
        Tenant tenant = new Tenant();
        tenant.setName(trimmed);
        tenant.setStatus(1);
        tenantMapper.insert(tenant);
        if (StringUtils.hasText(adminUsername)) {
            adminUserService.createAdmin(adminUsername, adminPassword, adminNickname,
                    AdminRoleService.TENANT_ADMIN_ROLE, tenant.getId());
        }
        return tenant.getId();
    }

    /** 改名/启停。平台自营租户 0 为隐式租户：不可停用（允许存在行时改名）。 */
    public void update(Long id, String name, Integer status) {
        if (status != null && status == 0 && PLATFORM_TENANT_ID == id) {
            throw new BizException(40073, "平台自营租户不可停用");
        }
        Tenant tenant = tenantMapper.selectById(id);
        if (tenant == null) {
            throw new BizException(40402, "租户不存在");
        }
        if (name != null) {
            if (!StringUtils.hasText(name) || name.trim().length() > 64) {
                throw new BizException(40070, "租户名称须为 1-64 个字符");
            }
            tenant.setName(name.trim());
        }
        if (status != null) {
            if (status != 0 && status != 1) {
                throw new BizException(40072, "status 仅允许 0/1");
            }
            if (status == 0 && PLATFORM_TENANT_ID == id) {
                throw new BizException(40073, "平台自营租户不可停用");
            }
            tenant.setStatus(status);
        }
        tenantMapper.updateById(tenant);
    }

    private AdminTenantItem toItem(Tenant t) {
        List<String> adminRoles = adminRoleService.adminRoleCodes();
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getTenantId, t.getId())
                .in(User::getRole, adminRoles));
        return new AdminTenantItem(t.getId(), t.getName(), t.getStatus(), count,
                t.getCreateTime() == null ? "" : t.getCreateTime().toString());
    }
}
