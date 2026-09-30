-- 存量 MySQL 库补列脚本（P6 出售商品属性扩展）。
--
-- 背景：schema-sale.sql 使用 CREATE TABLE IF NOT EXISTS，不会为已存在的表增加列。
--   - 全新数据库 / H2 集成测试库：无需执行本文件（建表即含新列）。
--   - 已存在的 MySQL 库：必须手动执行本文件，否则商品读写会因缺列报错。
--
-- 执行前可先确认缺失列：
--   SHOW COLUMNS FROM `goods`;
--
-- 用法：
--   mysql -uroot -p rephone < patch-goods-attrs.sql

ALTER TABLE `goods`
    ADD COLUMN `brand`           VARCHAR(32)  NULL COMMENT '品牌' AFTER `desc_text`,
    ADD COLUMN `storage`         VARCHAR(16)  NULL COMMENT '内存/容量' AFTER `brand`,
    ADD COLUMN `condition_level` VARCHAR(16)  NULL COMMENT '成色' AFTER `storage`,
    ADD COLUMN `tags`            VARCHAR(255) NULL COMMENT '标签（英文逗号分隔）' AFTER `condition_level`;

-- 按品牌/成色筛选的辅助索引
ALTER TABLE `goods`
    ADD INDEX `idx_goods_brand` (`tenant_id`, `brand`, `condition_level`);
