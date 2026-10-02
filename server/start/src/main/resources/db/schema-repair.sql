-- P7 维修工单表与维修项目字典（MySQL 8 / H2 MySQL 模式通用，幂等可重复执行）。
-- 维修单号业务生成（F+时间戳+随机），禁止使用自增 ID 作为订单号。

CREATE TABLE IF NOT EXISTS `repair_item` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `tenant_id`  BIGINT       NOT NULL DEFAULT 0 COMMENT '所属租户',
    `group_name` VARCHAR(32)  NOT NULL COMMENT '故障分组名',
    `name`       VARCHAR(64)  NOT NULL COMMENT '维修项目名',
    `sort`       INT          NOT NULL DEFAULT 0,
    `enabled`    TINYINT      NOT NULL DEFAULT 1 COMMENT '1上架 0下架',
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_repair_item_tenant_name` UNIQUE (`tenant_id`, `name`)
);

CREATE TABLE IF NOT EXISTS `repair_item_price` (
    `id`        BIGINT        NOT NULL AUTO_INCREMENT,
    `tenant_id` BIGINT        NOT NULL DEFAULT 0 COMMENT '所属租户',
    `model_id`  BIGINT        NOT NULL DEFAULT 0 COMMENT '机型ID，0=全机型基准价（机型价缺省时回退）',
    `item_id`   BIGINT        NOT NULL COMMENT '维修项目ID',
    `price`     DECIMAL(10,2) NOT NULL COMMENT '上门维修价（元），后端统一元，返回小程序转分',
    `sort`      INT           NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    CONSTRAINT `uk_repair_price` UNIQUE (`tenant_id`, `model_id`, `item_id`)
);

CREATE TABLE IF NOT EXISTS `repair_order` (
    `id`            BIGINT        NOT NULL AUTO_INCREMENT,
    `tenant_id`     BIGINT        NOT NULL DEFAULT 0 COMMENT '所属租户',
    `order_no`      VARCHAR(32)   NOT NULL COMMENT '业务维修单号（F前缀）',
    `user_id`       BIGINT        NOT NULL COMMENT '下单用户',
    `openid`        VARCHAR(64)   NOT NULL COMMENT '下单用户openid',
    `model_id`      BIGINT        NOT NULL COMMENT '机型ID',
    `brand_name`    VARCHAR(64)   NOT NULL COMMENT '品牌快照',
    `model_name`    VARCHAR(128)  NOT NULL COMMENT '机型快照',
    `items_json`    VARCHAR(2048) NOT NULL COMMENT '维修项目快照 [{itemId,name,priceFen}]',
    `total_fen`     BIGINT        NOT NULL COMMENT '合计费用（分），服务端按 repair_item_price 实时计算，客户端不传金额',
    `status`        INT           NOT NULL DEFAULT 10 COMMENT '10待确认 20已预约/待寄出 25已寄出 30维修中 40待验收/待回寄 45回寄中 50已完成 80已取消',
    `service_type`  TINYINT       NOT NULL DEFAULT 10 COMMENT '10上门维修 20寄修',
    `contact_name`  VARCHAR(32)   NOT NULL COMMENT '联系人',
    `contact_phone` VARCHAR(20)   NOT NULL COMMENT '联系电话',
    `address`       VARCHAR(255)  NOT NULL COMMENT '上门地址；寄修=回寄收件地址',
    `appoint_time`  VARCHAR(32)   NOT NULL COMMENT '预约上门时间段；寄修存空串',
    `express_com`          VARCHAR(32) NULL COMMENT '寄出快递公司编码（快递100 com）',
    `express_company`      VARCHAR(32) NULL COMMENT '寄出快递公司名',
    `express_no`           VARCHAR(32) NULL COMMENT '用户寄出运单号',
    `express_trace`        TEXT NULL COMMENT '寄出轨迹快照JSON（快递100 同单号限频30分钟；TEXT 避免行超限）',
    `trace_at`             DATETIME NULL COMMENT '寄出轨迹快照时间',
    `return_express_com`   VARCHAR(32) NULL COMMENT '回寄快递公司编码',
    `return_express_company` VARCHAR(32) NULL COMMENT '回寄快递公司名',
    `return_express_no`    VARCHAR(32) NULL COMMENT '商家回寄运单号',
    `return_express_trace` TEXT NULL COMMENT '回寄轨迹快照JSON',
    `return_trace_at`      DATETIME NULL COMMENT '回寄轨迹快照时间',
    `remark`        VARCHAR(255)  NULL COMMENT '用户备注',
    `images_json`   VARCHAR(1024) NULL COMMENT '故障照片COS key JSON数组',
    `warranty_days` INT           NOT NULL DEFAULT 180 COMMENT '质保天数',
    `admin_remark`  VARCHAR(255)  NULL COMMENT '后台备注',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_repair_order_no` (`order_no`),
    KEY `idx_repair_user` (`user_id`),
    KEY `idx_repair_tenant_status` (`tenant_id`, `status`)
);

-- ===== 维修项目字典 =====
INSERT IGNORE INTO `repair_item` (`id`, `tenant_id`, `group_name`, `name`, `sort`) VALUES
(1,  0, '屏幕问题',   '换外屏',           1),
(2,  0, '屏幕问题',   '换内外屏总成',     2),
(3,  0, '电池问题',   '更换电池',         3),
(4,  0, '充电与开机', '充电接口维修',     4),
(5,  0, '充电与开机', '无法开机检测维修', 5),
(6,  0, '充电与开机', '进水处理',         6),
(7,  0, '充电与开机', '主板维修',         7),
(8,  0, '声音与拍照', '摄像头维修',       8),
(9,  0, '声音与拍照', '扬声器/听筒维修',  9),
(10, 0, '其他',       '面容/指纹维修',    10);

-- ===== 全机型基准价（model_id=0，元；机型未配价时回退此价）=====
INSERT IGNORE INTO `repair_item_price` (`tenant_id`, `model_id`, `item_id`, `price`, `sort`) VALUES
(0, 0, 1,  199.00, 1), (0, 0, 2,  399.00, 2), (0, 0, 3,  149.00, 3), (0, 0, 4,  99.00, 4),
(0, 0, 5,   88.00, 5), (0, 0, 6,  168.00, 6), (0, 0, 7,  299.00, 7), (0, 0, 8, 129.00, 8),
(0, 0, 9,   99.00, 9), (0, 0, 10, 249.00, 10);

-- ===== 热门旗舰机型价（元），未覆盖机型自动回退基准价 =====
INSERT IGNORE INTO `repair_item_price` (`tenant_id`, `model_id`, `item_id`, `price`, `sort`) VALUES
(0, 1, 1,  699.00, 1), (0, 1, 2, 1699.00, 2), (0, 1, 3, 499.00, 3), (0, 1, 4, 299.00, 4),
(0, 1, 5,  199.00, 5), (0, 1, 6,  368.00, 6), (0, 1, 7, 699.00, 7), (0, 1, 8, 399.00, 8),
(0, 1, 9,  299.00, 9), (0, 1, 10, 599.00, 10),
(0, 7, 1,  679.00, 1), (0, 7, 2, 1579.00, 2), (0, 7, 3, 359.00, 3), (0, 7, 4, 259.00, 4),
(0, 7, 5,  189.00, 5), (0, 7, 6,  338.00, 6), (0, 7, 7, 649.00, 7), (0, 7, 8, 359.00, 8),
(0, 7, 9,  279.00, 9), (0, 7, 10, 549.00, 10);
