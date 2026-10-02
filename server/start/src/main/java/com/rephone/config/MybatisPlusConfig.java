package com.rephone.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.rephone.common.context.TenantContextHolder;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 拦截器配置。
 * 多租户规则：
 * - 业务 SQL 自动追加 tenant_id = 当前上下文租户；
 * - 租户上下文为空（登录前匿名链路、平台任务）不追加条件；
 * - tenant 表是租户注册表本身，固定不过滤。
 * 分页插件必须注册（否则 selectPage 不生效：全量返回且 total=0）；
 * 改写类插件在前、分页插件在最后。
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
                // tenant 是租户注册表本身；role 是平台级角色字典（租户管理员的权限解析发生在已写租户上下文的请求里）
                if ("tenant".equals(name) || "role".equals(name)) {
                    return true;
                }
                return TenantContextHolder.get() == null;
            }
        }));
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }
}
