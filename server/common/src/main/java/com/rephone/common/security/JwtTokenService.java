package com.rephone.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;

/**
 * HS256 JWT 签发与校验。
 * 密钥只能来自环境变量/密钥服务（见 start 模块 SecurityBeanConfig），
 * 长度不足 32 字节直接拒绝构造。
 */
public class JwtTokenService {

    private final SecretKey key;
    private final long ttlSeconds;

    public JwtTokenService(String secret, long ttlSeconds) {
        byte[] bytes = secret == null ? new byte[0] : secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT 密钥长度不足（至少 32 字节），请通过环境变量 JWT_SECRET 提供");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.ttlSeconds = ttlSeconds;
    }

    public String create(long userId, String openid, long tenantId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("openid", openid)
                .claim("tid", tenantId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(ttlSeconds)))
                .signWith(key)
                .compact();
    }

    /** 管理端账号登录签发：subject=用户ID，带 role claim（AdminTokenFilter 校验 role=admin）。 */
    public String createAdminToken(long userId, long tenantId, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("tid", tenantId)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(ttlSeconds)))
                .signWith(key)
                .compact();
    }

    /** 校验并解析 token；签名不符/过期抛 JwtException。 */
    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
