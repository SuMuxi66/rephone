package com.rephone.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

/**
 * 不安全配置守卫（MockGuardConfig）行为验证。
 *
 * <p>纯单元测试：用 MockEnvironment 精确驱动守卫的各个判定分支，不起 Spring 上下文。
 * 这里要钉死的是发布安全底线 —— 生产 profile 下只要存在任何 mock 开关或弱管理令牌，
 * 应用必须【拒绝启动】，而不是打一行日志照常跑起来。
 *
 * <p>为什么这条底线重要：mock 登录下任意请求都能换取有效会话（整条 /api/wx/* 认证被绕过）；
 * mock 支付会伪造支付成功；弱管理令牌可读写全部订单与用户 PII。
 */
class MockGuardConfigTest {

    private static MockEnvironment env(String... profiles) {
        MockEnvironment e = new MockEnvironment();
        e.setActiveProfiles(profiles);
        return e;
    }

    private static void assertGuardRejects(MockEnvironment e, String expectFragment) {
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> new MockGuardConfig(e).afterPropertiesSet());
        assertTrue(ex.getMessage().contains(expectFragment),
                "异常信息应点名违规项，实际: " + ex.getMessage());
    }

    /** 四个 mock 开关在 prod 下逐个都必须拒绝启动（缺一个就是一条绕过路径）。 */
    @Test
    void prodRejectsEveryMockSwitch() {
        String[][] cases = {
            {"rephone.wx.mock-login", "mock-login"},
            {"rephone.wxpay.mock", "wxpay.mock"},
            {"rephone.cos.mock", "cos.mock"},
            {"rephone.express.mock", "express.mock"},
        };
        for (String[] c : cases) {
            MockEnvironment e = env("prod");
            e.setProperty(c[0], "true");
            assertGuardRejects(e, c[1]);
        }
    }

    @Test
    void prodRejectsWeakAdminToken() {
        MockEnvironment e = env("prod");
        e.setProperty("rephone.admin.token", "local-admin-token");
        assertGuardRejects(e, "管理端令牌");
    }

    @Test
    void prodAllowsSafeConfiguration() {
        MockEnvironment e = env("prod");
        e.setProperty("rephone.wx.mock-login", "false");
        e.setProperty("rephone.wxpay.mock", "false");
        e.setProperty("rephone.cos.mock", "false");
        e.setProperty("rephone.express.mock", "false");
        e.setProperty("rephone.admin.token", "0123456789abcdef0123456789abcdef0123456789abcdef");
        assertDoesNotThrow(() -> new MockGuardConfig(e).afterPropertiesSet());
    }

    /** ADMIN_TOKEN 留空 = 关闭机器令牌（管理员改用账号密码登录），不是弱口令。 */
    @Test
    void blankAdminTokenIsNotTreatedAsWeak() {
        MockEnvironment e = env("prod");
        e.setProperty("rephone.admin.token", "");
        assertDoesNotThrow(() -> new MockGuardConfig(e).afterPropertiesSet());
    }

    /** 开发/预发允许 mock，但绝不能「静默」：不抛异常（守卫会打 WARN 横幅）。 */
    @Test
    void nonProdOnlyWarnsAndStillStarts() {
        MockEnvironment e = env("dev");
        e.setProperty("rephone.wx.mock-login", "true");
        e.setProperty("rephone.wxpay.mock", "true");
        assertDoesNotThrow(() -> new MockGuardConfig(e).afterPropertiesSet());
    }

    /** 未显式设置 profile 时 Spring 默认 profile 为 "default"，不属于 prod。 */
    @Test
    void noProfileIsNotTreatedAsProd() {
        MockEnvironment e = env();
        e.setProperty("rephone.express.mock", "true");
        assertDoesNotThrow(() -> new MockGuardConfig(e).afterPropertiesSet());
    }
}
