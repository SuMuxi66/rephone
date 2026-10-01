package com.rephone.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

/**
 * 不安全配置守卫。
 *
 * <p>两个安全要求：
 * <ol>
 *   <li><b>生产（prod profile）下任何 mock 开关或弱管理令牌都直接拒绝启动</b> —— 失败要响，不能靠人记得；</li>
 *   <li><b>非生产环境必须把生效的不安全开关喊出来</b> —— 原实现只检查 prod，
 *       于是"没设 prod profile"的开发/预发部署在 mock 登录下等于整个 /api/wx/* 认证被绕过，
 *       而日志里一句提示都没有。</li>
 * </ol>
 */
@Configuration
public class MockGuardConfig implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(MockGuardConfig.class);

    /** 示例/历史文档里出现过的弱管理令牌，禁止在生产生效。 */
    private static final Set<String> WEAK_ADMIN_TOKENS =
            Set.of("local-admin-token", "test-admin-token", "admin", "changeme", "secret");

    private final Environment env;

    public MockGuardConfig(Environment env) {
        this.env = env;
    }

    @Override
    public void afterPropertiesSet() {
        List<String> violations = collectViolations();
        if (violations.isEmpty()) {
            return;
        }
        String detail = String.join("、", violations);
        if (env.acceptsProfiles(Profiles.of("prod"))) {
            throw new IllegalStateException("生产环境禁止以下不安全配置：" + detail);
        }
        log.warn("""
                
                ==================== 开发模式：以下开关绝不可用于生产 ====================
                  {}
                  影响：mock 登录下任意请求都能换取有效会话；弱管理令牌可直接访问管理端。
                  生产部署必须设置 SPRING_PROFILES_ACTIVE=prod，届时本守卫会直接拒绝启动。
                =======================================================================""",
                detail);
    }

    private List<String> collectViolations() {
        List<String> violations = new ArrayList<>();
        check(violations, "rephone.wx.mock-login", "WX_MOCK_LOGIN");
        check(violations, "rephone.wxpay.mock", "WXPAY_MOCK");
        check(violations, "rephone.cos.mock", "COS_MOCK");
        check(violations, "rephone.express.mock", "EXPRESS_MOCK");

        String adminToken = env.getProperty("rephone.admin.token", "");
        if (!adminToken.isBlank() && WEAK_ADMIN_TOKENS.contains(adminToken.trim().toLowerCase())) {
            violations.add("管理端令牌用了弱默认值（ADMIN_TOKEN=" + adminToken.trim()
                    + "），任何人都能凭它读写全部订单与用户数据");
        }
        return violations;
    }

    private void check(List<String> violations, String key, String envName) {
        if (Boolean.parseBoolean(env.getProperty(key, "false"))) {
            violations.add(key + "（环境变量 " + envName + "）");
        }
    }
}
