package com.rephone.controller.support;

import java.util.List;

/**
 * 管理端权限点与路径映射（静态表）。
 * 新增 /api/admin/** 接口时必须同步登记，否则落入默认规则=仅全权角色可访问。
 * 权限点字符串同时是角色表 permissions_json 的取值。
 */
public final class AdminPermissions {

    /** 全权通配，仅 ADMIN 平台超管持有（代码固定，不落角色表）。 */
    public static final String ALL = "*";

    public static final String RECYCLE_MANAGE = "recycle:manage";
    public static final String REPAIR_MANAGE = "repair:manage";
    public static final String SALE_MANAGE = "sale:manage";
    public static final String GOODS_MANAGE = "goods:manage";
    public static final String ADDRESS_MANAGE = "address:manage";
    public static final String ACCOUNT_MANAGE = "account:manage";
    public static final String TENANT_MANAGE = "tenant:manage";
    public static final String ROLE_MANAGE = "role:manage";
    public static final String FINANCE_READ = "finance:read";
    public static final String DASHBOARD_READ = "dashboard:read";

    private record Rule(boolean exact, String prefix, String permission) {
    }

    private static final List<Rule> RULES = List.of(
            new Rule(true, "/api/admin/recycle", RECYCLE_MANAGE),
            new Rule(true, "/api/admin/repair", REPAIR_MANAGE),
            new Rule(true, "/api/admin/sale", SALE_MANAGE),
            new Rule(true, "/api/admin/after-sales", SALE_MANAGE),
            new Rule(true, "/api/admin/goods", GOODS_MANAGE),
            new Rule(true, "/api/admin/upload", GOODS_MANAGE),
            new Rule(true, "/api/admin/cos", GOODS_MANAGE),
            new Rule(true, "/api/admin/brands", GOODS_MANAGE),
            new Rule(true, "/api/admin/brand", GOODS_MANAGE),
            new Rule(true, "/api/admin/models", GOODS_MANAGE),
            new Rule(true, "/api/admin/model", GOODS_MANAGE),
            new Rule(true, "/api/admin/addresses", ADDRESS_MANAGE),
            new Rule(true, "/api/admin/users", ACCOUNT_MANAGE),
            new Rule(true, "/api/admin/user", ACCOUNT_MANAGE),
            new Rule(true, "/api/admin/account", ACCOUNT_MANAGE),
            new Rule(true, "/api/admin/tenants", TENANT_MANAGE),
            new Rule(true, "/api/admin/tenant", TENANT_MANAGE),
            new Rule(true, "/api/admin/roles", ROLE_MANAGE),
            new Rule(true, "/api/admin/role", ROLE_MANAGE),
            new Rule(true, "/api/admin/finance", FINANCE_READ),
            new Rule(true, "/api/admin/dashboard", DASHBOARD_READ));

    private AdminPermissions() {
    }

    /**
     * 返回该 URI 所需权限点。
     * null=仅需管理身份（/api/admin/auth/me）；未登记路径返回 {@link #ALL}=仅全权角色可访问。
     */
    public static String required(String uri) {
        if (uri == null) {
            return ALL;
        }
        if (uri.equals("/api/admin/auth/me")) {
            return null;
        }
        for (Rule rule : RULES) {
            boolean hit = rule.exact() ? uri.equals(rule.prefix()) || uri.startsWith(rule.prefix() + "/")
                    : uri.startsWith(rule.prefix());
            if (hit) {
                return rule.permission();
            }
        }
        return ALL;
    }

    /** 全部权限点（角色管理页矩阵展示顺序）。 */
    public static List<String> all() {
        return List.of(RECYCLE_MANAGE, REPAIR_MANAGE, SALE_MANAGE, GOODS_MANAGE, ADDRESS_MANAGE,
                ACCOUNT_MANAGE, TENANT_MANAGE, ROLE_MANAGE, FINANCE_READ, DASHBOARD_READ);
    }
}
