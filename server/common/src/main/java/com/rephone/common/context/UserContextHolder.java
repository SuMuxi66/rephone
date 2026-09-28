package com.rephone.common.context;

import com.rephone.common.exception.BizException;

/** 已登录用户上下文持有器。 */
public final class UserContextHolder {

    private static final ThreadLocal<UserContext> CONTEXT = new ThreadLocal<>();

    private UserContextHolder() {
    }

    public static void set(UserContext context) {
        CONTEXT.set(context);
    }

    public static UserContext get() {
        return CONTEXT.get();
    }

    /** 要求已登录，否则抛 40100 业务异常。 */
    public static UserContext require() {
        UserContext context = CONTEXT.get();
        if (context == null) {
            throw new BizException(40100, "未登录或登录已过期");
        }
        return context;
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
