-- 首页/货架演示商品（专业二手机交易平台改版）：真实在售机型 + 成色/内存/标签/划线价。
-- 幂等：按 name 判重（INSERT ... SELECT ... WHERE NOT EXISTS），可随启动反复执行。
-- 图片：image 暂为 NULL（卡片走占位样式），真实产品图由运营经管理后台上传后
--       UPDATE goods SET image='/img/goods/xxx.png' 即可。

INSERT INTO `goods` (`tenant_id`, `name`, `image`, `price_fen`, `original_price_fen`, `stock`,
                     `status`, `desc_text`, `brand`, `storage`, `condition_level`, `tags`)
SELECT 0, 'Apple iPhone 13 Pro 256G 远峰蓝', NULL, 469900, 579900, 3, 1,
       '国行双卡，电池效率89%，无拆无修，外观轻微使用痕迹，功能全部正常',
       'Apple', '256G', '95新', '官方质检,一机一报告,180天质保,7天退换'
WHERE NOT EXISTS (SELECT 1 FROM `goods` WHERE `name` = 'Apple iPhone 13 Pro 256G 远峰蓝');

INSERT INTO `goods` (`tenant_id`, `name`, `image`, `price_fen`, `original_price_fen`, `stock`,
                     `status`, `desc_text`, `brand`, `storage`, `condition_level`, `tags`)
SELECT 0, 'Apple iPhone 12 128G 黑色', NULL, 239900, 299900, 5, 1,
       '国行全网通，电池效率85%，屏幕无划痕，边框细微磕碰，已官方质检',
       'Apple', '128G', '9成新', '官方质检,一机一报告,180天质保'
WHERE NOT EXISTS (SELECT 1 FROM `goods` WHERE `name` = 'Apple iPhone 12 128G 黑色');

INSERT INTO `goods` (`tenant_id`, `name`, `image`, `price_fen`, `original_price_fen`, `stock`,
                     `status`, `desc_text`, `brand`, `storage`, `condition_level`, `tags`)
SELECT 0, '华为 Mate 40 Pro 8+128G 釉白色', NULL, 339900, 449900, 2, 1,
       '麒麟9000芯片，成色极佳无磕碰，全原装无维修，附质检报告',
       '华为', '8+128G', '99新', '官方质检,一机一报告,180天质保,7天退换'
WHERE NOT EXISTS (SELECT 1 FROM `goods` WHERE `name` = '华为 Mate 40 Pro 8+128G 釉白色');

INSERT INTO `goods` (`tenant_id`, `name`, `image`, `price_fen`, `original_price_fen`, `stock`,
                     `status`, `desc_text`, `brand`, `storage`, `condition_level`, `tags`)
SELECT 0, '小米 12S Ultra 12+256G 冷杉绿', NULL, 289900, 369900, 4, 1,
       '徕卡影像旗舰，外观95新，电池效率90%，功能完好已质检',
       '小米', '12+256G', '95新', '官方质检,一机一报告,180天质保'
WHERE NOT EXISTS (SELECT 1 FROM `goods` WHERE `name` = '小米 12S Ultra 12+256G 冷杉绿');

INSERT INTO `goods` (`tenant_id`, `name`, `image`, `price_fen`, `original_price_fen`, `stock`,
                     `status`, `desc_text`, `brand`, `storage`, `condition_level`, `tags`)
SELECT 0, '荣耀 Magic4 Pro 8+256G 亮黑色', NULL, 269900, 349900, 3, 1,
       '潜望长焦旗舰，99新几乎无使用痕迹，全原装，已通过36项质检',
       '荣耀', '8+256G', '99新', '官方质检,一机一报告,180天质保,7天退换'
WHERE NOT EXISTS (SELECT 1 FROM `goods` WHERE `name` = '荣耀 Magic4 Pro 8+256G 亮黑色');

-- 清理历史冒烟测试商品（名称带时间戳后缀的"冒烟商品"）
DELETE FROM `goods` WHERE `name` LIKE '%191232';
