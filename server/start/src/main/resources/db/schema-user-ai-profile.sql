-- 用户 AI 画像分析结果（每个用户一份，重复生成覆盖）。
CREATE TABLE IF NOT EXISTS `user_ai_profile` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `tenant_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '所属租户',
    `user_id`     BIGINT       NOT NULL COMMENT '用户ID',
    `analysis`    TEXT         NULL COMMENT 'AI 生成的画像分析',
    `model`       VARCHAR(64)  NULL COMMENT '生成模型标识（mock=规则模板）',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_ai_profile_user` (`tenant_id`, `user_id`)
);
