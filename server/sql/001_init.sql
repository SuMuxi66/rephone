-- RePhone P1 核心表（MySQL 8）。幂等：可重复执行。
-- 约束：所有业务表必须带 tenant_id（tenant 表是租户注册表本身，除外）。
-- 该文件同时用于 docker-entrypoint-initdb.d 自动初始化。

CREATE DATABASE IF NOT EXISTS `rephone` DEFAULT CHARSET = utf8mb4;
USE `rephone`;

CREATE TABLE IF NOT EXISTS `tenant` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '租户ID',
    `name`        VARCHAR(64)  NOT NULL COMMENT '租户名称',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '1 启用 0 停用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '租户';

CREATE TABLE IF NOT EXISTS `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `tenant_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '所属租户，0=平台/自营',
    `openid`      VARCHAR(64)  NOT NULL COMMENT '微信openid',
    `unionid`     VARCHAR(64)  NULL COMMENT '微信unionid',
    `nickname`    VARCHAR(64)  NULL COMMENT '昵称',
    `avatar_url`  VARCHAR(512) NULL COMMENT '头像',
    `gender`      TINYINT      NOT NULL DEFAULT 0 COMMENT '0未知 1男 2女',
    `phone`       VARCHAR(20)  NULL COMMENT '手机号',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '1正常 0禁用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_openid` (`openid`),
    KEY `idx_user_tenant` (`tenant_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户';

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
