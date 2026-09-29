package com.rephone.controller.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rephone.common.security.JwtTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 管理端鉴权：双重凭证，任一通过即可——
 * 1) 机器凭证 Authorization: Bearer &lt;ADMIN_TOKEN&gt;（常量时间比较）；
 * 2) 账号凭证：管理员账号登录签发的 JWT（role=admin claim）。
 * /api/admin/auth/login 为匿名登录入口（shouldNotFilter 放行）；其余 /api/admin/* 必须携带凭证。
 * 通过后写入 request attribute：{@link #ATTR_AUTH_TYPE}（token/account）与 {@link #ATTR_USER_ID}，
 * 供 /api/admin/auth/me 等接口识别凭证类型。租户上下文保持为空（管理端可见全租户）。
 */
public class AdminTokenFilter extends OncePerRequestFilter {

    public static final String ATTR_AUTH_TYPE = "admin.authType";
    public static final String ATTR_USER_ID = "admin.userId";

    private final String adminToken;
    private final JwtTokenService tokenService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AdminTokenFilter(String adminToken, JwtTokenService tokenService) {
        this.adminToken = adminToken;
        this.tokenService = tokenService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return "/api/admin/auth/login".equals(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String credential = header.substring("Bearer ".length());
            // 1) 机器凭证：ADMIN_TOKEN
            if (StringUtils.hasText(adminToken) && constantTimeEquals(credential, adminToken)) {
                request.setAttribute(ATTR_AUTH_TYPE, "token");
                chain.doFilter(request, response);
                return;
            }
            // 2) 账号凭证：管理员 JWT
            try {
                Claims claims = tokenService.parse(credential);
                if ("admin".equals(claims.get("role", String.class))) {
                    request.setAttribute(ATTR_AUTH_TYPE, "account");
                    request.setAttribute(ATTR_USER_ID, Long.parseLong(claims.getSubject()));
                    chain.doFilter(request, response);
                    return;
                }
            } catch (JwtException | IllegalArgumentException ignored) {
                // 落到 401
            }
        }
        writeError(response, 40100, "管理端鉴权失败");
    }

    /** 常量时间比较，防时序侧信道逐字节爆破。 */
    private static boolean constantTimeEquals(String actual, String expected) {
        return MessageDigest.isEqual(actual.getBytes(StandardCharsets.UTF_8),
                expected.getBytes(StandardCharsets.UTF_8));
    }

    private void writeError(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), Map.of("code", code, "message", message, "data", ""));
    }
}
