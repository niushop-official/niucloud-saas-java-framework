DROP TABLE IF EXISTS `{{prefix}}recharge`;

CREATE TABLE `{{prefix}}recharge` (
  `recharge_id` int unsigned NOT NULL AUTO_INCREMENT,
  `site_id` int NOT NULL DEFAULT '0' COMMENT '站点ID',
  `recharge_name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '套餐名称',
  `face_value` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '面值',
  `buy_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '价格',
  `point` int NOT NULL DEFAULT '0' COMMENT '赠送积分',
  `growth` int NOT NULL DEFAULT '0' COMMENT '赠送成长值',
  `coupon_id` varchar(500) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '赠送优惠券ID',
  `sale_num` int NOT NULL DEFAULT '0' COMMENT '发放数量',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序号',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0关闭 1开启）',
  `create_time` int NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` int NOT NULL DEFAULT '0' COMMENT '修改时间',
  `gift_json` text COLLATE utf8mb4_general_ci COMMENT '赠品json',
  PRIMARY KEY (`recharge_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会员充值套餐';

DROP TABLE IF EXISTS `{{prefix}}recharge_order`;

CREATE TABLE `{{prefix}}recharge_order` (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `site_id` int NOT NULL DEFAULT '0' COMMENT '站点id',
  `order_no` varchar(50) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单编号',
  `order_from` varchar(55) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单来源',
  `order_type` varchar(50) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单类型',
  `out_trade_no` varchar(50) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '支付流水号',
  `order_status` int NOT NULL DEFAULT '0' COMMENT '订单状态',
  `refund_status` int NOT NULL DEFAULT '0' COMMENT '退款状态',
  `member_id` int NOT NULL DEFAULT '0' COMMENT '会员id',
  `ip` varchar(20) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '会员ip',
  `member_message` varchar(50) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '会员留言信息',
  `order_item_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '订单项目金额',
  `order_discount_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '订单优惠金额',
  `order_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '订单金额',
  `create_time` int NOT NULL DEFAULT '0' COMMENT '创建时间',
  `pay_time` int NOT NULL DEFAULT '0' COMMENT '订单支付时间',
  `close_time` int NOT NULL DEFAULT '0' COMMENT '订单关闭时间',
  `is_delete` int NOT NULL DEFAULT '0' COMMENT '是否删除(针对后台)',
  `is_enable_refund` int NOT NULL DEFAULT '0' COMMENT '是否允许退款',
  `remark` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商家留言',
  `timeout` int NOT NULL DEFAULT '0' COMMENT '通用业务超时时间记录',
  `invoice_id` int NOT NULL DEFAULT '0' COMMENT '发票id，0表示不开发票',
  `close_reason` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '关闭原因',
  PRIMARY KEY (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='订单表';

DROP TABLE IF EXISTS `{{prefix}}recharge_order_item`;

CREATE TABLE `{{prefix}}recharge_order_item` (
  `order_item_id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL DEFAULT '0' COMMENT '订单id',
  `site_id` int NOT NULL DEFAULT '0' COMMENT '站点id',
  `member_id` int NOT NULL DEFAULT '0' COMMENT '购买会员id',
  `item_id` int NOT NULL DEFAULT '0' COMMENT '项目id',
  `item_type` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '项目类型',
  `item_name` varchar(400) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '项目名称',
  `item_image` varchar(2000) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '项目图片',
  `price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '项目单价',
  `num` decimal(10,3) NOT NULL DEFAULT '0.000' COMMENT '购买数量',
  `item_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '项目总价',
  `is_refund` int NOT NULL DEFAULT '0' COMMENT '是否退款',
  `refund_no` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '退款编号',
  `refund_status` int NOT NULL DEFAULT '0' COMMENT '退款状态',
  `create_time` int NOT NULL DEFAULT '0' COMMENT '创建时间',
  PRIMARY KEY (`order_item_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='订单商品表';

DROP TABLE IF EXISTS `{{prefix}}recharge_order_item_refund`;

CREATE TABLE `{{prefix}}recharge_order_item_refund` (
  `refund_id` int NOT NULL AUTO_INCREMENT,
  `order_item_id` int NOT NULL DEFAULT '0' COMMENT '订单id',
  `order_id` int NOT NULL DEFAULT '0' COMMENT '订单id',
  `order_no` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单编号',
  `refund_no` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '退款单号',
  `site_id` int NOT NULL DEFAULT '0' COMMENT '站点id',
  `member_id` int NOT NULL DEFAULT '0' COMMENT '会员id',
  `num` decimal(10,3) NOT NULL DEFAULT '0.000' COMMENT '退货数量',
  `money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '总退款',
  `status` int NOT NULL DEFAULT '0' COMMENT '退款状态',
  `create_time` int NOT NULL DEFAULT '0' COMMENT '创建时间',
  `audit_time` int NOT NULL DEFAULT '0' COMMENT '审核时间',
  `transfer_time` int NOT NULL DEFAULT '0' COMMENT '转账时间',
  `item_type` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '项目类型',
  PRIMARY KEY (`refund_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='订单退款表';

DROP TABLE IF EXISTS `{{prefix}}recharge_order_log`;

CREATE TABLE `{{prefix}}recharge_order_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL DEFAULT '0' COMMENT '订单id',
  `site_id` int NOT NULL DEFAULT '0' COMMENT '站点id',
  `action` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '操作内容',
  `uid` int NOT NULL DEFAULT '0' COMMENT '操作人id',
  `nick_name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '操作人名称',
  `order_status` int NOT NULL DEFAULT '0' COMMENT '订单状态，操作后',
  `action_way` bigint NOT NULL DEFAULT '2' COMMENT '操作类型1买家2卖家 3 系统任务',
  `order_status_name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单状态名称，操作后',
  `action_time` int NOT NULL DEFAULT '0' COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='订单操作记录表';
