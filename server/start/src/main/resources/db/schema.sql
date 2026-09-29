-- 与 server/sql/001_init.sql 同构（MySQL 8，幂等）。启动时自动执行（spring.sql.init.mode=always）。

CREATE TABLE IF NOT EXISTS `tenant` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '租户ID',
    `name`        VARCHAR(64)  NOT NULL COMMENT '租户名称',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '1 启用 0 停用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '租户';

CREATE TABLE IF NOT EXISTS `user` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `tenant_id`     BIGINT       NOT NULL DEFAULT 0 COMMENT '所属租户，0=平台/自营',
    `openid`        VARCHAR(64)  NOT NULL COMMENT '微信openid',
    `unionid`       VARCHAR(64)  NULL COMMENT '微信unionid',
    `nickname`      VARCHAR(64)  NULL COMMENT '昵称',
    `avatar_url`    VARCHAR(512) NULL COMMENT '头像',
    `gender`        TINYINT      NOT NULL DEFAULT 0 COMMENT '0未知 1男 2女',
    `phone`         VARCHAR(20)  NULL COMMENT '手机号',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '1正常 0禁用',
    `username`      VARCHAR(64)  NULL COMMENT '管理端登录名（仅管理员账号使用）',
    `password_hash` VARCHAR(100) NULL COMMENT 'BCrypt 密码哈希（仅管理员账号使用）',
    `role`          VARCHAR(16)  NOT NULL DEFAULT 'USER' COMMENT 'USER 普通用户 ADMIN 管理员',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_openid` (`openid`),
    UNIQUE KEY `uk_user_username` (`username`),
    KEY `idx_user_tenant` (`tenant_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户';

CREATE TABLE IF NOT EXISTS `user_address` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '地址ID',
    `tenant_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '所属租户',
    `user_id`     BIGINT       NOT NULL COMMENT '所属用户',
    `name`        VARCHAR(32)  NOT NULL COMMENT '收件人',
    `phone`       VARCHAR(20)  NOT NULL COMMENT '联系电话',
    `region`      VARCHAR(128) NOT NULL COMMENT '省 市 区（空格拼接）',
    `detail`      VARCHAR(255) NOT NULL COMMENT '详细地址',
    `is_default`  TINYINT      NOT NULL DEFAULT 0 COMMENT '1默认地址',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_addr_user` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户收货地址';

CREATE TABLE IF NOT EXISTS `order_status_log` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `tenant_id`     BIGINT       NOT NULL DEFAULT 0 COMMENT '所属租户',
    `order_type`    TINYINT      NOT NULL COMMENT '10回收单 20出售单 30维修工单',
    `order_no`      VARCHAR(32)  NOT NULL COMMENT '订单号（非自增，业务生成）',
    `from_status`   INT          NOT NULL COMMENT '变更前状态',
    `to_status`     INT          NOT NULL COMMENT '变更后状态',
    `operator_type` TINYINT      NOT NULL COMMENT '10用户 20后台 30系统',
    `operator_id`   BIGINT       NULL COMMENT '操作人ID',
    `remark`        VARCHAR(255) NULL COMMENT '备注',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_log_order` (`order_no`),
    KEY `idx_log_tenant` (`tenant_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '订单状态流转日志';
