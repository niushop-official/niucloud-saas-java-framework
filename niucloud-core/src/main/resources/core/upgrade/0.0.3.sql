
ALTER TABLE `{{prefix}}pay` ADD COLUMN `from_main_id` INT(11) NOT NULL DEFAULT 0 COMMENT '发起支付会员id';

ALTER TABLE `{{prefix}}pay` MODIFY `from_main_id` INT(11) NOT NULL DEFAULT 0 COMMENT '发起支付会员id' AFTER `main_id`;
