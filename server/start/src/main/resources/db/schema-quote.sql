-- P2 估价基础数据（MySQL 8 / H2 MySQL 模式通用，幂等可重复执行）。
-- 规则类型：10=机型+内存基准回收价(元)  20=成色系数  30=故障扣减(元)  40=屏幕成色系数。
-- model_id=0 表示全局规则（成色/故障/屏幕）。应用启动时由 spring.sql.init 自动导入（INSERT IGNORE）。
-- 注：为兼容 H2 测试库，本文件不使用 ENGINE/CHARSET/COMMENT 等 MySQL 方言子句。

CREATE TABLE IF NOT EXISTS `brand` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `tenant_id`  BIGINT       NOT NULL DEFAULT 0 COMMENT '所属租户',
    `name`       VARCHAR(64)  NOT NULL,
    `logo`       VARCHAR(512) NULL,
    `sort`       INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_brand_tenant_name` UNIQUE (`tenant_id`, `name`)
);

CREATE TABLE IF NOT EXISTS `phone_model` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `tenant_id`    BIGINT       NOT NULL DEFAULT 0 COMMENT '所属租户',
    `brand_id`     BIGINT       NOT NULL,
    `name`         VARCHAR(128) NOT NULL,
    `image`        VARCHAR(512) NULL COMMENT '机型图片URL（空则前端回退品牌logo）',
    `release_year` INT          NULL,
    `sort`         INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_model_tenant_brand_name` UNIQUE (`tenant_id`, `brand_id`, `name`)
);

CREATE TABLE IF NOT EXISTS `quote_rule` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT,
    `tenant_id`     BIGINT        NOT NULL DEFAULT 0 COMMENT '所属租户',
    `rule_type`     TINYINT       NOT NULL COMMENT '10基准价 20成色系数 30故障扣减',
    `model_id`      BIGINT        NOT NULL DEFAULT 0 COMMENT '机型ID，0=全局规则',
    `option_key`    VARCHAR(64)   NOT NULL,
    `option_label`  VARCHAR(64)   NULL,
    `numeric_value` DECIMAL(10,2) NOT NULL,
    `sort`          INT           NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_rule_tenant_type_model_key` UNIQUE (`tenant_id`, `rule_type`, `model_id`, `option_key`)
);

-- ===== 品牌 =====
INSERT IGNORE INTO `brand` (`id`, `tenant_id`, `name`, `sort`) VALUES
(1, 0, 'Apple', 1),
(2, 0, '华为', 2),
(3, 0, '小米', 3),
(4, 0, 'OPPO', 4),
(5, 0, 'vivo', 5);

-- ===== 机型 =====
INSERT IGNORE INTO `phone_model` (`id`, `tenant_id`, `brand_id`, `name`, `release_year`, `sort`) VALUES
(1,  0, 1, 'iPhone 15 Pro Max', 2023, 1),
(2,  0, 1, 'iPhone 15', 2023, 2),
(3,  0, 1, 'iPhone 14', 2022, 3),
(4,  0, 1, 'iPhone 13', 2021, 4),
(5,  0, 1, 'iPhone 12', 2020, 5),
(6,  0, 1, 'iPhone SE 3', 2022, 6),
(7,  0, 2, 'Mate 60 Pro', 2023, 1),
(8,  0, 2, 'Mate 60', 2023, 2),
(9,  0, 2, 'P60', 2023, 3),
(10, 0, 2, 'nova 12', 2023, 4),
(11, 0, 2, '畅享 70', 2023, 5),
(12, 0, 3, '小米 14 Pro', 2023, 1),
(13, 0, 3, '小米 14', 2023, 2),
(14, 0, 3, '小米 13', 2022, 3),
(15, 0, 3, 'Redmi K70', 2023, 4),
(16, 0, 3, 'Redmi Note 13', 2023, 5),
(17, 0, 4, 'Find X7', 2024, 1),
(18, 0, 4, 'Reno 11', 2023, 2),
(19, 0, 4, 'A58', 2023, 3),
(20, 0, 5, 'X100', 2023, 1),
(21, 0, 5, 'S18', 2023, 2),
(22, 0, 5, 'Y100', 2023, 3);

-- ===== 机型基准价：128GB 为基准，256GB +700，512GB +1400（元）=====
INSERT IGNORE INTO `quote_rule` (`tenant_id`, `rule_type`, `model_id`, `option_key`, `option_label`, `numeric_value`, `sort`) VALUES
(0, 10, 1,  '128GB', '128GB', 6800.00, 1), (0, 10, 1,  '256GB', '256GB', 7500.00, 2), (0, 10, 1,  '512GB', '512GB', 8200.00, 3),
(0, 10, 2,  '128GB', '128GB', 5300.00, 1), (0, 10, 2,  '256GB', '256GB', 6000.00, 2), (0, 10, 2,  '512GB', '512GB', 6700.00, 3),
(0, 10, 3,  '128GB', '128GB', 4200.00, 1), (0, 10, 3,  '256GB', '256GB', 4900.00, 2), (0, 10, 3,  '512GB', '512GB', 5600.00, 3),
(0, 10, 4,  '128GB', '128GB', 3300.00, 1), (0, 10, 4,  '256GB', '256GB', 4000.00, 2), (0, 10, 4,  '512GB', '512GB', 4700.00, 3),
(0, 10, 5,  '128GB', '128GB', 2500.00, 1), (0, 10, 5,  '256GB', '256GB', 3200.00, 2), (0, 10, 5,  '512GB', '512GB', 3900.00, 3),
(0, 10, 6,  '128GB', '128GB', 2200.00, 1), (0, 10, 6,  '256GB', '256GB', 2900.00, 2), (0, 10, 6,  '512GB', '512GB', 3600.00, 3),
(0, 10, 7,  '128GB', '128GB', 6000.00, 1), (0, 10, 7,  '256GB', '256GB', 6700.00, 2), (0, 10, 7,  '512GB', '512GB', 7400.00, 3),
(0, 10, 8,  '128GB', '128GB', 5200.00, 1), (0, 10, 8,  '256GB', '256GB', 5900.00, 2), (0, 10, 8,  '512GB', '512GB', 6600.00, 3),
(0, 10, 9,  '128GB', '128GB', 4300.00, 1), (0, 10, 9,  '256GB', '256GB', 5000.00, 2), (0, 10, 9,  '512GB', '512GB', 5700.00, 3),
(0, 10, 10, '128GB', '128GB', 3000.00, 1), (0, 10, 10, '256GB', '256GB', 3700.00, 2), (0, 10, 10, '512GB', '512GB', 4400.00, 3),
(0, 10, 11, '128GB', '128GB', 1600.00, 1), (0, 10, 11, '256GB', '256GB', 2300.00, 2), (0, 10, 11, '512GB', '512GB', 3000.00, 3),
(0, 10, 12, '128GB', '128GB', 5200.00, 1), (0, 10, 12, '256GB', '256GB', 5900.00, 2), (0, 10, 12, '512GB', '512GB', 6600.00, 3),
(0, 10, 13, '128GB', '128GB', 4300.00, 1), (0, 10, 13, '256GB', '256GB', 5000.00, 2), (0, 10, 13, '512GB', '512GB', 5700.00, 3),
(0, 10, 14, '128GB', '128GB', 3300.00, 1), (0, 10, 14, '256GB', '256GB', 4000.00, 2), (0, 10, 14, '512GB', '512GB', 4700.00, 3),
(0, 10, 15, '128GB', '128GB', 2600.00, 1), (0, 10, 15, '256GB', '256GB', 3300.00, 2), (0, 10, 15, '512GB', '512GB', 4000.00, 3),
(0, 10, 16, '128GB', '128GB', 1500.00, 1), (0, 10, 16, '256GB', '256GB', 2200.00, 2), (0, 10, 16, '512GB', '512GB', 2900.00, 3),
(0, 10, 17, '128GB', '128GB', 4500.00, 1), (0, 10, 17, '256GB', '256GB', 5200.00, 2), (0, 10, 17, '512GB', '512GB', 5900.00, 3),
(0, 10, 18, '128GB', '128GB', 2800.00, 1), (0, 10, 18, '256GB', '256GB', 3500.00, 2), (0, 10, 18, '512GB', '512GB', 4200.00, 3),
(0, 10, 19, '128GB', '128GB', 1300.00, 1), (0, 10, 19, '256GB', '256GB', 2000.00, 2), (0, 10, 19, '512GB', '512GB', 2700.00, 3),
(0, 10, 20, '128GB', '128GB', 4200.00, 1), (0, 10, 20, '256GB', '256GB', 4900.00, 2), (0, 10, 20, '512GB', '512GB', 5600.00, 3),
(0, 10, 21, '128GB', '128GB', 2700.00, 1), (0, 10, 21, '256GB', '256GB', 3400.00, 2), (0, 10, 21, '512GB', '512GB', 4100.00, 3),
(0, 10, 22, '128GB', '128GB', 1300.00, 1), (0, 10, 22, '256GB', '256GB', 2000.00, 2), (0, 10, 22, '512GB', '512GB', 2700.00, 3);

-- ===== 成色系数（全局）=====
INSERT IGNORE INTO `quote_rule` (`tenant_id`, `rule_type`, `model_id`, `option_key`, `option_label`, `numeric_value`, `sort`) VALUES
(0, 20, 0, 'COND_99', '99新',        1.00, 1),
(0, 20, 0, 'COND_95', '95新',        0.85, 2),
(0, 20, 0, 'COND_90', '9成新',       0.75, 3),
(0, 20, 0, 'COND_80', '8成新及以下',  0.60, 4);

-- ===== 故障扣减（全局，元）=====
INSERT IGNORE INTO `quote_rule` (`tenant_id`, `rule_type`, `model_id`, `option_key`, `option_label`, `numeric_value`, `sort`) VALUES
(0, 30, 0, 'SCREEN',  '屏幕划痕或磕碰',   120.00, 1),
(0, 30, 0, 'SHELL',   '外壳明显磨损',     80.00,  2),
(0, 30, 0, 'BATTERY', '电池健康低于80%',  150.00, 3),
(0, 30, 0, 'REPAIRED','曾维修或拆机',     300.00, 4),
(0, 30, 0, 'FACEID',  '面容/指纹失效',    200.00, 5),
(0, 30, 0, 'NOBOOT',  '无法开机',         500.00, 6);

-- ===== 屏幕成色系数（全局，与整机成色相乘；对齐转转/爱回收屏幕单列检测）=====
INSERT IGNORE INTO `quote_rule` (`tenant_id`, `rule_type`, `model_id`, `option_key`, `option_label`, `numeric_value`, `sort`) VALUES
(0, 40, 0, 'SCR_OK',     '无划痕无瑕疵',    1.00, 1),
(0, 40, 0, 'SCR_LIGHT',  '轻微划痕',        0.92, 2),
(0, 40, 0, 'SCR_HEAVY',  '明显划痕或磕碰',  0.78, 3),
(0, 40, 0, 'SCR_BROKEN', '碎屏或显示异常',  0.45, 4);

-- ===== 故障扣减补充（全局，元；转转/爱回收标准功能检测项。
-- SCREEN/SHELL 与成色维度语义重叠，前端已改为屏幕状态/成色表达，规则保留兼容旧客户端 =====
INSERT IGNORE INTO `quote_rule` (`tenant_id`, `rule_type`, `model_id`, `option_key`, `option_label`, `numeric_value`, `sort`) VALUES
(0, 30, 0, 'DISPLAY',   '花屏/亮线或色斑',        200.00, 11),
(0, 30, 0, 'CAMERA',    '前后摄像头异常',         120.00, 12),
(0, 30, 0, 'FLASH',     '闪光灯异常',              40.00, 13),
(0, 30, 0, 'SPEAKER',   '扬声器或听筒异常',       100.00, 14),
(0, 30, 0, 'MIC',       '麦克风或送话异常',        80.00, 15),
(0, 30, 0, 'SIGNAL',    'Wi-Fi/蓝牙或信号异常',   100.00, 16),
(0, 30, 0, 'BUTTON',    '电源/音量键失灵',         60.00, 17),
(0, 30, 0, 'VIBRATE',   '振动异常',                40.00, 18),
(0, 30, 0, 'CHARGE',    '充电异常',               100.00, 19),
(0, 30, 0, 'WATER',     '进水或受潮',             400.00, 20),
(0, 30, 0, 'MAINBOARD', '主板维修史',             600.00, 21),
(0, 30, 0, 'REBOOT',    '反复重启或死机',         300.00, 22),
(0, 30, 0, 'IDLOCK',    'ID锁/账号无法退出',      800.00, 23);
