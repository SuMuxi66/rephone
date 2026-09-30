-- P6 出售端：商品、出售订单、售后单（MySQL 8 / H2 通用，幂等）。
-- 订单号业务生成（S/A 前缀），金额一律分（BIGINT），支付为 mock（真实微信支付需商户凭据）。

CREATE TABLE IF NOT EXISTS `goods` (
    `id`                 BIGINT       NOT NULL AUTO_INCREMENT,
    `tenant_id`          BIGINT       NOT NULL DEFAULT 0 COMMENT '所属租户',
    `name`               VARCHAR(128) NOT NULL COMMENT '商品名',
    `image`              VARCHAR(512) NULL COMMENT '商品图URL',
    `price_fen`          BIGINT       NOT NULL COMMENT '售价（分）',
    `original_price_fen` BIGINT       NULL COMMENT '原价（分，划线用）',
    `stock`              INT          NOT NULL DEFAULT 0 COMMENT '库存',
    `status`             TINYINT      NOT NULL DEFAULT 1 COMMENT '1上架 0下架',
    `desc_text`          VARCHAR(512) NULL COMMENT '商品描述',
    `brand`              VARCHAR(32)  NULL COMMENT '品牌',
    `storage`            VARCHAR(16)  NULL COMMENT '内存/容量',
    `condition_level`    VARCHAR(16)  NULL COMMENT '成色',
    `tags`               VARCHAR(255) NULL COMMENT '标签（英文逗号分隔）',
    `create_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_goods_tenant_status` (`tenant_id`, `status`)
);

CREATE TABLE IF NOT EXISTS `sale_order` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT,
    `tenant_id`       BIGINT       NOT NULL DEFAULT 0 COMMENT '所属租户',
    `order_no`        VARCHAR(32)  NOT NULL COMMENT '业务订单号（S前缀）',
    `user_id`         BIGINT       NOT NULL COMMENT '买家',
    `openid`          VARCHAR(64)  NOT NULL COMMENT '买家openid',
    `goods_id`        BIGINT       NOT NULL COMMENT '商品ID',
    `goods_name`      VARCHAR(128) NOT NULL COMMENT '商品名快照',
    `goods_image`     VARCHAR(512) NULL COMMENT '商品图快照',
    `price_fen`       BIGINT       NOT NULL COMMENT '成交单价（分，服务端取）',
    `quantity`        INT          NOT NULL DEFAULT 1,
    `total_fen`       BIGINT       NOT NULL COMMENT '合计（分，服务端计算）',
    `status`          INT          NOT NULL DEFAULT 10 COMMENT '10待付款 20已付款待发货 30已发货 40已完成 80已取消 90已退款',
    `receiver_name`   VARCHAR(32)  NOT NULL COMMENT '收件人',
    `receiver_phone`  VARCHAR(20)  NOT NULL COMMENT '联系电话',
    `receiver_addr`   VARCHAR(255) NOT NULL COMMENT '收货地址',
    `express_company` VARCHAR(32)  NULL COMMENT '快递公司',
    `express_no`      VARCHAR(32)  NULL COMMENT '快递单号',
    `pay_no`          VARCHAR(64)  NULL COMMENT '支付流水号（mock）',
    `refund_fen`      BIGINT       NULL COMMENT '退款金额（分）',
    `refund_reason`   VARCHAR(255) NULL COMMENT '退款原因',
    `remark`          VARCHAR(255) NULL COMMENT '买家备注',
    `admin_remark`    VARCHAR(255) NULL COMMENT '后台备注',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sale_order_no` (`order_no`),
    KEY `idx_sale_user` (`user_id`),
    KEY `idx_sale_tenant_status` (`tenant_id`, `status`)
);

CREATE TABLE IF NOT EXISTS `after_sale` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `tenant_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '所属租户',
    `as_no`       VARCHAR(32)  NOT NULL COMMENT '售后单号（A前缀）',
    `order_no`    VARCHAR(32)  NOT NULL COMMENT '关联出售订单号',
    `user_id`     BIGINT       NOT NULL COMMENT '申请人',
    `type`        TINYINT      NOT NULL DEFAULT 10 COMMENT '10仅退款',
    `reason`      VARCHAR(255) NOT NULL COMMENT '申请原因',
    `status`      INT          NOT NULL DEFAULT 10 COMMENT '10待审核 30已退款 40已拒绝 80已撤销',
    `refund_fen`  BIGINT       NULL COMMENT '退款金额（分）',
    `admin_remark` VARCHAR(255) NULL COMMENT '审核说明',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_after_sale_no` (`as_no`),
    KEY `idx_as_order` (`order_no`),
    KEY `idx_as_user` (`user_id`)
);
