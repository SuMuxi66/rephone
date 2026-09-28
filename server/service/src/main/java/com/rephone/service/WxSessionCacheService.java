package com.rephone.service;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 微信 session_key 的 Redis 缓存。
 * Redis 不可用时降级为跳过缓存（不影响登录主流程），方便本地无 Redis 开发。
 */
@Service
public class WxSessionCacheService {

    private static final Logger log = LoggerFactory.getLogger(WxSessionCacheService.class);

    private static final String KEY_PREFIX = "rephone:wx:session:";
    private static final Duration TTL = Duration.ofHours(72);

    private final StringRedisTemplate redis;

    public WxSessionCacheService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void save(String openid, String sessionKey) {
        if (!StringUtils.hasText(sessionKey)) {
            return;
        }
        try {
            redis.opsForValue().set(KEY_PREFIX + openid, sessionKey, TTL);
        } catch (DataAccessException e) {
            log.warn("[redis] session 缓存写入失败（不影响登录）: {}", e.getMessage());
        }
    }

    public String get(String openid) {
        try {
            return redis.opsForValue().get(KEY_PREFIX + openid);
        } catch (DataAccessException e) {
            log.warn("[redis] session 缓存读取失败（返回空）: {}", e.getMessage());
            return null;
        }
    }
}
