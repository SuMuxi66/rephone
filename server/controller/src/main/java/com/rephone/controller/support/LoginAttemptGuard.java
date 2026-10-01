package com.rephone.controller.support;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * 登录失败限流：同一账号在滑动窗口内失败 {@value #MAX_FAILURES} 次即锁定，成功登录清零。
 *
 * <p>黑盒测试发现管理端登录对连续错误密码完全不设防（8 次尝试 410ms 全部放行），
 * 可在线暴力破解。
 *
 * <p>实现是**进程内内存**计数：单实例足够；多实例部署时每个实例各算一份，
 * 阈值实际被放大成 实例数 × MAX_FAILURES。若要严格，应换成 Redis 计数。
 */
@Component
public class LoginAttemptGuard {

    /** 窗口内允许的失败次数，超过即锁定。 */
    private static final int MAX_FAILURES = 5;

    /** 滑动窗口长度。 */
    private static final Duration WINDOW = Duration.ofMinutes(15);

    /** 最多跟踪的账号数：防止攻击者用海量用户名把内存打爆。 */
    private static final int MAX_TRACKED = 10_000;

    private final Map<String, Deque<Instant>> failures = new ConcurrentHashMap<>();

    /** 该账号当前是否处于锁定状态。 */
    public boolean isLocked(String key) {
        Deque<Instant> queue = failures.get(key);
        if (queue == null) {
            return false;
        }
        synchronized (queue) {
            prune(queue);
            return queue.size() >= MAX_FAILURES;
        }
    }

    /** 记录一次失败。 */
    public void recordFailure(String key) {
        if (failures.size() >= MAX_TRACKED && !failures.containsKey(key) && !evictStale()) {
            return;
        }
        Deque<Instant> queue = failures.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (queue) {
            prune(queue);
            queue.addLast(Instant.now());
        }
    }

    /** 登录成功后清零，避免正常用户被历史失败拖累。 */
    public void reset(String key) {
        failures.remove(key);
    }

    /** 还需等待多少秒才能重试；未锁定返回 0。 */
    public long retryAfterSeconds(String key) {
        Deque<Instant> queue = failures.get(key);
        if (queue == null) {
            return 0;
        }
        synchronized (queue) {
            prune(queue);
            if (queue.size() < MAX_FAILURES) {
                return 0;
            }
            Instant oldest = queue.peekFirst();
            if (oldest == null) {
                return 0;
            }
            return Math.max(Duration.between(Instant.now(), oldest.plus(WINDOW)).getSeconds(), 1);
        }
    }

    private static void prune(Deque<Instant> queue) {
        Instant cutoff = Instant.now().minus(WINDOW);
        while (!queue.isEmpty() && !queue.peekFirst().isAfter(cutoff)) {
            queue.pollFirst();
        }
    }

    /** 容量吃紧时先回收已过期的条目；回收后仍满返回 false。 */
    private boolean evictStale() {
        failures.entrySet().removeIf(e -> {
            synchronized (e.getValue()) {
                prune(e.getValue());
                return e.getValue().isEmpty();
            }
        });
        return failures.size() < MAX_TRACKED;
    }
}
