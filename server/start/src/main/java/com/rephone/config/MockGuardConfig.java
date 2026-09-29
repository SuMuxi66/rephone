package com.rephone.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

/**
 * 生产环境 mock 开关守卫：prod profile 下任何 mock 标志开启直接拒绝启动，
 * 防止假登录/假打款/免验签回调被带上生产（安全审计 P2 发现 5）。
 */
@Configuration
public class MockGuardConfig implements InitializingBean {

    private final Environment env;

    public MockGuardConfig(Environment env) {
        this.env = env;
    }

    @Override
    public void afterPropertiesSet() {
        if (!env.acceptsProfiles(Profiles.of("prod"))) {
            return;
        }
        List<String> violations = new ArrayList<>();
        check(violations, "rephone.wx.mock-login", "WX_MOCK_LOGIN");
        check(violations, "rephone.wxpay.mock", "WXPAY_MOCK");
        check(violations, "rephone.cos.mock", "COS_MOCK");
        check(violations, "rephone.express.mock", "EXPRESS_MOCK");
        if (!violations.isEmpty()) {
            throw new IllegalStateException("生产环境禁止开启 mock 标志：" + String.join(", ", violations));
        }
    }

    private void check(List<String> violations, String key, String envName) {
        if (Boolean.parseBoolean(env.getProperty(key, "false"))) {
            violations.add(key + "（环境变量 " + envName + "）");
        }
    }
}
