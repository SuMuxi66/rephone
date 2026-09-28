package com.rephone.common.context;

/**
 * 已登录用户上下文（ThreadLocal），由 JwtAuthFilter 写入、请求结束后清理。
 */
public record UserContext(long userId, String openid, long tenantId) {

    public static UserContext anonymous() {
        return new UserContext(0L, "", 0L);
    }
}
