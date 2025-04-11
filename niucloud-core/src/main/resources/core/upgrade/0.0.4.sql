
ALTER TABLE `{{prefix}}pay_transfer` CHANGE COLUMN `transfer_fail_reason` `transfer_fail_reason` VARCHAR(2000) NOT NULL DEFAULT '' COMMENT '失败原因';

ALTER TABLE `{{prefix}}pay_transfer` ADD COLUMN `transfer_payment_code` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '收款码图片';

ALTER TABLE `{{prefix}}pay_transfer` ADD COLUMN `transfer_payee` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '在线转账数据(json)';

ALTER TABLE `{{prefix}}pay_transfer` ADD COLUMN `out_batch_no` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '扩展数据,主要用于记录接收到线上打款的业务数据编号';

ALTER TABLE `{{prefix}}pay_transfer` MODIFY COLUMN `remark` VARCHAR(255) NOT NULL DEFAULT '';

ALTER TABLE `{{prefix}}pay_transfer` MODIFY `transfer_payment_code` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '收款码图片' AFTER `transfer_remark`;

ALTER TABLE `{{prefix}}pay_transfer` ADD COLUMN `package_info` VARCHAR(1000) NOT NULL DEFAULT '' COMMENT '跳转领取页面的package信息';

ALTER TABLE `{{prefix}}pay_transfer` ADD COLUMN `extra` text NOT NULL COMMENT '扩展信息';

ALTER TABLE `{{prefix}}member_cash_out_account` ADD COLUMN `transfer_payment_code` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '收款码';

ALTER TABLE `{{prefix}}member_cash_out` ADD COLUMN `transfer_payee` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '转账收款方(json),主要用于对接在线的打款方式';

ALTER TABLE `{{prefix}}member_cash_out` ADD COLUMN `transfer_payment_code` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '收款码图片';

ALTER TABLE `{{prefix}}member_cash_out` MODIFY `transfer_payee` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '转账收款方(json),主要用于对接在线的打款方式' AFTER `transfer_account`;

ALTER TABLE `{{prefix}}member_cash_out` MODIFY `transfer_payment_code` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '收款码图片' AFTER `transfer_payee`;

ALTER TABLE `{{prefix}}member_sign` MODIFY `day_award` text NOT NULL COMMENT '日签奖励';

ALTER TABLE `{{prefix}}member_sign` MODIFY `continue_award` text NOT NULL COMMENT '连签奖励';

DROP TABLE IF EXISTS `{{prefix}}diy_theme`;
CREATE TABLE `{{prefix}}diy_theme` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `site_id` INT(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `title` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '标题',
  `type` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '插件类型app，addon',
  `addon` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '所属应用，app：系统，shop：商城、o2o：上门服务',
  `color_mark` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '颜色标识',
  `color_name` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '颜色名称',
  `mode` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '模式，default：默认【跟随系统】，diy：自定义配色',
  `value` TEXT DEFAULT NULL COMMENT '配色',
  `diy_value` TEXT DEFAULT NULL COMMENT '自定义配色',
  `create_time` INT(11) NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` INT(11) NOT NULL DEFAULT 0 COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE utf8mb4_general_ci COMMENT='自定义主题配色表';

DROP TABLE IF EXISTS `{{prefix}}pay_transfer_scene`;
CREATE TABLE `{{prefix}}pay_transfer_scene` (
  `id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `site_id` INT(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `type` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '业务类型',
  `scene` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '场景',
  `infos` VARCHAR(2000) NOT NULL DEFAULT '' COMMENT '转账报备背景',
  `create_time` INT(11) NOT NULL DEFAULT 0 COMMENT '创建时间',
  `perception` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '转账收款感知',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '支付转账场景表' ROW_FORMAT = Dynamic;
