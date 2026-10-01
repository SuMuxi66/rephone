package com.rephone.controller.support;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** 管理端登录限流（纯单元测试）。 */
class LoginAttemptGuardTest {

    @Test
    void locksAfterMaxFailures() {
        LoginAttemptGuard guard = new LoginAttemptGuard();
        for (int i = 0; i < 5; i++) {
            assertFalse(guard.isLocked("admin"), "第 " + i + " 次失败前不应锁定");
            guard.recordFailure("admin");
        }
        assertTrue(guard.isLocked("admin"), "连续 5 次失败后应锁定");
        assertTrue(guard.retryAfterSeconds("admin") > 0, "锁定期间应给出等待秒数");
    }

    @Test
    void successResetsCounter() {
        LoginAttemptGuard guard = new LoginAttemptGuard();
        for (int i = 0; i < 4; i++) {
            guard.recordFailure("admin");
        }
        guard.reset("admin");
        // 清零后再失败 4 次仍不应锁定
        for (int i = 0; i < 4; i++) {
            guard.recordFailure("admin");
            assertFalse(guard.isLocked("admin"));
        }
    }

    @Test
    void differentAccountsAreIsolated() {
        LoginAttemptGuard guard = new LoginAttemptGuard();
        for (int i = 0; i < 5; i++) {
            guard.recordFailure("admin");
        }
        assertTrue(guard.isLocked("admin"));
        assertFalse(guard.isLocked("someone-else"), "一个账号被锁不应影响其它账号");
        assertFalse(guard.isLocked("never-seen"));
    }
}
