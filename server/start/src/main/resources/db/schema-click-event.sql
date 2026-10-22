-- 用户行为事件表：小程序启动 / 商品曝光 / 商品浏览 / 机型点击
-- PV 口径不去重，长期留存供点击漏斗统计与用户画像分析。
-- 商品类事件在写入时由后端快照 brand/price_fen，防商品删除或改价影响历史分析。
CREATE TABLE IF NOT EXISTS `click_event` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `tenant_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '所属租户',
    `user_id`     BIGINT       NOT NULL DEFAULT 0 COMMENT '用户ID',
    `event_type`  TINYINT      NOT NULL COMMENT '1启动 2商品曝光 3商品浏览 4机型点击',
    `target_type` TINYINT      NOT NULL DEFAULT 0 COMMENT '0无目标 1商品 2机型',
    `target_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '目标ID（无目标为0）',
    `brand`       VARCHAR(32)  NULL COMMENT '商品品牌快照',
    `price_fen`   BIGINT       NULL COMMENT '商品价格快照（分）',
    `device_id`   VARCHAR(64)  NULL COMMENT '设备标识',
    `page`        VARCHAR(64)  NULL COMMENT '来源页面标识',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_ce_tenant_user` (`tenant_id`, `user_id`, `create_time`),
    KEY `idx_ce_tenant_type_time` (`tenant_id`, `event_type`, `create_time`),
    KEY `idx_ce_tenant_target` (`tenant_id`, `target_type`, `target_id`, `create_time`)
);
