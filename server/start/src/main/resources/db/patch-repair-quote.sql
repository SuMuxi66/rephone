-- P8 维修报价模式：repair_order 增加 pay_no 列 + repair_quote 报价表（MySQL 8）。
-- 注意：CREATE TABLE IF NOT EXISTS 不会给存量表加列，真实库必须手工执行本补丁：
--   docker exec -i rephone-mysql mysql -uroot -p<密码> rephone < server/start/src/main/resources/db/patch-repair-quote.sql
-- H2 集成测试不走本文件（每次重建库，schema-repair.sql 已含新列与新表）。
-- 若此前已执行过本补丁，ADD COLUMN 会报重复列，忽略即可。

ALTER TABLE `repair_order`
    ADD COLUMN `pay_no` VARCHAR(64) NULL COMMENT '维修支付流水号（报价模式 mock/微信支付）' AFTER `total_fen`;

CREATE TABLE IF NOT EXISTS `repair_quote` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT,
    `tenant_id`       BIGINT        NOT NULL DEFAULT 0 COMMENT '所属租户',
    `repair_order_no` VARCHAR(32)   NOT NULL COMMENT '维修单号（F前缀）',
    `quote_amount`    DECIMAL(10,2) NOT NULL COMMENT '报价金额（元），API 传输一律分',
    `description`     VARCHAR(500)  NULL COMMENT '维修说明（故障原因/维修方案）',
    `images_json`     VARCHAR(1024) NULL COMMENT '报价图片COS key JSON数组',
    `operator_id`     BIGINT        NULL COMMENT '报价操作人（管理端账号；机器凭证为 NULL）',
    `status`          TINYINT       NOT NULL DEFAULT 1 COMMENT '1待确认 2已接受 3已拒绝',
    `create_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_repair_quote_order` (`repair_order_no`),
    KEY `idx_repair_quote_tenant` (`tenant_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '维修报价单（检测→报价→用户确认→支付→维修）';
