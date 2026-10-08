-- 评价表（MySQL 8 / H2 MySQL 模式通用，幂等可重复执行）。
-- 订单引用用业务订单号 order_no（与全仓接口口径一致），order_type 沿用 order_status_log 取值：
-- 10 回收单 20 出售单 30 维修工单。order_type + order_no 唯一 = 一单只能评一次。

CREATE TABLE IF NOT EXISTS `comment` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT,
    `tenant_id`   BIGINT        NOT NULL DEFAULT 0 COMMENT '所属租户',
    `order_type`  TINYINT       NOT NULL COMMENT '10回收单 20出售单 30维修工单',
    `order_no`    VARCHAR(32)   NOT NULL COMMENT '业务订单号',
    `user_id`     BIGINT        NOT NULL COMMENT '评价用户',
    `score`       TINYINT       NOT NULL COMMENT '星级 1-5',
    `content`     VARCHAR(500)  NULL COMMENT '评价内容',
    `images_json` VARCHAR(1024) NULL COMMENT '图片COS key JSON数组',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_comment_order` (`order_type`, `order_no`),
    KEY `idx_comment_user` (`user_id`),
    KEY `idx_comment_tenant` (`tenant_id`)
);
