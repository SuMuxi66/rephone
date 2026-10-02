package com.rephone.controller.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rephone.common.security.JwtTokenService;
import com.rephone.mapper.UserMapper;
import com.rephone.pojo.entity.User;
import com.rephone.service.AdminRoleService;
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
 * 1) 机器凭证 Authorization: Bearer &lt;ADMIN_TOKEN&gt;（常量时间比较）：break-glass，全权不查库；
 * 2) 账号凭证：管理端登录签发的 JWT（role=admin claim）。
 * 账号凭证通过后做授权：查库校验账号存在、未禁用、角色仍为启用管理角色（禁用即 token 立即失效），
 * 再按 {@link AdminPermissions} 静态映射校验权限点（无权限 40300）。
 * /api/admin/auth/login 为匿名入口（shouldNotFilter 放行）。
 * 租户上下文：平台超管（tid=0）保持为空=全租户；租户管理员由租户管理提交引入（tid&gt;0 写上下文本租户隔离）。
 */
public class AdminTokenFilter extends OncePerRequestFilter {

    public static final String ATTR_AUTH_TYPE = "admin.authType";
    public static final String ATTR_USER_ID = "admin.userId";
    public static final String ATTR_ROLE_CODE = "admin.roleCode";

    private final String adminToken;
    private final JwtTokenService tokenService;
    private final UserMapper userMapper;
    private final AdminRoleService adminRoleService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AdminTokenFilter(String adminToken, JwtTokenService tokenService,
                            UserMapper userMapper, AdminRoleService adminRoleService) {
        this.adminToken = adminToken;
        this.tokenService = tokenService;
        this.userMapper = userMapper;
        this.adminRoleService = adminRoleService;
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
            // 1) 机器凭证：ADMIN_TOKEN（运维后门，全权）
            if (StringUtils.hasText(adminToken) && constantTimeEquals(credential, adminToken)) {
                request.setAttribute(ATTR_AUTH_TYPE, "token");
                chain.doFilter(request, response);
                return;
            }
            // 2) 账号凭证：管理员 JWT
            try {
                Claims claims = tokenService.parse(credential);
                if ("admin".equals(claims.get("role", String.class))) {
                    long userId = Long.parseLong(claims.getSubject());
                    User user = userMapper.selectById(userId);
                    if (user == null || !Integer.valueOf(1).equals(user.getStatus())
                            || !adminRoleService.isAdminRole(user.getRole())) {
                        writeError(response, 40101, "管理员账号不存在或已停用，请重新登录");
                        return;
                    }
                    String required = AdminPermissions.required(request.getRequestURI());
                    if (!adminRoleService.hasPermission(user.getRole(), required)) {
                        writeError(response, 40300, "无权限访问该功能");
                        return;
                    }
                    request.setAttribute(ATTR_AUTH_TYPE, "account");
                    request.setAttribute(ATTR_USER_ID, userId);
                    request.setAttribute(ATTR_ROLE_CODE, user.getRole());
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
        response.setStatus(code == 40300 ? HttpServletResponse.SC_FORBIDDEN : HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), Map.of("code", code, "message", message, "data", ""));
    }
}
