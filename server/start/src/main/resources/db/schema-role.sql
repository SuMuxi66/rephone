-- P7 管理端 RBAC 角色表（MySQL 8 / H2 MySQL 模式通用，幂等可重复执行）。
-- 权限点为代码内静态枚举（AdminPermissions），角色只是权限点集合的持久化；
-- ADMIN 平台超管固定全权（代码写死 ["*"]），不从表读，防止误改锁死。

CREATE TABLE IF NOT EXISTS `role` (
    `id`               BIGINT        NOT NULL AUTO_INCREMENT,
    `role_code`        VARCHAR(16)   NOT NULL COMMENT '角色编码，写入 user.role',
    `role_name`        VARCHAR(32)   NOT NULL COMMENT '展示名',
    `permissions_json` VARCHAR(1024) NOT NULL COMMENT '权限点 JSON 数组；["*"]=全权',
    `status`           TINYINT       NOT NULL DEFAULT 1 COMMENT '1 启用 0 停用（停用后该角色账号无法登录）',
    `built_in`         TINYINT       NOT NULL DEFAULT 0 COMMENT '1 内置角色不可删除',
    `remark`           VARCHAR(255)  NULL,
    `create_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '管理端角色';

-- 内置种子角色（INSERT IGNORE 幂等；权限调整走管理端角色管理页，不在此改）
INSERT IGNORE INTO `role` (`role_code`, `role_name`, `permissions_json`, `status`, `built_in`, `remark`) VALUES
('ADMIN',        '平台超管',   '["*"]', 1, 1, '代码固定全权，不读本表'),
('TENANT_ADMIN', '租户管理员', '["recycle:manage","repair:manage","sale:manage","goods:manage","address:manage","dashboard:read"]', 1, 1, '数据自动限定本租户'),
('OPERATOR',     '运营',       '["recycle:manage","repair:manage","sale:manage","goods:manage","address:manage","dashboard:read"]', 1, 1, '平台自营运营'),
('FINANCE',      '财务',       '["finance:read","dashboard:read"]', 1, 1, '只读资金口径'),
('VIEWER',       '只读',       '["dashboard:read"]', 1, 1, '仅看板');
