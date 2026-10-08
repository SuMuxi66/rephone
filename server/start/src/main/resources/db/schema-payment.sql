-- P8 维修真实支付：支付订单表（MySQL 8 / H2 MySQL 模式通用，幂等可重复执行）。
-- 金额一律分（BIGINT）；pay_no 即微信 out_trade_no，全仓唯一。
-- order_type 与 order_status_log 取值一致：20 出售单 30 维修工单。

CREATE TABLE IF NOT EXISTS `payment_order` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT,
    `tenant_id`      BIGINT       NOT NULL DEFAULT 0 COMMENT '所属租户',
    `order_no`       VARCHAR(32)  NOT NULL COMMENT '业务订单号（维修 F 前缀 / 出售 S 前缀）',
    `order_type`     TINYINT      NOT NULL COMMENT '20出售 30维修',
    `pay_no`         VARCHAR(32)  NOT NULL COMMENT '支付单号（微信 out_trade_no）',
    `amount_fen`     BIGINT       NOT NULL COMMENT '支付金额（分）',
    `status`         TINYINT      NOT NULL DEFAULT 0 COMMENT '0待支付 1已支付 2已关闭',
    `transaction_id` VARCHAR(64)  NULL COMMENT '微信支付订单号',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `pay_time`       DATETIME     NULL COMMENT '支付完成时间',
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payment_pay_no` (`pay_no`),
    KEY `idx_payment_order` (`order_type`, `order_no`),
    KEY `idx_payment_tenant` (`tenant_id`)
);
