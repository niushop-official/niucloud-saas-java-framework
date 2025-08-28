
ALTER TABLE `{{prefix}}shop_store`
  CHANGE COLUMN trade_time trade_time VARCHAR(255) NOT NULL DEFAULT '' COMMENT '营业时间(文本展示使用)';

ALTER TABLE `{{prefix}}shop_store` 
  ADD COLUMN time_week TEXT DEFAULT NULL COMMENT '自定义的营业时间["0","1","2","3","4","5","6"]周日-周六';

ALTER TABLE `{{prefix}}shop_store` 
  ADD COLUMN trade_time_json TEXT DEFAULT NULL COMMENT '营业时间';

ALTER TABLE `{{prefix}}shop_store` 
  ADD COLUMN time_interval INT NOT NULL DEFAULT 0 COMMENT '时段设置（分钟）';
  
ALTER TABLE `{{prefix}}shop_store`
   MODIFY create_time INT NOT NULL DEFAULT 0 COMMENT '添加时间' AFTER time_interval;
   
ALTER TABLE `{{prefix}}shop_store` 
   MODIFY update_time INT NOT NULL DEFAULT 0 COMMENT '更新时间' AFTER create_time;

ALTER TABLE `{{prefix}}shop_point_exchange_order` 
  MODIFY COLUMN site_id INT NOT NULL DEFAULT 0 COMMENT '站点id';

ALTER TABLE `{{prefix}}shop_point_exchange_order` 
  MODIFY COLUMN order_money DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '订单金额';

ALTER TABLE `{{prefix}}shop_order_refund_log`
  CHANGE COLUMN main_type main_type VARCHAR(255) NOT NULL DEFAULT '' COMMENT '操作人类型';
  
ALTER TABLE `{{prefix}}shop_order_refund`
  ADD COLUMN delete_time INT NOT NULL DEFAULT 0 COMMENT '是否删除(针对后台)';
  
ALTER TABLE `{{prefix}}shop_order_goods` 
  ADD COLUMN delete_time INT NOT NULL DEFAULT 0 COMMENT '是否删除(针对后台)';
  
ALTER TABLE `{{prefix}}shop_order_discount` 
  ADD COLUMN member_id INT NOT NULL DEFAULT 0 COMMENT '会员id';
  
ALTER TABLE `{{prefix}}shop_order_discount` 
  ADD COLUMN goods_id INT NOT NULL DEFAULT 0 COMMENT '商品id';
  
ALTER TABLE `{{prefix}}shop_order_discount` 
  ADD COLUMN sku_id INT NOT NULL DEFAULT 0 COMMENT 'sku_id';

ALTER TABLE `{{prefix}}shop_order_delivery` 
  CHANGE COLUMN remark remark VARCHAR(1000) NOT NULL DEFAULT '' COMMENT '备注';
  
ALTER TABLE `{{prefix}}shop_order_delivery` 
  ADD COLUMN third_delivery VARCHAR(50) NOT NULL DEFAULT '' COMMENT '配送方（三方配送）';

ALTER TABLE `{{prefix}}shop_order_delivery` 
 MODIFY remark VARCHAR(1000) NOT NULL DEFAULT '' COMMENT '备注' AFTER third_delivery;
 
ALTER TABLE `{{prefix}}shop_order_delivery` 
 MODIFY create_time INT NOT NULL DEFAULT 0 COMMENT '创建时间' AFTER remark;
 
ALTER TABLE `{{prefix}}shop_order_batch_delivery`
   CHANGE COLUMN type type VARCHAR(255) NOT NULL DEFAULT '' COMMENT '操作类型 批量发货  批量打单 ....';
   
ALTER TABLE `{{prefix}}shop_order_batch_delivery` 
  MODIFY COLUMN create_time INT NOT NULL DEFAULT 0 COMMENT '创建时间';
  
ALTER TABLE `{{prefix}}shop_order` 
  ADD COLUMN buyer_ask_delivery_time VARCHAR(255) NOT NULL DEFAULT '' COMMENT '购买人要求的配送/发货/自提时间（文本）';
  
ALTER TABLE `{{prefix}}shop_order`
    ADD COLUMN relate_order_id INT NOT NULL DEFAULT 0 COMMENT '关联活动来源订单id';
    
ALTER TABLE `{{prefix}}shop_order` 
  ADD COLUMN relate_source VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'seckill 秒杀系统 ';
  
ALTER TABLE `{{prefix}}shop_goods_rank`
MODIFY COLUMN brand_ids VARCHAR(255) NOT NULL DEFAULT '' COMMENT '商品品牌id';
  
ALTER TABLE `{{prefix}}shop_goods_evaluate` 
  MODIFY COLUMN content VARCHAR(3000) NOT NULL DEFAULT '' COMMENT '评价内容';

ALTER TABLE `{{prefix}}shop_goods_brand` 
  CHANGE COLUMN color_json color_json TEXT DEFAULT NULL;

 ALTER TABLE `{{prefix}}shop_goods_brand`
  MODIFY color_json TEXT DEFAULT NULL AFTER delete_time;
  
ALTER TABLE `{{prefix}}shop_goods` 
  DROP COLUMN attr_id;
  
ALTER TABLE `{{prefix}}shop_goods` 
  ADD COLUMN brand_ids VARCHAR(255) NOT NULL DEFAULT '' COMMENT '商品品牌id';

ALTER TABLE `{{prefix}}shop_goods` 
  ADD COLUMN attr_ids TEXT DEFAULT NULL COMMENT '商品参数id';

ALTER TABLE `{{prefix}}shop_goods` 
  ADD COLUMN diy_detail_id INT NOT NULL DEFAULT 0 COMMENT '自定义详情id';

ALTER TABLE `{{prefix}}shop_goods` 
  ADD COLUMN category_ids VARCHAR(255) NOT NULL DEFAULT '' COMMENT '商品分类id';

DROP TABLE IF EXISTS `{{prefix}}shop_discount_goods`;

CREATE TABLE `{{prefix}}shop_discount_goods` (
  discount_goods_id INT NOT NULL AUTO_INCREMENT COMMENT '活动商品id',
  site_id INT NOT NULL DEFAULT 0 COMMENT '站点id',
  discount_id INT NOT NULL DEFAULT 0 COMMENT '活动id',
  goods_id INT NOT NULL DEFAULT 0 COMMENT '商品id',
  sku_id INT NOT NULL DEFAULT 0 COMMENT '商品规格id',
  status VARCHAR(50) NOT NULL DEFAULT '' COMMENT '商品状态',
  type VARCHAR(255) NOT NULL DEFAULT '' COMMENT '折扣类型',
  rate DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '折扣',
  reduce_money DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '减钱',
  discount_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '活动商品价格（展示，搜索）',
  order_money DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '活动累计金额',
  order_num INT NOT NULL DEFAULT 0 COMMENT '活动累计订单数',
  member_num INT NOT NULL DEFAULT 0 COMMENT '活动参与会员数',
  success_num INT NOT NULL DEFAULT 0 COMMENT '活动成功参与会员数',
  is_enabled INT NOT NULL DEFAULT 1 COMMENT '是否参与活动',
  PRIMARY KEY (discount_goods_id)
)
ENGINE = INNODB,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_general_ci,
COMMENT = '限时折扣商品表';

DROP TABLE IF EXISTS `{{prefix}}shop_discount`;

CREATE TABLE `{{prefix}}shop_discount` (
  discount_id INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '活动id',
  site_id INT NOT NULL DEFAULT 0 COMMENT '站点id',
  name VARCHAR(255) NOT NULL DEFAULT '' COMMENT '活动名称',
  remark TEXT DEFAULT NULL COMMENT '活动说明',
  start_time INT NOT NULL DEFAULT 0 COMMENT '活动开始时间',
  end_time INT NOT NULL DEFAULT 0 COMMENT '活动结束时间',
  status VARCHAR(50) NOT NULL DEFAULT '' COMMENT '活动状态',
  order_money DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '活动累计金额',
  order_num INT NOT NULL DEFAULT 0 COMMENT '活动累计订单数',
  member_num INT NOT NULL DEFAULT 0 COMMENT '活动参与会员数',
  success_num INT NOT NULL DEFAULT 0 COMMENT '活动成功参与会员数',
  create_time INT NOT NULL DEFAULT 0 COMMENT '添加时间',
  update_time INT NOT NULL DEFAULT 0 COMMENT '修改时间',
  PRIMARY KEY (discount_id)
)
ENGINE = INNODB,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_general_ci,
COMMENT = '限时折扣表';

ALTER TABLE `{{prefix}}shop_delivery_local_delivery` 
  CHANGE COLUMN delivery_time delivery_time TEXT DEFAULT NULL COMMENT '配送时间段';

ALTER TABLE `{{prefix}}shop_delivery_electronic_sheet` 
  ADD COLUMN interface_type CHAR(20) NOT NULL DEFAULT '' COMMENT '快递公司类型 kdbird:快递鸟 kd100:快递100';

ALTER TABLE `{{prefix}}shop_delivery_electronic_sheet` 
  ADD COLUMN exp_type_name VARCHAR(255) NOT NULL DEFAULT '' COMMENT '物流公司业务类型名称';

ALTER TABLE `{{prefix}}shop_delivery_electronic_sheet` 
  ADD COLUMN temp_id VARCHAR(255) NOT NULL DEFAULT '' COMMENT '主模版:快递100用';

ALTER TABLE `{{prefix}}shop_delivery_electronic_sheet` 
  ADD COLUMN child_temp_id VARCHAR(255) NOT NULL DEFAULT '' COMMENT '子模版:快递100用';

ALTER TABLE `{{prefix}}shop_delivery_electronic_sheet` 
  ADD COLUMN back_temp_id VARCHAR(255) NOT NULL DEFAULT '' COMMENT '回单模版:快递100用';

ALTER TABLE `{{prefix}}shop_delivery_electronic_sheet` 
  ADD COLUMN interface_data TEXT DEFAULT NULL COMMENT '接口参数';

ALTER TABLE `{{prefix}}shop_delivery_company` 
  CHANGE COLUMN express_no express_no VARCHAR(255) NOT NULL DEFAULT '' COMMENT '快递鸟:物流公司编号(用于物流跟踪)';

ALTER TABLE `{{prefix}}shop_delivery_company` 
  CHANGE COLUMN express_no_electronic_sheet express_no_electronic_sheet VARCHAR(255) NOT NULL DEFAULT '' COMMENT '快递鸟:物流公司编号(用于电子面单)';

ALTER TABLE `{{prefix}}shop_delivery_company` 
  ADD COLUMN kd100_express_no VARCHAR(255) NOT NULL DEFAULT '' COMMENT '快递100:物流公司编号(用于物流跟踪)';

ALTER TABLE `{{prefix}}shop_delivery_company` 
  ADD COLUMN kd100_express_no_electronic_sheet VARCHAR(255) NOT NULL DEFAULT '' COMMENT '快递100:物流公司编号(用于电子面单)';
  
DROP TABLE IF EXISTS `{{prefix}}shop_coupon_send_records`;

CREATE TABLE `{{prefix}}shop_coupon_send_records` (
  id INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  site_id INT NOT NULL COMMENT '站点id',
  coupon_id INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '优惠券id',
  send_num INT NOT NULL COMMENT '每位会员发放数量',
  range_type VARCHAR(20) NOT NULL DEFAULT '' COMMENT '发券范围',
  range_param TEXT DEFAULT NULL COMMENT '范围对应参数',
  success_num INT NOT NULL DEFAULT 0 COMMENT '发放成功数',
  status VARCHAR(255) NOT NULL DEFAULT '' COMMENT '状态 wait-待发送 process-发送中 finish-结束',
  member_num INT NOT NULL DEFAULT 0 COMMENT '发放会员数',
  end_time INT NOT NULL DEFAULT 0 COMMENT '发放结束时间',
  admin_uid INT NOT NULL DEFAULT 0 COMMENT '操作人id',
  admin_username VARCHAR(255) NOT NULL DEFAULT '' COMMENT '操作人名称',
  create_time INT NOT NULL DEFAULT 0 COMMENT '创建时间',
  update_time INT NOT NULL DEFAULT 0 COMMENT '更新时间',
  PRIMARY KEY (id)
)
ENGINE = INNODB,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_general_ci,
COMMENT = '优惠券发券记录表';

ALTER TABLE `{{prefix}}shop_coupon` 
  ADD COLUMN give_count INT NOT NULL DEFAULT 0 COMMENT '已发放数量';

ALTER TABLE `{{prefix}}shop_coupon` 
 MODIFY give_count INT NOT NULL DEFAULT 0 COMMENT '已发放数量' AFTER receive_count;
