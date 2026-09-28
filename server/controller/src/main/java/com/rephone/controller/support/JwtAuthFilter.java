package com.rephone.controller.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rephone.common.context.TenantContextHolder;
import com.rephone.common.context.UserContext;
import com.rephone.common.context.UserContextHolder;
import com.rephone.common.security.JwtTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT 鉴权过滤器。挂载在 /api/wx/*（注册见 start 模块）：
 * - /api/wx/login 放行（匿名登录入口）
 * - 其余接口必须携带 Authorization: Bearer &lt;token&gt;
 * 校验通过后写入 TenantContextHolder / UserContextHolder，请求结束清理。
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    public static final String LOGIN_PATH = "/api/wx/login";

    private final JwtTokenService tokenService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthFilter(JwtTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        if (LOGIN_PATH.equals(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            writeUnauthorized(response);
            return;
        }
        try {
            Claims claims = tokenService.parse(header.substring("Bearer ".length()));
            long userId = Long.parseLong(claims.getSubject());
            Number tid = claims.get("tid", Number.class);
            long tenantId = tid == null ? 0L : tid.longValue();
            TenantContextHolder.set(tenantId);
            UserContextHolder.set(new UserContext(userId, claims.get("openid", String.class), tenantId));
            chain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException e) {
            writeUnauthorized(response);
        } finally {
            TenantContextHolder.clear();
            UserContextHolder.clear();
        }
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(),
                java.util.Map.of("code", 40100, "message", "未登录或登录已过期", "data", ""));
    }
}
