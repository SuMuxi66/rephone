package com.rephone.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rephone.common.exception.BizException;
import com.rephone.common.result.R;
import com.rephone.common.security.JwtTokenService;
import com.rephone.controller.support.AdminTokenFilter;
import com.rephone.controller.support.LoginAttemptGuard;
import com.rephone.mapper.UserMapper;
import com.rephone.pojo.entity.User;
import com.rephone.service.AdminRoleService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端账号登录（匿名入口）与身份查询。
 * login 任何失败统一 40101，不区分账号不存在/密码错误；连续失败记录 warn 日志。
 * 登录准入：user.role 必须是启用的管理角色（ADMIN 平台超管代码固定放行）。
 */
@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    private static final Logger log = LoggerFactory.getLogger(AdminAuthController.class);

    private final UserMapper userMapper;
    private final JwtTokenService tokenService;
    private final LoginAttemptGuard attemptGuard;
    private final AdminRoleService adminRoleService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AdminAuthController(UserMapper userMapper, JwtTokenService tokenService,
                               LoginAttemptGuard attemptGuard, AdminRoleService adminRoleService) {
        this.userMapper = userMapper;
        this.tokenService = tokenService;
        this.attemptGuard = attemptGuard;
        this.adminRoleService = adminRoleService;
    }

    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody(required = false) Map<String, String> body) {
        String username = body == null ? null : body.get("username");
        String password = body == null ? null : body.get("password");
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new BizException(40101, "用户名或密码错误");
        }
        String attemptKey = username.trim().toLowerCase();
        if (attemptGuard.isLocked(attemptKey)) {
            long retryAfter = attemptGuard.retryAfterSeconds(attemptKey);
            log.warn("[admin-login] 触发限流 username={} retryAfter={}s", attemptKey, retryAfter);
            throw new BizException(42901, "登录失败次数过多，请 " + Math.max(1, retryAfter / 60) + " 分钟后再试");
        }
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username.trim())
                .last("limit 1"));
        boolean ok = user != null && adminRoleService.isAdminRole(user.getRole())
                && StringUtils.hasText(user.getPasswordHash())
                && encoder.matches(password, user.getPasswordHash());
        if (!ok) {
            attemptGuard.recordFailure(attemptKey);
            log.warn("[admin-login] 登录失败 username={}", username);
            throw new BizException(40101, "用户名或密码错误");
        }
        attemptGuard.reset(attemptKey);
        String token = tokenService.createAdminToken(user.getId(), user.getTenantId(), "admin");
        return R.ok(Map.of(
                "token", token,
                "userId", user.getId(),
                "tenantId", user.getTenantId() == null ? 0L : user.getTenantId(),
                "nickname", user.getNickname() == null ? "" : user.getNickname(),
                "role", "ADMIN",
                "roleCode", user.getRole(),
                "permissions", adminRoleService.permissionsOf(user.getRole())));
    }

    /** 身份查询：token 凭证返回 authType=token；账号 JWT 凭证返回管理员用户信息与权限点。 */
    @GetMapping("/me")
    public R<Map<String, Object>> me(HttpServletRequest request) {
        String authType = (String) request.getAttribute(AdminTokenFilter.ATTR_AUTH_TYPE);
        if ("account".equals(authType)) {
            Long userId = (Long) request.getAttribute(AdminTokenFilter.ATTR_USER_ID);
            String roleCode = (String) request.getAttribute(AdminTokenFilter.ATTR_ROLE_CODE);
            User user = userMapper.selectById(userId);
            if (user == null || !adminRoleService.isAdminRole(user.getRole())) {
                throw new BizException(40101, "管理员账号不存在或已变更，请重新登录");
            }
            return R.ok(Map.of(
                    "authType", "account",
                    "role", "ADMIN",
                    "roleCode", user.getRole(),
                    "permissions", adminRoleService.permissionsOf(roleCode),
                    "userId", user.getId(),
                    "tenantId", user.getTenantId() == null ? 0L : user.getTenantId(),
                    "nickname", user.getNickname() == null ? "" : user.getNickname()));
        }
        return R.ok(Map.of("authType", "token", "role", "ADMIN", "roleCode", "ADMIN",
                "permissions", List.of("*")));
    }
}
