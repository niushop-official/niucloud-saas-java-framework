
DROP TABLE IF EXISTS `{{prefix}}sys_upgrade_records`;

CREATE TABLE `{{prefix}}sys_upgrade_records` (
  id INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  upgrade_key VARCHAR(255) NOT NULL DEFAULT '' COMMENT '升级标识',
  app_key VARCHAR(255) NOT NULL DEFAULT '' COMMENT '插件标识',
  name VARCHAR(255) NOT NULL DEFAULT '' COMMENT '升级名称',
  content TEXT DEFAULT NULL COMMENT '升级内容',
  prev_version VARCHAR(255) NOT NULL DEFAULT '' COMMENT '前一版本',
  current_version VARCHAR(255) NOT NULL DEFAULT '' COMMENT '当前版本',
  status VARCHAR(255) NOT NULL DEFAULT '' COMMENT '状态',
  fail_reason LONGTEXT DEFAULT NULL COMMENT '失败原因',
  create_time INT NOT NULL DEFAULT 0 COMMENT '创建时间',
  complete_time INT NOT NULL DEFAULT 0 COMMENT '完成时间',
  PRIMARY KEY (id)
)
ENGINE = INNODB,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_general_ci,
COMMENT = '升级记录表';

DROP TABLE IF EXISTS `{{prefix}}sys_schedule_log`;

CREATE TABLE `{{prefix}}sys_schedule_log` (
  id INT NOT NULL AUTO_INCREMENT COMMENT '执行记录id',
  schedule_id INT NOT NULL DEFAULT 0 COMMENT '任务id',
  addon VARCHAR(255) NOT NULL DEFAULT '' COMMENT '所属插件',
  `key` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '计划任务模板key',
  name VARCHAR(50) NOT NULL DEFAULT '' COMMENT '计划任务名称',
  execute_time INT NOT NULL COMMENT '执行时间',
  execute_result TEXT DEFAULT NULL COMMENT '日志信息',
  status VARCHAR(255) NOT NULL DEFAULT '' COMMENT '执行状态',
  class VARCHAR(255) NOT NULL DEFAULT '',
  job VARCHAR(255) NOT NULL DEFAULT '',
  PRIMARY KEY (id)
)
ENGINE = INNODB,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_general_ci,
COMMENT = '计划任务执行记录';

ALTER TABLE `{{prefix}}sys_schedule` 
  ADD COLUMN name VARCHAR(255) NOT NULL DEFAULT '' COMMENT '任务名称';
  
ALTER TABLE `{{prefix}}sys_menu`
  ADD COLUMN parent_select_key VARCHAR(255) NOT NULL DEFAULT '' COMMENT '上级key';
 
DROP TABLE IF EXISTS `{{prefix}}sys_backup_records`;
  
CREATE TABLE `{{prefix}}sys_backup_records` (
  id INT NOT NULL AUTO_INCREMENT COMMENT '主键id',
  version VARCHAR(255) NOT NULL DEFAULT '' COMMENT '备份版本号',
  backup_key VARCHAR(255) NOT NULL DEFAULT '' COMMENT '备份标识',
  content VARCHAR(255) NOT NULL DEFAULT '' COMMENT '备份内容',
  status VARCHAR(255) NOT NULL DEFAULT '' COMMENT '状态',
  fail_reason LONGTEXT DEFAULT NULL COMMENT '失败原因',
  remark VARCHAR(500) NOT NULL DEFAULT '' COMMENT '备注',
  create_time INT NOT NULL DEFAULT 0 COMMENT '创建时间',
  complete_time INT NOT NULL DEFAULT 0 COMMENT '完成时间',
  PRIMARY KEY (id)
)
ENGINE = INNODB,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_general_ci,
COMMENT = '备份记录表';

ALTER TABLE `{{prefix}}site_addon_init_record` 
  CHANGE COLUMN addon addon VARCHAR(255) NOT NULL DEFAULT '';

ALTER TABLE `{{prefix}}site` 
  ADD COLUMN meta_title VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'Meta 标题';

ALTER TABLE `{{prefix}}site` 
  ADD COLUMN meta_desc VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'Meta 描述';

ALTER TABLE `{{prefix}}site` 
  ADD COLUMN meta_keyword VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'Meta 关键字';
  
ALTER TABLE `{{prefix}}pay_transfer` 
  CHANGE COLUMN extra extra VARCHAR(1000) NOT NULL DEFAULT '' COMMENT '扩展信息';
  
DROP TABLE IF EXISTS `{{prefix}}niu_sms_template`;
  
CREATE TABLE `{{prefix}}niu_sms_template` (
  id INT NOT NULL AUTO_INCREMENT,
  site_id INT DEFAULT 0 COMMENT '站点ID',
  sms_type VARCHAR(255) NOT NULL DEFAULT '' COMMENT '短信服务商类型 niuyun-牛云 aliyun-阿里云 tencent-腾讯',
  username VARCHAR(255) NOT NULL DEFAULT '' COMMENT '子账号名称',
  template_key VARCHAR(255) NOT NULL DEFAULT '' COMMENT '模版key',
  template_id VARCHAR(255) NOT NULL DEFAULT '' COMMENT '模版id',
  template_type VARCHAR(255) NOT NULL DEFAULT '' COMMENT '模版类型',
  template_content VARCHAR(255) NOT NULL DEFAULT '' COMMENT '模版内容',
  param_json VARCHAR(255) NOT NULL DEFAULT '' COMMENT '参数变量',
  status VARCHAR(255) NOT NULL DEFAULT '' COMMENT '上下架状态',
  audit_status VARCHAR(255) NOT NULL DEFAULT '' COMMENT '报备、审核状态',
  audit_msg VARCHAR(255) NOT NULL DEFAULT '' COMMENT '审核结果/拒绝原因',
  report_info TEXT DEFAULT NULL COMMENT '报备、审核信息',
  create_time INT NOT NULL DEFAULT 0 COMMENT '创建时间',
  update_time INT NOT NULL DEFAULT 0 COMMENT '修改时间',
  PRIMARY KEY (id)
)
ENGINE = INNODB,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_general_ci,
COMMENT = '牛云短信模板表';

ALTER TABLE `{{prefix}}member_sign` 
  CHANGE COLUMN day_award day_award VARCHAR(255) NOT NULL DEFAULT '' COMMENT '日签奖励';

ALTER TABLE `{{prefix}}member` 
  ADD COLUMN id_card VARCHAR(30) NOT NULL DEFAULT '' COMMENT '身份证号';
  
ALTER TABLE `{{prefix}}member` 
  ADD COLUMN remark VARCHAR(300) NOT NULL DEFAULT '' COMMENT '备注';

ALTER TABLE `{{prefix}}member` 
 MODIFY id_card VARCHAR(30) NOT NULL DEFAULT '' COMMENT '身份证号' AFTER birthday;
ALTER TABLE `{{prefix}}member` 
 MODIFY remark VARCHAR(300) NOT NULL DEFAULT '' COMMENT '备注' AFTER location;
 
DROP TABLE IF EXISTS `{{prefix}}diy_theme`;

CREATE TABLE `{{prefix}}diy_theme` (
  id INT NOT NULL AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL DEFAULT '' COMMENT '标题',
  type VARCHAR(255) NOT NULL DEFAULT '' COMMENT '插件类型app，addon',
  addon VARCHAR(255) NOT NULL DEFAULT '' COMMENT '所属应用，app：系统，shop：商城、o2o：上门服务',
  mode VARCHAR(255) NOT NULL DEFAULT '' COMMENT '模式，default：默认【跟随系统】，diy：自定义配色',
  theme_type VARCHAR(255) NOT NULL DEFAULT '' COMMENT '配色类型，default：默认，diy：自定义',
  default_theme TEXT DEFAULT NULL COMMENT '当前色调的默认值',
  theme TEXT DEFAULT NULL COMMENT '当前色调',
  new_theme TEXT DEFAULT NULL COMMENT '新增颜色集合',
  is_selected TINYINT NOT NULL DEFAULT 0 COMMENT '已选色调，0：否，1.是',
  create_time INT NOT NULL DEFAULT 0 COMMENT '创建时间',
  update_time INT NOT NULL DEFAULT 0 COMMENT '更新时间',
  site_id INT NOT NULL DEFAULT 0 COMMENT '站点id',
  PRIMARY KEY (id)
)
ENGINE = INNODB,
CHARACTER SET utf8mb4,
COLLATE utf8mb4_general_ci,
COMMENT = '自定义主题配色表';

ALTER TABLE `{{prefix}}diy_page` 
  CHANGE COLUMN template template VARCHAR(255) NOT NULL DEFAULT '' COMMENT '页面模板名称';

ALTER TABLE `{{prefix}}diy_form_submit_config` 
  MODIFY COLUMN tips_type VARCHAR(255) NOT NULL DEFAULT '' COMMENT '提示内容类型，default：默认提示，diy：自定义提示';
