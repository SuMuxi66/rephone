-- 已有 MySQL 库手动执行一次（CREATE TABLE IF NOT EXISTS 不会给已存在的表加列）。
-- 新库无需执行：schema-recycle.sql 已包含这些列。
-- 需要列：回收单物流轨迹快照 + 快递公司编码（快递100 实时查询用）。
-- 用法：mysql -uroot -p rephone < patch-express-trace.sql
-- 重复执行会报 Duplicate column name，属正常，可忽略。

ALTER TABLE `recycle_order`
    ADD COLUMN `express_com`   VARCHAR(32)   NULL COMMENT '快递公司编码（快递100 com，小写）' AFTER `express_task_no`,
    ADD COLUMN `express_trace` VARCHAR(8000) NULL COMMENT '最近一次物流轨迹快照JSON' AFTER `express_com`,
    ADD COLUMN `trace_at`      DATETIME      NULL COMMENT '轨迹快照时间' AFTER `express_trace`;
