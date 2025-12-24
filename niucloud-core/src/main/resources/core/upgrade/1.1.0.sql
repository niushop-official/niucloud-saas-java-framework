
ALTER TABLE `{{prefix}}verify` CHANGE COLUMN `verifier_member_id` `verifier_member_id` INT NOT NULL DEFAULT 0 COMMENT '核销员的会员id';

ALTER TABLE `{{prefix}}verify` ADD COLUMN `is_admin` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '是否后台核销0-否1-是';

ALTER TABLE `{{prefix}}verify` CHANGE `value` `value` LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '核销内容';

ALTER TABLE `{{prefix}}activity_exchange_code` ADD COLUMN type_item_id INT(11) NOT NULL DEFAULT 0 COMMENT '规格id';

ALTER TABLE `{{prefix}}sys_user` ADD COLUMN mobile VARCHAR(20) NOT NULL DEFAULT '' COMMENT '手机号';