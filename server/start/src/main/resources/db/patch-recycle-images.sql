-- P8 回收图片上传：recycle_order 增加用户机器照片列（MySQL 8）。
-- 注意：CREATE TABLE IF NOT EXISTS 不会给存量表加列，真实库必须手工执行本补丁：
--   docker exec -i rephone-mysql mysql -uroot -p<密码> rephone < server/start/src/main/resources/db/patch-recycle-images.sql
-- H2 集成测试不走本文件（每次重建库，schema-recycle.sql 已含新列）。
-- 若此前已执行过本补丁，ADD COLUMN 会报重复列，忽略即可。

ALTER TABLE `recycle_order`
    ADD COLUMN `images_json` VARCHAR(1024) NULL COMMENT '用户上传机器照片 [{type,url}] JSON数组（front/back/screen/damage）' AFTER `issues_json`;
