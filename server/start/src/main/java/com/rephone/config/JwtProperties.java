package com.rephone.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** JWT 配置。secret 只能来自环境变量 JWT_SECRET。 */
@ConfigurationProperties(prefix = "rephone.jwt")
public class JwtProperties {

    private String secret;

    /** token 有效期（秒），默认 7 天。 */
    private long ttlSeconds = 604800;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getTtlSeconds() {
        return ttlSeconds;
    }

    public void setTtlSeconds(long ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
    }
}
