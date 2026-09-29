package com.rephone.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rephone.mapper.UserMapper;
import com.rephone.pojo.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 管理员种子账号：仅当 rephone.admin.bootstrap-password 非空、且库中无 admin 用户时创建。
 * 生产环境不配置该变量即不会创建任何默认账号（默认安全）。
 */
@Component
public class AdminBootstrapRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapRunner.class);
    private static final String BOOTSTRAP_USERNAME = "admin";

    private final UserMapper userMapper;
    private final String bootstrapPassword;

    public AdminBootstrapRunner(UserMapper userMapper, org.springframework.core.env.Environment env) {
        this.userMapper = userMapper;
        this.bootstrapPassword = env.getProperty("rephone.admin.bootstrap-password", "");
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!StringUtils.hasText(bootstrapPassword)) {
            return;
        }
        Long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, BOOTSTRAP_USERNAME));
        if (count != null && count > 0) {
            return;
        }
        User user = new User();
        user.setTenantId(0L);
        user.setOpenid("admin-bootstrap");
        user.setUsername(BOOTSTRAP_USERNAME);
        user.setNickname("平台管理员");
        user.setRole("ADMIN");
        user.setStatus(1);
        user.setGender(0);
        user.setAvatarUrl("");
        user.setPasswordHash(new BCryptPasswordEncoder().encode(bootstrapPassword));
        userMapper.insert(user);
        log.info("[admin-bootstrap] 已创建管理员账号 {}（密码来自 ADMIN_BOOTSTRAP_PASSWORD，请妥善保管）", BOOTSTRAP_USERNAME);
    }
}
