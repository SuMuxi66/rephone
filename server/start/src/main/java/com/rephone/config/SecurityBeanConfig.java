package com.rephone.config;

import com.rephone.common.security.JwtTokenService;
import com.rephone.controller.support.AdminTokenFilter;
import com.rephone.controller.support.JwtAuthFilter;
import com.rephone.mapper.TenantMapper;
import com.rephone.mapper.UserMapper;
import com.rephone.service.AdminRoleService;
import com.rephone.wechat.WxProperties;
import java.security.SecureRandom;
import java.util.HexFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * 安全相关 Bean：
 * - JwtTokenService：密钥来自 JWT_SECRET；mock-login 开发模式下允许临时随机密钥；
 *   正式模式缺失密钥直接拒绝启动（密钥不落代码）。
 * - JwtAuthFilter：只拦截 /api/wx/*，登录接口自身放行。
 */
@Configuration
public class SecurityBeanConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityBeanConfig.class);

    @Bean
    public JwtTokenService jwtTokenService(JwtProperties jwtProperties, WxProperties wxProperties) {
        String secret = jwtProperties.getSecret();
        if (!StringUtils.hasText(secret)) {
            if (wxProperties.isMockLogin()) {
                byte[] buf = new byte[32];
                new SecureRandom().nextBytes(buf);
                secret = HexFormat.of().formatHex(buf);
                log.warn("[jwt] 未配置 JWT_SECRET，mock-login 开发模式使用临时随机密钥（重启后所有 token 失效）");
            } else {
                throw new IllegalStateException(
                        "必须通过环境变量 JWT_SECRET 提供 JWT 密钥（或开发时设置 WX_MOCK_LOGIN=true）");
            }
        }
        return new JwtTokenService(secret, jwtProperties.getTtlSeconds());
    }

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilter(JwtTokenService tokenService) {
        FilterRegistrationBean<JwtAuthFilter> registration =
                new FilterRegistrationBean<>(new JwtAuthFilter(tokenService));
        registration.addUrlPatterns("/api/wx/*");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<AdminTokenFilter> adminTokenFilter(Environment env, JwtTokenService tokenService,
                                                                     UserMapper userMapper,
                                                                     TenantMapper tenantMapper,
                                                                     AdminRoleService adminRoleService) {
        FilterRegistrationBean<AdminTokenFilter> registration =
                new FilterRegistrationBean<>(
                        new AdminTokenFilter(env.getProperty("rephone.admin.token", ""), tokenService,
                                userMapper, tenantMapper, adminRoleService));
        registration.addUrlPatterns("/api/admin/*");
        registration.setOrder(0);
        return registration;
    }
}
