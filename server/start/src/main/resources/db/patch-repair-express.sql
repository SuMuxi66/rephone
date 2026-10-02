-- P7 寄修场景：repair_order 增加双向运单与轨迹快照列（MySQL 8）。
-- 注意：CREATE TABLE IF NOT EXISTS 不会给存量表加列，真实库必须手工执行本补丁：
--   docker exec -i rephone-mysql mysql -uroot -p<密码> rephone < server/start/src/main/resources/db/patch-repair-express.sql
-- H2 集成测试不走本文件（每次重建库，schema-repair.sql 已含新列）。

ALTER TABLE `repair_order`
    ADD COLUMN `express_com` VARCHAR(32) NULL COMMENT '寄出快递公司编码（快递100 com）' AFTER `appoint_time`,
    ADD COLUMN `express_company` VARCHAR(32) NULL COMMENT '寄出快递公司名' AFTER `express_com`,
    ADD COLUMN `express_no` VARCHAR(32) NULL COMMENT '用户寄出运单号' AFTER `express_company`,
    ADD COLUMN `express_trace` VARCHAR(8000) NULL COMMENT '寄出轨迹快照JSON' AFTER `express_no`,
    ADD COLUMN `trace_at` DATETIME NULL COMMENT '寄出轨迹快照时间' AFTER `express_trace`,
    ADD COLUMN `return_express_com` VARCHAR(32) NULL COMMENT '回寄快递公司编码' AFTER `trace_at`,
    ADD COLUMN `return_express_company` VARCHAR(32) NULL COMMENT '回寄快递公司名' AFTER `return_express_com`,
    ADD COLUMN `return_express_no` VARCHAR(32) NULL COMMENT '商家回寄运单号' AFTER `return_express_company`,
    ADD COLUMN `return_express_trace` VARCHAR(8000) NULL COMMENT '回寄轨迹快照JSON' AFTER `return_express_no`,
    ADD COLUMN `return_trace_at` DATETIME NULL COMMENT '回寄轨迹快照时间' AFTER `return_express_trace`;
