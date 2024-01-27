
DROP TABLE IF EXISTS `{{prefix}}shop_address`;
CREATE TABLE `{{prefix}}shop_address` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `contact_name` varchar(255) NOT NULL DEFAULT '' COMMENT '联系人',
  `mobile` varchar(50) NOT NULL DEFAULT '' COMMENT '手机号',
  `province_id` int(11) NOT NULL DEFAULT '0' COMMENT '省',
  `city_id` int(11) NOT NULL DEFAULT '0' COMMENT '市',
  `district_id` int(11) NOT NULL DEFAULT '0' COMMENT '区',
  `address` varchar(255) NOT NULL DEFAULT '' COMMENT '详细地址',
  `full_address` varchar(1000) NOT NULL DEFAULT '' COMMENT '地址',
  `lat` varchar(50) NOT NULL DEFAULT '' COMMENT '纬度',
  `lng` varchar(50) NOT NULL DEFAULT '' COMMENT '经度',
  `is_delivery_address` int(11) NOT NULL DEFAULT '0' COMMENT '是否是发货地址',
  `is_refund_address` int(11) NOT NULL DEFAULT '0' COMMENT '是否是退货地址',
  `is_default_delivery` int(11) NOT NULL DEFAULT '0' COMMENT '默认发货地址',
  `is_default_refund` int(11) NOT NULL DEFAULT '0' COMMENT '默认收货地址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='商家地址库';


DROP TABLE IF EXISTS `{{prefix}}shop_cart`;
CREATE TABLE `{{prefix}}shop_cart` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '购物车表ID',
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `member_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '会员ID',
  `goods_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '商品ID',
  `sku_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT 'sku id',
  `num` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '商品数量',
  `market_type` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '活动类型',
  `market_type_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '活动id',
  `create_time` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '添加时间',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '购物车商品状态',
  `invalid_remark` varchar(255) NOT NULL DEFAULT '' COMMENT '失效原因',
  PRIMARY KEY (`id`),
  KEY `goods_id` (`goods_id`),
  KEY `member_id` (`member_id`),
  KEY `sku_id` (`sku_id`),
  KEY `type` (`market_type`),
  KEY `type_id` (`market_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='购物车表';


DROP TABLE IF EXISTS `{{prefix}}shop_coupon`;
CREATE TABLE `{{prefix}}shop_coupon` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `title` varchar(255) NOT NULL DEFAULT '' COMMENT '名称',
  `start_time` int(11) NOT NULL DEFAULT '0' COMMENT '活动开启时间',
  `end_time` int(11) NOT NULL DEFAULT '0' COMMENT '活动结束时间',
  `remain_count` int(11) NOT NULL DEFAULT '0' COMMENT '剩余数量',
  `receive_count` int(11) NOT NULL DEFAULT '0' COMMENT '已领取数量',
  `limit_count` int(11) NOT NULL DEFAULT '0' COMMENT '单个会员限制领取数量',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT ' 状态 1 正常 2 未开启 3 已无效',
  `create_time` int(11) NOT NULL DEFAULT '0' COMMENT '添加时间',
  `price` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '面值',
  `min_condition_money` decimal(10,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '商品最低多少金额可用优惠券',
  `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '优惠券类型 1通用优惠券 2商品品类优惠券 3商品优惠券',
  `receive_type` int(11) NOT NULL DEFAULT '0' COMMENT '领取方式',
  `valid_type` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '有效时间',
  `length` int(11) NOT NULL DEFAULT '0' COMMENT '有效期时长(天)',
  `valid_start_time` int(11) NOT NULL DEFAULT '0' COMMENT '有效期开始时间',
  `valid_end_time` int(11) NOT NULL DEFAULT '0' COMMENT '有效期结束时间',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `receive_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT ' 状态 1 正常 2 关闭',
  PRIMARY KEY (`id`),
  KEY `status` (`status`),
  KEY `title` (`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='优惠券表';


DROP TABLE IF EXISTS `{{prefix}}shop_coupon_goods`;
CREATE TABLE `{{prefix}}shop_coupon_goods` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `coupon_id` int(11) NOT NULL DEFAULT '0' COMMENT '优惠券模板id',
  `goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品id',
  `category_id` int(11) NOT NULL DEFAULT '0' COMMENT '分类id',
  PRIMARY KEY (`id`),
  KEY `index_category_id` (`category_id`),
  KEY `index_coupon_id` (`coupon_id`),
  KEY `index_goods_id` (`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='优惠券商品或品类关联表';


DROP TABLE IF EXISTS `{{prefix}}shop_coupon_member`;
CREATE TABLE `{{prefix}}shop_coupon_member` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '优惠券发放记录id',
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `coupon_id` int(11) NOT NULL DEFAULT '0' COMMENT '优惠券id',
  `member_id` int(11) NOT NULL DEFAULT '0' COMMENT '会员id',
  `create_time` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '领取时间',
  `expire_time` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '过期时间',
  `use_time` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '使用时间',
  `type` varchar(32) NOT NULL DEFAULT '' COMMENT '优惠券类型',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态',
  `title` varchar(255) NOT NULL DEFAULT '' COMMENT '优惠券名称',
  `price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '面值',
  `min_condition_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '最低使用门槛',
  `receive_type` varchar(255) NOT NULL DEFAULT '' COMMENT '领取方式',
  `trade_id` int(11) NOT NULL DEFAULT '0' COMMENT '关联业务id',
  PRIMARY KEY (`id`),
  KEY `coupon_id` (`coupon_id`),
  KEY `member_id` (`member_id`),
  KEY `status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='优惠券会员领取记录表';


DROP TABLE IF EXISTS `{{prefix}}shop_delivery_company`;
CREATE TABLE `{{prefix}}shop_delivery_company` (
  `company_id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `company_name` varchar(255) NOT NULL DEFAULT '' COMMENT '物流公司名称',
  `logo` varchar(255) NOT NULL DEFAULT '' COMMENT '物流公司logo',
  `url` varchar(255) NOT NULL DEFAULT '' COMMENT '物流公司网站',
  `express_no` varchar(255) NOT NULL DEFAULT '' COMMENT '物流公司编号',
  `create_time` int(11) NOT NULL DEFAULT '0',
  `update_time` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;


DROP TABLE IF EXISTS `{{prefix}}shop_delivery_deliver`;
CREATE TABLE `{{prefix}}shop_delivery_deliver` (
  `deliver_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '配送员id',
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `deliver_name` varchar(255) NOT NULL DEFAULT '' COMMENT '配送员名称',
  `deliver_mobile` varchar(20) NOT NULL DEFAULT '' COMMENT '配送员手机号',
  `create_time` int(11) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `modify_time` int(11) NOT NULL DEFAULT '0' COMMENT '修改时间',
  `store_id` int(11) NOT NULL DEFAULT '0' COMMENT '门店id',
  PRIMARY KEY (`deliver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='配送员表';


DROP TABLE IF EXISTS `{{prefix}}shop_delivery_local_delivery`;
CREATE TABLE `{{prefix}}shop_delivery_local_delivery` (
  `local_id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `fee_type` varchar(30) NOT NULL DEFAULT '' COMMENT '费用类型',
  `base_dist` decimal(10,1) NOT NULL DEFAULT '0.0' COMMENT '多少km内',
  `base_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '配送费用',
  `grad_dist` decimal(10,1) NOT NULL DEFAULT '0.0' COMMENT '每超出多少km内',
  `grad_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '配送费用',
  `weight_start` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '重量多少内不额外收费',
  `weight_unit` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '每超出多少kg',
  `weight_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '价格',
  `delivery_type` varchar(2000) NOT NULL DEFAULT '' COMMENT '配送类型',
  `area` longtext NOT NULL COMMENT '配送区域',
  `center` varchar(255) NOT NULL DEFAULT '' COMMENT '发货地址中心点',
  PRIMARY KEY (`local_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;


DROP TABLE IF EXISTS `{{prefix}}shop_delivery_shipping_template`;
CREATE TABLE `{{prefix}}shop_delivery_shipping_template` (
  `template_id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `template_name` varchar(50) NOT NULL DEFAULT '' COMMENT '模板名称',
  `fee_type` varchar(20) NOT NULL DEFAULT '' COMMENT '运费计算方式1.重量2体积3按件',
  `create_time` int(11) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` int(11) NOT NULL DEFAULT '0' COMMENT '修改时间',
  `is_free_shipping` smallint(6) NOT NULL DEFAULT '0' COMMENT '该区域是否包邮',
  `no_delivery` smallint(6) NOT NULL DEFAULT '0' COMMENT '是否指定该区域不配送',
  PRIMARY KEY (`template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='运费模板';


DROP TABLE IF EXISTS `{{prefix}}shop_delivery_shipping_template_item`;
CREATE TABLE `{{prefix}}shop_delivery_shipping_template_item` (
  `item_id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `template_id` int(11) NOT NULL DEFAULT '0' COMMENT '模板id',
  `city_id` int(11) NOT NULL DEFAULT '0' COMMENT '市id',
  `snum` int(11) NOT NULL DEFAULT '0' COMMENT '起步计算标准',
  `sprice` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '起步计算价格',
  `xnum` int(11) NOT NULL DEFAULT '0' COMMENT '续步计算标准',
  `xprice` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '续步计算价格',
  `fee_type` varchar(20) NOT NULL DEFAULT '1' COMMENT '运费计算方式',
  `fee_area_ids` text NOT NULL COMMENT '运费设置区域id集',
  `fee_area_names` text NOT NULL COMMENT '运费设置区域名称集',
  `no_delivery` smallint(6) NOT NULL DEFAULT '0' COMMENT '是否指定该区域不配送',
  `no_delivery_area_ids` text NOT NULL COMMENT '不配送的区域id集',
  `no_delivery_area_names` text NOT NULL COMMENT '不配送的区域名称集',
  `is_free_shipping` smallint(6) NOT NULL DEFAULT '0' COMMENT '该区域是否包邮',
  `free_shipping_area_ids` text NOT NULL COMMENT '包邮的区域id集',
  `free_shipping_area_names` text NOT NULL COMMENT '包邮的区域名称集',
  `free_shipping_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '满足包邮的条件',
  `free_shipping_num` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`item_id`),
  KEY `express_template_item_city_id` (`city_id`),
  KEY `express_template_item_fee_type` (`fee_type`),
  KEY `express_template_item_template_id` (`template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='运费模板细节';


DROP TABLE IF EXISTS `{{prefix}}shop_goods`;
CREATE TABLE `{{prefix}}shop_goods` (
  `goods_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '商品id',
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `goods_name` varchar(255) NOT NULL DEFAULT '' COMMENT '商品名称',
  `goods_type` varchar(50) NOT NULL DEFAULT 'real' COMMENT '商品类型',
  `sub_title` varchar(255) NOT NULL DEFAULT '' COMMENT '副标题',
  `goods_cover` varchar(2000) NOT NULL DEFAULT '' COMMENT '商品封面',
  `goods_image` text COMMENT '商品图片',
  `goods_category` varchar(255) NOT NULL DEFAULT '' COMMENT '商品分类',
  `goods_desc` text COMMENT '商品介绍',
  `brand_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品品牌id',
  `label_ids` varchar(255) NOT NULL DEFAULT '' COMMENT '标签组',
  `service_ids` varchar(255) NOT NULL DEFAULT '' COMMENT '商品服务',
  `unit` varchar(255) NOT NULL DEFAULT '件' COMMENT '单位',
  `stock` int(11) NOT NULL DEFAULT '0' COMMENT '商品库存（总和）',
  `sale_num` int(11) NOT NULL DEFAULT '0' COMMENT '销量',
  `virtual_sale_num` int(11) NOT NULL DEFAULT '0' COMMENT '虚拟销量',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '商品状态（1.正常0下架）',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `delivery_type` varchar(255) NOT NULL DEFAULT '' COMMENT '支持的配送方式',
  `is_free_shipping` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否免邮',
  `fee_type` varchar(255) NOT NULL DEFAULT '' COMMENT '运费设置，选择模板：template，固定运费：fixed',
  `delivery_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '固定运费',
  `delivery_template_id` int(11) NOT NULL DEFAULT '0' COMMENT '运费模板',
  `supplier_id` int(11) NOT NULL DEFAULT '0' COMMENT '供应商id',
  `create_time` int(11) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` int(11) NOT NULL DEFAULT '0' COMMENT '修改时间',
  `delete_time` int(11) NOT NULL DEFAULT '0' COMMENT '删除时间',
  PRIMARY KEY (`goods_id`),
  KEY `idx_goods_category` (`goods_category`),
  KEY `idx_goods_create_time` (`create_time`),
  KEY `idx_goods_delete_time` (`delete_time`),
  KEY `idx_goods_name` (`goods_name`),
  KEY `idx_goods_sort` (`sort`),
  KEY `idx_goods_status` (`status`),
  KEY `idx_goods_sub_title` (`sub_title`),
  KEY `IDX_ns_goods_goods_class` (`goods_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='商品表';


DROP TABLE IF EXISTS `{{prefix}}shop_goods_brand`;
CREATE TABLE `{{prefix}}shop_goods_brand` (
  `brand_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '品牌ID',
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `brand_name` varchar(100) NOT NULL DEFAULT '' COMMENT '品牌名称',
  `logo` varchar(255) NOT NULL DEFAULT '' COMMENT '品牌logo',
  `desc` text NOT NULL COMMENT '品牌介绍',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `create_time` int(11) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` int(11) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `delete_time` int(11) NOT NULL DEFAULT '0' COMMENT '删除时间',
  PRIMARY KEY (`brand_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='商品品牌表';


DROP TABLE IF EXISTS `{{prefix}}shop_goods_category`;
CREATE TABLE `{{prefix}}shop_goods_category` (
  `category_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '商品分类id',
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `category_name` varchar(255) NOT NULL DEFAULT '' COMMENT '分类名称',
  `image` varchar(255) NOT NULL DEFAULT '' COMMENT '分类图片',
  `level` int(11) NOT NULL DEFAULT '0' COMMENT '层级',
  `pid` int(11) NOT NULL DEFAULT '0' COMMENT '上级分类id',
  `category_full_name` varchar(255) NOT NULL DEFAULT '' COMMENT '组装分类名称',
  `is_show` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否显示（1：显示，0：不显示）',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序号',
  `create_time` int(11) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` int(11) NOT NULL DEFAULT '0' COMMENT '修改时间',
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='商品分类表';


DROP TABLE IF EXISTS `{{prefix}}shop_goods_collect`;
CREATE TABLE `{{prefix}}shop_goods_collect` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `member_id` int(11) NOT NULL DEFAULT '0' COMMENT '会员id',
  `goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品id',
  `create_time` int(11) NOT NULL DEFAULT '0' COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  KEY `IDX_member_collect_goods` (`goods_id`),
  KEY `IDX_member_collect_member` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='商品收藏记录表';


DROP TABLE IF EXISTS `{{prefix}}shop_goods_evaluate`;
CREATE TABLE `{{prefix}}shop_goods_evaluate` (
  `evaluate_id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `order_id` int(11) NOT NULL DEFAULT '0' COMMENT '订单id',
  `order_goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '订单项ID',
  `goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品ID',
  `member_id` int(11) NOT NULL DEFAULT '0' COMMENT '会员ID',
  `member_head` varchar(255) NOT NULL DEFAULT '' COMMENT '会员头像',
  `member_name` varchar(100) NOT NULL DEFAULT '' COMMENT '会员名称',
  `content` varchar(3000) NOT NULL COMMENT '评价内容',
  `images` varchar(3000) NOT NULL DEFAULT '' COMMENT '评价图片',
  `is_anonymous` tinyint(4) NOT NULL DEFAULT '1' COMMENT '1匿名  2不匿名',
  `scores` tinyint(4) NOT NULL DEFAULT '1' COMMENT '评论分数 1-5',
  `is_audit` tinyint(4) NOT NULL DEFAULT '1' COMMENT '审核状态 1待审 2通过 3拒绝',
  `explain_first` varchar(3000) NOT NULL DEFAULT '' COMMENT '解释内容',
  `topping` int(11) NOT NULL DEFAULT '0' COMMENT '排序 置顶',
  `create_time` int(11) NOT NULL DEFAULT '0' COMMENT '评论时间',
  `update_time` int(11) NOT NULL DEFAULT '0' COMMENT '修改时间',
  PRIMARY KEY (`evaluate_id`),
  KEY `idx_shop_goods_evaluate_create_time` (`create_time`),
  KEY `idx_shop_goods_evaluate_goods_id` (`goods_id`),
  KEY `idx_shop_goods_evaluate_is_anonymous` (`is_anonymous`),
  KEY `idx_shop_goods_evaluate_is_audit` (`is_audit`),
  KEY `idx_shop_goods_evaluate_member_id` (`member_id`),
  KEY `idx_shop_goods_evaluate_order_id` (`order_id`),
  KEY `idx_shop_goods_evaluate_scores` (`scores`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='商品评价表';


DROP TABLE IF EXISTS `{{prefix}}shop_goods_label`;
CREATE TABLE `{{prefix}}shop_goods_label` (
  `label_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `label_name` varchar(255) NOT NULL DEFAULT '' COMMENT '标签名称',
  `memo` varchar(255) NOT NULL DEFAULT '' COMMENT '标签说明',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `create_time` int(11) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` int(11) NOT NULL DEFAULT '0' COMMENT '更新时间',
  PRIMARY KEY (`label_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='商品标签表';


DROP TABLE IF EXISTS `{{prefix}}shop_goods_service`;
CREATE TABLE `{{prefix}}shop_goods_service` (
  `service_id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `service_name` varchar(255) NOT NULL DEFAULT '' COMMENT '服务名称',
  `image` varchar(255) NOT NULL DEFAULT '' COMMENT '图片',
  `desc` varchar(255) NOT NULL DEFAULT '' COMMENT '描述',
  `create_time` int(11) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` int(11) NOT NULL DEFAULT '0' COMMENT '更新时间',
  PRIMARY KEY (`service_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='商品服务表';


DROP TABLE IF EXISTS `{{prefix}}shop_goods_sku`;
CREATE TABLE `{{prefix}}shop_goods_sku` (
  `sku_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '商品sku_id',
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `sku_name` varchar(255) NOT NULL DEFAULT '' COMMENT '商品sku名称',
  `sku_image` varchar(2000) NOT NULL DEFAULT '' COMMENT 'sku主图',
  `sku_no` varchar(255) NOT NULL DEFAULT '' COMMENT '商品sku编码',
  `goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品id',
  `sku_spec_format` text COMMENT 'sku规格格式',
  `price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT 'sku单价',
  `market_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '划线价',
  `sale_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '实际卖价（有活动显示活动价，默认原价）',
  `cost_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT 'sku成本价',
  `stock` int(11) NOT NULL DEFAULT '0' COMMENT '商品sku库存',
  `weight` decimal(10,3) NOT NULL DEFAULT '0.000' COMMENT '重量（单位kg）',
  `volume` decimal(10,3) NOT NULL DEFAULT '0.000' COMMENT '体积（单位立方米）',
  `sale_num` int(11) NOT NULL DEFAULT '0' COMMENT '销量',
  `is_default` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否默认',
  PRIMARY KEY (`sku_id`),
  KEY `idx_goods_sku_is_default` (`is_default`),
  KEY `idx_goods_sku_price` (`price`),
  KEY `idx_goods_sku_sale_price` (`sale_price`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='商品规格表';


DROP TABLE IF EXISTS `{{prefix}}shop_goods_spec`;
CREATE TABLE `{{prefix}}shop_goods_spec` (
  `spec_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '规格id',
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '关联商品id',
  `spec_name` varchar(255) NOT NULL DEFAULT '' COMMENT '规格项名称',
  `spec_values` text COMMENT '规格值名称，多个逗号隔开',
  PRIMARY KEY (`spec_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='商品规格项/值表';


DROP TABLE IF EXISTS `{{prefix}}shop_invoice`;
CREATE TABLE `{{prefix}}shop_invoice` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `member_id` int(11) NOT NULL DEFAULT '0' COMMENT '会员id',
  `trade_type` varchar(10) NOT NULL DEFAULT 'order' COMMENT '开票分类 order:订单',
  `trade_id` int(11) NOT NULL DEFAULT '0' COMMENT '业务id',
  `header_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '抬头类型',
  `header_name` varchar(100) NOT NULL DEFAULT '' COMMENT '名称（发票抬头）',
  `type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '发票类型',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '发票内容',
  `tax_number` varchar(50) NOT NULL DEFAULT '' COMMENT '公司税号',
  `mobile` varchar(30) NOT NULL DEFAULT '' COMMENT '开票人手机号',
  `email` varchar(100) NOT NULL DEFAULT '' COMMENT '开票人邮箱',
  `telephone` varchar(30) NOT NULL DEFAULT '' COMMENT '注册电话',
  `address` varchar(255) NOT NULL DEFAULT '' COMMENT '注册地址',
  `bank_name` varchar(50) NOT NULL DEFAULT '' COMMENT '开户银行',
  `bank_card_number` varchar(50) NOT NULL DEFAULT '' COMMENT '银行账号',
  `money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '开票金额',
  `is_invoice` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否开票',
  `invoice_number` varchar(50) NOT NULL DEFAULT '' COMMENT '发票代码',
  `invoice_voucher` varchar(1000) NOT NULL COMMENT '发票凭证',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` int(11) NOT NULL DEFAULT '0' COMMENT '申请时间',
  `invoice_time` int(11) NOT NULL DEFAULT '0' COMMENT '开票时间',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '是否生效',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='发票表';


DROP TABLE IF EXISTS `{{prefix}}shop_order`;
CREATE TABLE `{{prefix}}shop_order` (
  `order_id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `order_no` varchar(50) NOT NULL DEFAULT '' COMMENT '订单编号',
  `body` varchar(1000) NOT NULL DEFAULT '' COMMENT '订单内容',
  `order_type` varchar(55) NOT NULL DEFAULT '' COMMENT '订单类型',
  `order_from` varchar(55) NOT NULL DEFAULT '' COMMENT '订单来源',
  `out_trade_no` varchar(50) NOT NULL DEFAULT '' COMMENT '支付流水号',
  `status` varchar(55) NOT NULL DEFAULT '' COMMENT '订单状态',
  `member_id` int(11) NOT NULL DEFAULT '0' COMMENT '会员id',
  `ip` varchar(20) NOT NULL DEFAULT '' COMMENT 'ip',
  `goods_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '商品金额',
  `delivery_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '配送金额',
  `discount_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '优惠金额',
  `order_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '订单金额',
  `pay_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '支付金额',
  `invoice_id` int(11) NOT NULL DEFAULT '0' COMMENT '发票id，0表示不开发票',
  `create_time` int(11) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `pay_time` int(11) NOT NULL DEFAULT '0' COMMENT '订单支付时间',
  `delivery_time` int(11) NOT NULL DEFAULT '0' COMMENT '订单发货时间',
  `take_time` int(11) NOT NULL DEFAULT '0' COMMENT '订单收货时间',
  `finish_time` int(11) NOT NULL DEFAULT '0' COMMENT '订单完成时间',
  `close_time` int(11) NOT NULL DEFAULT '0' COMMENT '订单关闭时间',
  `delete_time` int(11) NOT NULL DEFAULT '0' COMMENT '是否删除(针对后台)',
  `timeout` int(11) NOT NULL DEFAULT '0' COMMENT '通用业务超时时间记录',
  `delivery_type` varchar(255) NOT NULL DEFAULT '' COMMENT '配送方式',
  `take_store_id` int(11) NOT NULL DEFAULT '0' COMMENT '自提点',
  `taker_name` varchar(50) NOT NULL DEFAULT '' COMMENT '收货人',
  `taker_mobile` varchar(50) NOT NULL DEFAULT '' COMMENT '收货人手机号',
  `taker_province` int(11) NOT NULL DEFAULT '0' COMMENT '收货省',
  `taker_city` int(11) NOT NULL DEFAULT '0' COMMENT '收货市',
  `taker_district` int(11) NOT NULL DEFAULT '0' COMMENT '收货区县',
  `taker_address` varchar(50) NOT NULL DEFAULT '' COMMENT '收货地址',
  `taker_full_address` varchar(50) NOT NULL DEFAULT '' COMMENT '收货详细地址',
  `taker_longitude` varchar(50) NOT NULL DEFAULT '' COMMENT '收货地址经度',
  `taker_latitude` varchar(50) NOT NULL DEFAULT '' COMMENT '收货详细纬度',
  `taker_store_id` varchar(50) NOT NULL DEFAULT '' COMMENT '收货门店',
  `is_enable_refund` int(11) NOT NULL DEFAULT '0' COMMENT '是否允许退款',
  `member_remark` varchar(50) NOT NULL DEFAULT '' COMMENT '会员留言信息',
  `shop_remark` varchar(255) NOT NULL DEFAULT '' COMMENT '商家留言',
  `close_remark` varchar(255) NOT NULL DEFAULT '' COMMENT '关闭原因',
  `close_type` varchar(255) NOT NULL DEFAULT '' COMMENT '关闭来源(未支付自动关闭   手动关闭  退款关闭)',
  `refund_status` int(11) NOT NULL DEFAULT '1' COMMENT '退款状态  1不存在退款  2 部分退款  3 全部退款',
  `has_goods_types` varchar(255) NOT NULL DEFAULT '' COMMENT '包含的商品类型 json',
  `is_evaluate` int(11) NOT NULL DEFAULT '0' COMMENT '是否评论',
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='订单表';


DROP TABLE IF EXISTS `{{prefix}}shop_order_delivery`;
CREATE TABLE `{{prefix}}shop_order_delivery` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `order_id` int(11) NOT NULL DEFAULT '0',
  `name` varchar(50) NOT NULL DEFAULT '' COMMENT '包裹名称',
  `delivery_type` varchar(50) NOT NULL DEFAULT '' COMMENT '配送方式',
  `sub_delivery_type` varchar(50) NOT NULL DEFAULT '' COMMENT '详细配送方式',
  `express_company_id` int(11) NOT NULL DEFAULT '0' COMMENT '快递公司id',
  `express_number` varchar(50) NOT NULL DEFAULT '' COMMENT '配送单号',
  `local_deliver_id` int(11) NOT NULL DEFAULT '0' COMMENT '同城配送员',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '配送状态',
  `create_time` int(11) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `remark` varchar(1000) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='订单发货表';


DROP TABLE IF EXISTS `{{prefix}}shop_order_discount`;
CREATE TABLE `{{prefix}}shop_order_discount` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `order_id` int(11) NOT NULL DEFAULT '0' COMMENT '订单id',
  `order_goods_ids` varchar(255) NOT NULL DEFAULT '' COMMENT '参与的订单商品项',
  `type` varchar(255) NOT NULL DEFAULT '' COMMENT '类型 discount 优惠，gift 赠送',
  `num` int(11) NOT NULL DEFAULT '0' COMMENT '使用数量',
  `money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '优惠金额',
  `discount_type` varchar(255) NOT NULL DEFAULT '' COMMENT '优惠类型',
  `discount_type_id` int(11) NOT NULL DEFAULT '0' COMMENT '优惠类型id',
  `content` varchar(255) NOT NULL DEFAULT '' COMMENT '订单优惠说明',
  `create_time` int(11) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `status` int(11) NOT NULL DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='订单优惠表';


DROP TABLE IF EXISTS `{{prefix}}shop_order_discount_goods`;
CREATE TABLE `{{prefix}}shop_order_discount_goods` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `order_discount_id` int(11) NOT NULL DEFAULT '0' COMMENT '订单优惠id',
  `order_id` int(11) NOT NULL DEFAULT '0' COMMENT '订单id',
  `order_goods_id` varchar(255) NOT NULL DEFAULT '' COMMENT '参与的订单商品项',
  `type` varchar(255) NOT NULL DEFAULT '' COMMENT '类型 discount 优惠，gift 赠送',
  `num` int(11) NOT NULL DEFAULT '0' COMMENT '使用数量',
  `money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '优惠金额',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='订单项优惠表';


DROP TABLE IF EXISTS `{{prefix}}shop_order_goods`;
CREATE TABLE `{{prefix}}shop_order_goods` (
  `order_goods_id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `order_id` int(11) NOT NULL DEFAULT '0' COMMENT '订单id',
  `member_id` int(11) NOT NULL DEFAULT '0' COMMENT '购买会员id',
  `goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品id',
  `sku_id` int(11) NOT NULL DEFAULT '0' COMMENT '商品规格id',
  `goods_name` varchar(400) NOT NULL DEFAULT '' COMMENT '商品名称',
  `sku_name` varchar(400) NOT NULL DEFAULT '' COMMENT '商品规格名称',
  `goods_image` varchar(2000) NOT NULL DEFAULT '' COMMENT '商品图片',
  `sku_image` varchar(1000) NOT NULL COMMENT 'sku规格图片',
  `price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '商品单价',
  `num` int(11) NOT NULL DEFAULT '0' COMMENT '购买数量',
  `goods_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '商品总价',
  `is_enable_refund` int(11) NOT NULL DEFAULT '0' COMMENT '是否允许退款',
  `goods_type` varchar(255) NOT NULL DEFAULT '' COMMENT '商品类型',
  `delivery_status` varchar(255) NOT NULL DEFAULT '' COMMENT '配送状态',
  `delivery_id` int(11) NOT NULL DEFAULT '0' COMMENT '发货单号',
  `discount_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '优惠金额',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态',
  `order_refund_no` varchar(50) NOT NULL DEFAULT '' COMMENT '退款单号',
  `order_goods_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '订单项实付金额',
  `original_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '商品原价',
  PRIMARY KEY (`order_goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='订单项表';


DROP TABLE IF EXISTS `{{prefix}}shop_order_log`;
CREATE TABLE `{{prefix}}shop_order_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_id` int(11) NOT NULL DEFAULT '0' COMMENT '订单id',
  `main_type` varchar(255) NOT NULL DEFAULT '操作人类型',
  `main_id` int(11) NOT NULL DEFAULT '0' COMMENT '操作人id',
  `status` int(11) DEFAULT NULL COMMENT '订单状态',
  `type` varchar(255) NOT NULL DEFAULT '',
  `content` varchar(255) DEFAULT NULL COMMENT '日志内容',
  `create_time` int(11) DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='订单日志表';


DROP TABLE IF EXISTS `{{prefix}}shop_order_refund`;
CREATE TABLE `{{prefix}}shop_order_refund` (
  `refund_id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `order_id` int(11) NOT NULL DEFAULT '0' COMMENT '订单id',
  `order_goods_id` int(11) NOT NULL DEFAULT '0' COMMENT '订单项id',
  `order_refund_no` varchar(255) NOT NULL DEFAULT '0' COMMENT '退款单号',
  `refund_type` varchar(255) NOT NULL DEFAULT '0' COMMENT '退款方式 ',
  `reason` varchar(255) NOT NULL DEFAULT '0' COMMENT '退款原因 ',
  `member_id` int(11) NOT NULL DEFAULT '0' COMMENT '会员id',
  `apply_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '申请退款',
  `money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '实际退款',
  `status` varchar(30) NOT NULL DEFAULT '0' COMMENT '退款状态',
  `create_time` int(11) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `transfer_time` int(11) NOT NULL DEFAULT '0' COMMENT '转账时间',
  `remark` varchar(2000) NOT NULL DEFAULT '描述' COMMENT '描述',
  `voucher` varchar(2000) NOT NULL DEFAULT '凭证' COMMENT '凭证',
  `source` varchar(255) NOT NULL DEFAULT '' COMMENT '来源 system 系统 member 会员',
  `timeout` int(11) NOT NULL DEFAULT '0' COMMENT '操作超时时间',
  `refund_no` varchar(255) NOT NULL DEFAULT '' COMMENT '退款交易号',
  `delivery` varchar(500) NOT NULL DEFAULT '' COMMENT '退货配送信息',
  `shop_reason` varchar(255) NOT NULL DEFAULT '' COMMENT '上架拒绝原因',
  `refund_address` varchar(1000) NOT NULL DEFAULT '' COMMENT '商家退货地址',
  PRIMARY KEY (`refund_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='订单退款表';


DROP TABLE IF EXISTS `{{prefix}}shop_order_refund_log`;
CREATE TABLE `{{prefix}}shop_order_refund_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_refund_no` varchar(100) NOT NULL DEFAULT '' COMMENT '退款编号',
  `main_type` varchar(255) NOT NULL DEFAULT '操作人类型',
  `main_id` int(11) NOT NULL DEFAULT '0' COMMENT '操作人id',
  `status` int(11) DEFAULT NULL COMMENT '退款状态',
  `type` varchar(255) NOT NULL DEFAULT '',
  `content` varchar(255) DEFAULT NULL COMMENT '日志内容',
  `create_time` int(11) DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='订单退款日志表';


DROP TABLE IF EXISTS `{{prefix}}shop_stat`;
CREATE TABLE `{{prefix}}shop_stat` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `date` varchar(255) NOT NULL DEFAULT '' COMMENT '日期',
  `date_time` int(11) NOT NULL DEFAULT '0' COMMENT '时间戳',
  `order_num` int(11) NOT NULL DEFAULT '0' COMMENT '订单总数',
  `sale_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '销售总额',
  `refund_money` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '退款总额',
  `access_sum` int(11) NOT NULL DEFAULT '0' COMMENT '访问数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci;


DROP TABLE IF EXISTS `{{prefix}}shop_store`;
CREATE TABLE `{{prefix}}shop_store` (
  `store_id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
  `store_name` varchar(255) NOT NULL DEFAULT '' COMMENT '门店名称',
  `store_desc` varchar(3000) NOT NULL DEFAULT '' COMMENT '简介',
  `store_logo` varchar(255) NOT NULL DEFAULT '' COMMENT '门店logo',
  `store_mobile` varchar(255) NOT NULL DEFAULT '' COMMENT '手机号',
  `province_id` int(11) NOT NULL DEFAULT '0' COMMENT '省id',
  `city_id` int(11) NOT NULL DEFAULT '0' COMMENT '市',
  `district_id` int(11) NOT NULL DEFAULT '0' COMMENT '县（区）',
  `address` varchar(255) NOT NULL DEFAULT '' COMMENT '详细地址',
  `full_address` varchar(255) NOT NULL DEFAULT '' COMMENT '完整地址',
  `longitude` varchar(255) NOT NULL DEFAULT '' COMMENT '经度',
  `latitude` varchar(255) NOT NULL DEFAULT '' COMMENT '纬度',
  `trade_time` varchar(255) NOT NULL DEFAULT '' COMMENT '营业时间',
  `create_time` int(11) NOT NULL DEFAULT '0' COMMENT '添加时间',
  `update_time` int(11) NOT NULL DEFAULT '0' COMMENT '更新时间',
  PRIMARY KEY (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='自提门店表';
