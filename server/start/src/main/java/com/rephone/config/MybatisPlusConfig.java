package com.rephone.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.rephone.common.context.TenantContextHolder;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 多租户配置。
 * 规则：
 * - 业务 SQL 自动追加 tenant_id = 当前上下文租户；
 * - 租户上下文为空（登录前匿名链路、平台任务）不追加条件；
 * - tenant 表是租户注册表本身，固定不过滤。
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                return new LongValue(TenantContextHolder.getOrDefault());
            }

            @Override
            public boolean ignoreTable(String tableName) {
                String name = tableName == null ? "" : tableName.toLowerCase().replace("`", "");
                if ("tenant".equals(name)) {
                    return true;
                }
                return TenantContextHolder.get() == null;
            }
        }));
        return interceptor;
    }
}
