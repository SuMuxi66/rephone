-- 已有 MySQL 库手动执行一次（CREATE TABLE IF NOT EXISTS 不会给已存在的表加列）。
-- 新库无需执行：schema-quote.sql 已包含这些列。
-- 需要列：机型参数（对照《2020-2026 主力机型参数表》，2026-10-25）。
-- 用法：mysql -uroot -p rephone < patch-phone-model-spec.sql
-- 重复执行会报 Duplicate column name，属正常，可忽略。
-- 参数数据见 data-model-specs.sql（guarded UPDATE，可直接重复执行）。

ALTER TABLE `phone_model`
    ADD COLUMN `release_month` TINYINT     NULL COMMENT '发布月份 1-12' AFTER `release_year`,
    ADD COLUMN `series`        VARCHAR(64)  NULL COMMENT '产品系列（如 Mate系列/数字系列）' AFTER `release_month`,
    ADD COLUMN `chip`         VARCHAR(128) NULL COMMENT '处理器' AFTER `series`,
    ADD COLUMN `ram`          VARCHAR(64)  NULL COMMENT '运行内存（如 12GB/16GB）' AFTER `chip`,
    ADD COLUMN `storage`      VARCHAR(64)  NULL COMMENT '机身存储（如 256GB/512GB/1TB）' AFTER `ram`,
    ADD COLUMN `battery_mah`  INT          NULL COMMENT '电池容量(mAh)' AFTER `storage`,
    ADD COLUMN `charge_w`     INT          NULL COMMENT '有线快充功率(W)' AFTER `battery_mah`,
    ADD COLUMN `wireless_w`   INT          NULL COMMENT '无线充电功率(W)，0=不支持' AFTER `charge_w`,
    ADD COLUMN `screen_size`  VARCHAR(32)  NULL COMMENT '屏幕尺寸（如 6.7英寸）' AFTER `wireless_w`,
    ADD COLUMN `resolution`   VARCHAR(32)  NULL COMMENT '分辨率（如 2832×1280）' AFTER `screen_size`,
    ADD COLUMN `refresh_rate` VARCHAR(32)  NULL COMMENT '刷新率（如 1-120 LTPO / 144）' AFTER `resolution`,
    ADD COLUMN `rear_camera`  VARCHAR(128) NULL COMMENT '后置主摄' AFTER `refresh_rate`;
