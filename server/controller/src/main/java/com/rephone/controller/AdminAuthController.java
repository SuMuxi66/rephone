package com.rephone.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rephone.common.exception.BizException;
import com.rephone.common.result.R;
import com.rephone.common.security.JwtTokenService;
import com.rephone.controller.support.AdminTokenFilter;
import com.rephone.mapper.UserMapper;
import com.rephone.pojo.entity.User;
import jakarta.servlet.http.HttpServletRequest;
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
 */
@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    private static final Logger log = LoggerFactory.getLogger(AdminAuthController.class);
    private static final String ROLE_ADMIN = "ADMIN";

    private final UserMapper userMapper;
    private final JwtTokenService tokenService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AdminAuthController(UserMapper userMapper, JwtTokenService tokenService) {
        this.userMapper = userMapper;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody(required = false) Map<String, String> body) {
        String username = body == null ? null : body.get("username");
        String password = body == null ? null : body.get("password");
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new BizException(40101, "用户名或密码错误");
        }
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username.trim())
                .last("limit 1"));
        boolean ok = user != null && ROLE_ADMIN.equals(user.getRole())
                && StringUtils.hasText(user.getPasswordHash())
                && encoder.matches(password, user.getPasswordHash());
        if (!ok) {
            log.warn("[admin-login] 登录失败 username={}", username);
            throw new BizException(40101, "用户名或密码错误");
        }
        String token = tokenService.createAdminToken(user.getId(), user.getTenantId(), "admin");
        return R.ok(Map.of(
                "token", token,
                "userId", user.getId(),
                "nickname", user.getNickname() == null ? "" : user.getNickname(),
                "role", "ADMIN"));
    }

    /** 身份查询：token 凭证返回 authType=token；账号 JWT 凭证返回管理员用户信息。 */
    @GetMapping("/me")
    public R<Map<String, Object>> me(HttpServletRequest request) {
        String authType = (String) request.getAttribute(AdminTokenFilter.ATTR_AUTH_TYPE);
        if ("account".equals(authType)) {
            Long userId = (Long) request.getAttribute(AdminTokenFilter.ATTR_USER_ID);
            User user = userMapper.selectById(userId);
            if (user == null || !ROLE_ADMIN.equals(user.getRole())) {
                throw new BizException(40101, "管理员账号不存在或已变更，请重新登录");
            }
            return R.ok(Map.of(
                    "authType", "account",
                    "role", "ADMIN",
                    "userId", user.getId(),
                    "nickname", user.getNickname() == null ? "" : user.getNickname()));
        }
        return R.ok(Map.of("authType", "token", "role", "ADMIN"));
    }
}
