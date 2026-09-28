package com.rephone.common.context;

/**
 * 租户上下文（ThreadLocal）。由 JwtAuthFilter 在解析 token 后写入，
 * 供 MyBatis-Plus TenantLineInnerInterceptor 拼接 tenant_id 条件使用。
 * 为 null 时（如登录前的匿名链路、平台级操作）拦截器不追加租户条件。
 */
public final class TenantContextHolder {

    private static final ThreadLocal<Long> CONTEXT = new ThreadLocal<>();

    private TenantContextHolder() {
    }

    public static void set(Long tenantId) {
        CONTEXT.set(tenantId);
    }

    /** 当前租户 ID，匿名链路返回 null。 */
    public static Long get() {
        return CONTEXT.get();
    }

    /** 当前租户 ID，匿名链路返回默认租户 0（平台/自营）。 */
    public static long getOrDefault() {
        Long tid = CONTEXT.get();
        return tid == null ? 0L : tid;
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
