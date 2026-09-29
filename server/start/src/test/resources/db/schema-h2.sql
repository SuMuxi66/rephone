-- 与 server/sql/001_init.sql 保持同构的 H2 测试版（无 ENGINE/COMMENT 等 MySQL 方言）。
-- 表名/列名一致：`user` 在 H2 MySQL 模式下用反引号规避保留字。

CREATE TABLE IF NOT EXISTS `tenant` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(64)  NOT NULL,
    `status`      TINYINT      NOT NULL DEFAULT 1,
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `user` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `tenant_id`     BIGINT       NOT NULL DEFAULT 0,
    `openid`        VARCHAR(64)  NOT NULL,
    `unionid`       VARCHAR(64)  NULL,
    `nickname`      VARCHAR(64)  NULL,
    `avatar_url`    VARCHAR(512) NULL,
    `gender`        TINYINT      NOT NULL DEFAULT 0,
    `phone`         VARCHAR(20)  NULL,
    `status`        TINYINT      NOT NULL DEFAULT 1,
    `username`      VARCHAR(64)  NULL,
    `password_hash` VARCHAR(100) NULL,
    `role`          VARCHAR(16)  NOT NULL DEFAULT 'USER',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_user_openid` UNIQUE (`openid`),
    CONSTRAINT `uk_user_username` UNIQUE (`username`)
);

CREATE TABLE IF NOT EXISTS `user_address` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `tenant_id`   BIGINT       NOT NULL DEFAULT 0,
    `user_id`     BIGINT       NOT NULL,
    `name`        VARCHAR(32)  NOT NULL,
    `phone`       VARCHAR(20)  NOT NULL,
    `region`      VARCHAR(128) NOT NULL,
    `detail`      VARCHAR(255) NOT NULL,
    `is_default`  TINYINT      NOT NULL DEFAULT 0,
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_addr_user` (`user_id`)
);

CREATE TABLE IF NOT EXISTS `order_status_log` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `tenant_id`     BIGINT       NOT NULL DEFAULT 0,
    `order_type`    TINYINT      NOT NULL,
    `order_no`      VARCHAR(32)  NOT NULL,
    `from_status`   INT          NOT NULL,
    `to_status`     INT          NOT NULL,
    `operator_type` TINYINT      NOT NULL,
    `operator_id`   BIGINT       NULL,
    `remark`        VARCHAR(255) NULL,
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);
