-- P3/P4 回收订单与质检表（MySQL 8 / H2 MySQL 模式通用，幂等可重复执行）。
-- 订单号业务生成（R+时间戳+随机），禁止使用自增 ID 作为订单号。

CREATE TABLE IF NOT EXISTS `recycle_order` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT,
    `tenant_id`       BIGINT        NOT NULL DEFAULT 0 COMMENT '所属租户',
    `order_no`        VARCHAR(32)   NOT NULL COMMENT '业务订单号',
    `user_id`         BIGINT        NOT NULL COMMENT '下单用户',
    `openid`          VARCHAR(64)   NOT NULL COMMENT '下单用户openid（订阅消息用）',
    `model_id`        BIGINT        NOT NULL COMMENT '机型ID',
    `brand_name`      VARCHAR(64)   NOT NULL COMMENT '品牌快照',
    `model_name`      VARCHAR(128)  NOT NULL COMMENT '机型快照',
    `storage`         VARCHAR(32)   NOT NULL COMMENT '内存快照',
    `condition_key`   VARCHAR(32)   NOT NULL COMMENT '成色代码快照',
    `condition_label` VARCHAR(32)   NOT NULL COMMENT '成色名称快照',
    `issues_json`     VARCHAR(512)  NULL COMMENT '故障代码JSON数组快照',
    `quote_fen`       BIGINT        NOT NULL COMMENT '预估价（分）',
    `final_fen`       BIGINT        NULL COMMENT '质检后最终价（分）',
    `status`          INT           NOT NULL DEFAULT 10 COMMENT '10待寄出 20运输中 30质检中 40待确认 50已打款 60已完成 80已取消',
    `pickup_type`     TINYINT       NOT NULL DEFAULT 10 COMMENT '10用户邮寄 20上门取件',
    `pickup_name`     VARCHAR(32)   NULL COMMENT '取件联系人',
    `pickup_phone`    VARCHAR(20)   NULL COMMENT '取件电话',
    `pickup_address`  VARCHAR(255)  NULL COMMENT '取件地址',
    `express_company` VARCHAR(32)   NULL COMMENT '快递公司',
    `express_no`      VARCHAR(32)   NULL COMMENT '快递单号',
    `express_task_no` VARCHAR(64)   NULL COMMENT '快递100取件任务号',
    `remark`          VARCHAR(255)  NULL COMMENT '用户备注',
    `admin_remark`    VARCHAR(255)  NULL COMMENT '后台备注',
    `create_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_recycle_order_no` (`order_no`),
    KEY `idx_recycle_user` (`user_id`),
    KEY `idx_recycle_tenant_status` (`tenant_id`, `status`)
);

CREATE TABLE IF NOT EXISTS `inspection` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT,
    `tenant_id`   BIGINT        NOT NULL DEFAULT 0 COMMENT '所属租户',
    `order_no`    VARCHAR(32)   NOT NULL COMMENT '回收订单号',
    `result`      VARCHAR(512)  NOT NULL COMMENT '质检结论',
    `final_fen`   BIGINT        NOT NULL COMMENT '最终回收价（分）',
    `images_json` VARCHAR(1024) NULL COMMENT '质检图片COS key JSON数组',
    `operator_id` BIGINT        NULL COMMENT '质检员ID',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_inspection_order` (`order_no`)
);
