SET NAMES utf8mb4;

DROP TABLE IF EXISTS `{table_prefix}addon`;
CREATE TABLE `{table_prefix}addon`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(40) NOT NULL DEFAULT '' COMMENT '插件名称',
  `icon` varchar(255) NOT NULL DEFAULT '' COMMENT '插件图标',
  `key` varchar(20) NOT NULL DEFAULT '' COMMENT '插件标识',
  `desc` text NULL COMMENT '插件描述',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态',
  `author` varchar(40) NOT NULL DEFAULT '' COMMENT '作者',
  `version` varchar(20) NOT NULL DEFAULT '' COMMENT '版本号',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '创建时间',
  `install_time` int(11) NOT NULL DEFAULT 0 COMMENT '安装时间',
  `update_time` int(11) NOT NULL DEFAULT 0 COMMENT '更新时间',
  `cover` varchar(255) NOT NULL DEFAULT '' COMMENT '封面',
  `type` varchar(255) NOT NULL DEFAULT 'app' COMMENT '插件类型app，addon',
  `support_app` varchar(255) NOT NULL DEFAULT '' COMMENT '插件支持的应用空表示通用插件',
  `is_star` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否加星',
  `compile` varchar(2000) NOT NULL DEFAULT '' COMMENT '编译端口',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '插件表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}addon_log`;
CREATE TABLE `{table_prefix}addon_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `action` varchar(40) NOT NULL DEFAULT '' COMMENT '操作类型   install 安装 uninstall 卸载 update 更新',
  `key` varchar(20) NOT NULL DEFAULT '' COMMENT '插件标识',
  `from_version` varchar(20) NOT NULL DEFAULT '' COMMENT '升级前的版本号',
  `to_version` varchar(20) NOT NULL DEFAULT '' COMMENT '升级后的版本号',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '插件日志表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}applet_site_version`;
CREATE TABLE `{table_prefix}applet_site_version`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `site_id` int(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `version_id` int(11) NOT NULL DEFAULT 0 COMMENT '版本id',
  `type` varchar(20) NOT NULL DEFAULT '' COMMENT '小程序类型',
  `action` varchar(20) NOT NULL DEFAULT '' COMMENT '操作方式 download 下载  upgrade 更新',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '站点小程序版本表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}applet_version`;
CREATE TABLE `{table_prefix}applet_version`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `config` varchar(255) NOT NULL DEFAULT '' COMMENT '配置信息',
  `type` varchar(20) NOT NULL DEFAULT '' COMMENT '小程序类型',
  `desc` text NULL COMMENT '插件描述',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态  下架  上架',
  `uid` varchar(40) NOT NULL DEFAULT '' COMMENT '发布者',
  `path` varchar(255) NOT NULL DEFAULT '' COMMENT '小程序包地址',
  `version` varchar(20) NOT NULL DEFAULT '' COMMENT '版本号',
  `version_num` varchar(20) NOT NULL DEFAULT '' COMMENT '版本号数字(用于排序)',
  `release_version` varchar(20) NOT NULL DEFAULT '' COMMENT '发布线上版本号',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '创建时间',
  `delete_time` int(11) NOT NULL DEFAULT 0 COMMENT '删除时间',
  `update_time` int(11) NOT NULL DEFAULT 0 COMMENT '更新时间',
  `site_id` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '小程序版本表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}diy_page`;
CREATE TABLE `{table_prefix}diy_page`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int(11) NOT NULL DEFAULT '0' COMMENT '站点id',
  `page_title` varchar(255) NOT NULL DEFAULT '' COMMENT '页面名称（用于后台展示）',
  `title` varchar(255) NOT NULL DEFAULT '' COMMENT '页面标题（用于前台展示）',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '页面标识',
  `type` varchar(255) NOT NULL DEFAULT '' COMMENT '页面模板',
  `template` varchar(255) NOT NULL DEFAULT '' COMMENT '模板名称',
  `mode` varchar(255) NOT NULL DEFAULT 'diy' COMMENT '页面展示模式，diy：自定义，fixed：固定',
  `value` longtext COMMENT '页面数据，json格式',
  `is_default` int(11) NOT NULL DEFAULT '0' COMMENT '是否默认页面，1：是，0：否',
  `is_change` int(11) NOT NULL DEFAULT '0' COMMENT '数据是否发生过变化，1：变化了，2：没有',
  `share` varchar(1000) NOT NULL DEFAULT '' COMMENT '分享内容',
  `visit_count` int(11) NOT NULL DEFAULT '0' COMMENT '访问量',
  `create_time` int(11) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` int(11) NOT NULL DEFAULT '0' COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自定义页面' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}diy_route`;
CREATE TABLE `{table_prefix}diy_route`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `title` varchar(255) NOT NULL DEFAULT '' COMMENT '页面名称',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '页面标识',
  `page` varchar(255) NOT NULL DEFAULT '' COMMENT '页面路径',
  `share` varchar(1000) NOT NULL DEFAULT '' COMMENT '分享内容',
  `is_share` int(11) NOT NULL DEFAULT 0 COMMENT '是否支持分享',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '自定义路由' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}generate_column`;
CREATE TABLE `{table_prefix}generate_column`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `table_id` int(11) NOT NULL DEFAULT 0 COMMENT '表id',
  `column_name` varchar(100) NOT NULL DEFAULT '' COMMENT '字段名称',
  `column_comment` varchar(300) NOT NULL DEFAULT '' COMMENT '字段描述',
  `column_type` varchar(100) NOT NULL DEFAULT '' COMMENT '字段类型',
  `is_required` tinyint(1) NULL DEFAULT 0 COMMENT '是否必填 0-非必填 1-必填',
  `is_pk` tinyint(1) NULL DEFAULT 0 COMMENT '是否为主键 0-不是 1-是',
  `is_insert` tinyint(1) NULL DEFAULT 0 COMMENT '是否为插入字段 0-不是 1-是',
  `is_update` tinyint(1) NULL DEFAULT 0 COMMENT '是否为更新字段 0-不是 1-是',
  `is_lists` tinyint(1) NULL DEFAULT 1 COMMENT '是否为列表字段 0-不是 1-是',
  `is_query` tinyint(1) NULL DEFAULT 1 COMMENT '是否为查询字段 0-不是 1-是',
  `is_search` tinyint(1) NULL DEFAULT 1 COMMENT '是否搜索字段',
  `query_type` varchar(100) NULL DEFAULT '=' COMMENT '查询类型',
  `view_type` varchar(100) NULL DEFAULT 'input' COMMENT '显示类型',
  `dict_type` varchar(255) NULL DEFAULT '' COMMENT '字典类型',
  `addon` varchar(255) NULL DEFAULT '' COMMENT '远程下拉关联应用',
  `model` varchar(255) NULL DEFAULT '' COMMENT '远程下拉关联model',
  `label_key` varchar(255) NULL DEFAULT '' COMMENT '远程下拉标题字段',
  `value_key` varchar(255) NULL DEFAULT '' COMMENT '远程下拉value字段',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` int(11) NOT NULL DEFAULT 0 COMMENT '修改时间',
  `is_delete` tinyint(4) NULL DEFAULT 0 COMMENT '是否为软删除字段 0-不是 1-是',
  `is_order` tinyint(4) NULL DEFAULT 0 COMMENT '是否为排序字段 0-不是 1-是',
  `validate_type` varchar(255) NULL DEFAULT '' COMMENT '验证类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成表字段信息表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}generate_table`;
CREATE TABLE `{table_prefix}generate_table`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `table_name` varchar(255) NOT NULL DEFAULT '' COMMENT '表名',
  `table_content` varchar(255) NOT NULL DEFAULT '' COMMENT '描述前缀',
  `module_name` varchar(255) NOT NULL DEFAULT '' COMMENT '模块名',
  `class_name` varchar(255) NOT NULL DEFAULT '' COMMENT '类名前缀',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '添加时间',
  `edit_type` int(11) NOT NULL DEFAULT 1 COMMENT '编辑方式 1-弹框 2-新页面',
  `addon_name` varchar(255) NOT NULL DEFAULT '' COMMENT '插件名',
  `order_type` int(11) NOT NULL DEFAULT 0 COMMENT '排序方式 0-无排序 1-正序 2-倒序',
  `parent_menu` varchar(255) NOT NULL DEFAULT '' COMMENT '上级菜单',
  `relations` text NULL COMMENT '关联配置',
  `synchronous_number` int(11) NOT NULL DEFAULT 0 COMMENT '同步次数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}jobs`;
CREATE TABLE `{table_prefix}jobs`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `queue` varchar(255) NOT NULL,
  `payload` longtext NOT NULL,
  `attempts` tinyint(4) UNSIGNED NOT NULL DEFAULT 0,
  `reserve_time` int(11) UNSIGNED NULL DEFAULT 0,
  `available_time` int(11) UNSIGNED NULL DEFAULT 0,
  `create_time` int(11) UNSIGNED NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '消息队列任务表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}jobs_failed`;
CREATE TABLE `{table_prefix}jobs_failed`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `connection` text NOT NULL,
  `queue` text NOT NULL,
  `payload` longtext NOT NULL,
  `exception` longtext NOT NULL,
  `fail_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '消息队列任务失败记录表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}member`;
CREATE TABLE `{table_prefix}member`  (
  `member_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `member_no` varchar(255) NOT NULL DEFAULT '' COMMENT '会员编码',
  `pid` int(11) NOT NULL DEFAULT 0 COMMENT '推广会员id',
  `site_id` int(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `username` varchar(255) NOT NULL DEFAULT '' COMMENT '会员用户名',
  `mobile` varchar(20) NOT NULL DEFAULT '' COMMENT '手机号',
  `password` varchar(255) NOT NULL DEFAULT '' COMMENT '会员密码',
  `nickname` varchar(255) NOT NULL DEFAULT '' COMMENT '会员昵称',
  `headimg` varchar(1000) NOT NULL DEFAULT '' COMMENT '会员头像',
  `member_level` int(11) NOT NULL DEFAULT 0 COMMENT '会员等级',
  `member_label` varchar(255) NOT NULL DEFAULT '' COMMENT '会员标签',
  `wx_openid` varchar(255) NOT NULL DEFAULT '' COMMENT '微信用户openid',
  `weapp_openid` varchar(255) NOT NULL DEFAULT '' COMMENT '微信小程序openid',
  `wx_unionid` varchar(255) NOT NULL DEFAULT '' COMMENT '微信unionid',
  `ali_openid` varchar(255) NOT NULL DEFAULT '' COMMENT '支付宝账户id',
  `douyin_openid` varchar(255) NOT NULL DEFAULT '' COMMENT '抖音小程序openid',
  `register_channel` varchar(255) NOT NULL DEFAULT 'H5' COMMENT '注册来源',
  `register_type` varchar(255) NOT NULL DEFAULT '' COMMENT '注册方式',
  `login_ip` varchar(255) NOT NULL DEFAULT '' COMMENT '当前登录ip',
  `login_type` varchar(255) NOT NULL DEFAULT 'h5' COMMENT '当前登录的操作终端类型',
  `login_channel` varchar(255) NOT NULL DEFAULT '',
  `login_count` int(11) NOT NULL DEFAULT 0 COMMENT '登录次数',
  `login_time` int(11) NOT NULL DEFAULT 0 COMMENT '当前登录时间',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '注册时间',
  `last_visit_time` int(11) NOT NULL DEFAULT 0 COMMENT '最后访问时间',
  `last_consum_time` int(11) NOT NULL DEFAULT 0 COMMENT '最后消费时间',
  `sex` tinyint(4) NOT NULL DEFAULT 0 COMMENT '性别 0保密 1男 2女',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '用户状态  用户状态默认为1',
  `birthday` varchar(20) NOT NULL DEFAULT '' COMMENT '出生日期',
  `point` int(11) NOT NULL DEFAULT 0 COMMENT '可用积分',
  `point_get` int(11) NOT NULL DEFAULT 0 COMMENT '累计获取积分',
  `balance` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '可用余额',
  `balance_get` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '累计获取余额',
  `money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '可用余额（可提现）',
  `money_get` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '累计获取余额（可提现）',
  `money_cash_outing` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '提现中余额（可提现）',
  `growth` int(11) NOT NULL DEFAULT 0 COMMENT '成长值',
  `growth_get` int(11) NOT NULL DEFAULT 0 COMMENT '累计获得成长值',
  `commission` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '当前佣金',
  `commission_get` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '佣金获取',
  `commission_cash_outing` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '提现中佣金',
  `is_member` tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否是会员',
  `member_time` int(11) NOT NULL DEFAULT 0 COMMENT '成为会员时间',
  `is_del` tinyint(4) NOT NULL DEFAULT 0 COMMENT '0正常  1已删除',
  `province_id` int(11) NOT NULL DEFAULT 0 COMMENT '省id',
  `city_id` int(11) NOT NULL DEFAULT 0 COMMENT '市id',
  `district_id` int(11) NOT NULL DEFAULT 0 COMMENT '区县id',
  `address` varchar(255) NOT NULL DEFAULT '' COMMENT '详细地址',
  `location` varchar(255) NOT NULL DEFAULT '' COMMENT '定位地址',
  `delete_time` int(11) NOT NULL DEFAULT 0 COMMENT '删除时间',
  `update_time` int(11) NOT NULL DEFAULT 0 COMMENT '修改时间',
  PRIMARY KEY (`member_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会员表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}member_account_log`;
CREATE TABLE `{table_prefix}member_account_log`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `member_id` int(11) NOT NULL DEFAULT 0 COMMENT '用户id',
  `site_id` int(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `account_type` varchar(255) NOT NULL DEFAULT 'point' COMMENT '账户类型',
  `account_data` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '账户数据',
  `account_sum` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '变动后的账户余额',
  `from_type` varchar(255) NOT NULL DEFAULT '' COMMENT '来源类型',
  `related_id` varchar(50) NOT NULL DEFAULT '' COMMENT '关联Id',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '创建时间',
  `memo` varchar(255) NOT NULL DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会员账单表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}member_address`;
CREATE TABLE `{table_prefix}member_address` (
   `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
   `member_id` int NOT NULL DEFAULT 0 COMMENT '会员id',
   `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
   `name` varchar(255) NOT NULL DEFAULT '' COMMENT '用户姓名',
   `mobile` varchar(255) NOT NULL DEFAULT '' COMMENT '手机',
   `province_id` int NOT NULL DEFAULT 0 COMMENT '省id',
   `city_id` int NOT NULL DEFAULT 0 COMMENT '市id',
   `district_id` int NOT NULL DEFAULT 0 COMMENT '区县id',
   `address` varchar(255) NOT NULL DEFAULT '' COMMENT '地址信息',
   `address_name` varchar(255) NOT NULL DEFAULT '',
   `full_address` varchar(255) NOT NULL DEFAULT '' COMMENT '详细地址信息',
   `lng` varchar(255) NOT NULL DEFAULT '' COMMENT '经度',
   `lat` varchar(255) NOT NULL DEFAULT '' COMMENT '纬度',
   `is_default` tinyint NOT NULL DEFAULT 0 COMMENT '是否是默认地址',
   PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会员收货地址' ROW_FORMAT = Dynamic;

ALTER TABLE `member_address`ADD INDEX IDX_member_address (member_id);


DROP TABLE IF EXISTS `{table_prefix}member_cash_out`;
CREATE TABLE `{table_prefix}member_cash_out`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `cash_out_no` varchar(50) NOT NULL DEFAULT '' COMMENT '提现交易号',
  `member_id` int(11) NOT NULL DEFAULT 0 COMMENT '会员id',
  `account_type` varchar(255) NOT NULL DEFAULT 'money' COMMENT '提现账户类型',
  `transfer_type` varchar(20) NOT NULL DEFAULT '0' COMMENT '转账提现类型',
  `transfer_realname` varchar(50) NOT NULL DEFAULT '' COMMENT '联系人名称',
  `transfer_mobile` varchar(11) NOT NULL DEFAULT '' COMMENT '手机号',
  `transfer_bank` varchar(255) NOT NULL DEFAULT '' COMMENT '银行名称',
  `transfer_account` varchar(255) NOT NULL DEFAULT '' COMMENT '收款账号',
  `transfer_fail_reason` varchar(255) NOT NULL DEFAULT '' COMMENT '失败原因',
  `transfer_status` varchar(20) NOT NULL DEFAULT '' COMMENT '转账状态',
  `transfer_time` int(11) NOT NULL DEFAULT 0 COMMENT '转账时间',
  `apply_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '提现申请金额',
  `rate` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '提现手续费比率',
  `service_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '提现手续费',
  `money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '提现到账金额',
  `audit_time` int(11) NOT NULL DEFAULT 0 COMMENT '审核时间',
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '状态1待审核2.待转账3已转账 -1拒绝 -2 已取消',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '申请时间',
  `refuse_reason` varchar(255) NOT NULL DEFAULT '' COMMENT '拒绝理由',
  `update_time` int(11) NOT NULL DEFAULT 0,
  `transfer_no` varchar(50) NOT NULL DEFAULT '' COMMENT '转账单号',
  `cancel_time` int(11) NOT NULL DEFAULT 0 COMMENT '取消时间',
  `final_transfer_type` varchar(255) NOT NULL DEFAULT '' COMMENT '转账方式',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会员提现表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}member_cash_out_account`;
CREATE TABLE `{table_prefix}member_cash_out_account`  (
  `account_id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `member_id` int(11) NOT NULL DEFAULT 0 COMMENT '会员id',
  `account_type` varchar(255) NOT NULL DEFAULT '' COMMENT '账户类型',
  `bank_name` varchar(255) NOT NULL DEFAULT '' COMMENT '银行名称',
  `realname` varchar(255) NOT NULL DEFAULT '' COMMENT '真实名称',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` int(11) NOT NULL DEFAULT 0 COMMENT '修改时间',
  `account_no` varchar(255) NOT NULL DEFAULT '' COMMENT '提现账户',
  PRIMARY KEY (`account_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会员提现账户' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}member_label`;
CREATE TABLE `{table_prefix}member_label`  (
  `label_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '标签id',
  `site_id` int(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `label_name` varchar(50) NOT NULL DEFAULT '' COMMENT '标签名称',
  `memo` varchar(1000) NOT NULL DEFAULT '' COMMENT '备注',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '添加时间',
  `update_time` int(11) NOT NULL DEFAULT 0 COMMENT '更新时间',
  PRIMARY KEY (`label_id`) USING BTREE,
  INDEX `label_id`(`label_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会员标签' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}member_level`;
CREATE TABLE `{table_prefix}member_level`  (
  `level_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '会员等级',
  `site_id` int(11) NOT NULL DEFAULT '0' COMMENT '站点id',
  `level_name` varchar(50) NOT NULL DEFAULT '' COMMENT '等级名称',
  `growth` int(11) NOT NULL DEFAULT '0' COMMENT '所需成长值',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `status` int(11) NOT NULL DEFAULT '1' COMMENT '状态 0已禁用1已启用',
  `create_time` int(11) NOT NULL DEFAULT '0' COMMENT '添加时间',
  `update_time` int(11) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `level_benefits` text COMMENT '等级权益',
  `level_gifts` text COMMENT '等级礼包',
  PRIMARY KEY (`level_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会员等级' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}member_sign`;
CREATE TABLE `{table_prefix}member_sign` (
  `sign_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `site_id` int(11) NOT NULL DEFAULT '0' COMMENT '站点id',
  `member_id` int(11) NOT NULL DEFAULT '0' COMMENT '会员id',
  `days` int(11) NOT NULL DEFAULT '0' COMMENT '连续签到天数',
  `day_award` varchar(255) NOT NULL DEFAULT '' COMMENT '日签奖励',
  `continue_award` varchar(255) NOT NULL DEFAULT '' COMMENT '连签奖励',
  `continue_tag` varchar(30) NOT NULL DEFAULT '' COMMENT '连签奖励标识',
  `create_time` int(11) NOT NULL DEFAULT '0' COMMENT '签到时间',
  `start_time` int(11) NOT NULL DEFAULT '0' COMMENT '签到周期开始时间',
  `is_sign` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否签到（0未签到 1已签到）',
  PRIMARY KEY (`sign_id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会员签到表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}pay`;
CREATE TABLE `{table_prefix}pay`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `site_id` int(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `main_id` int(11) NOT NULL DEFAULT 0 COMMENT '支付会员id',
  `out_trade_no` varchar(255) NOT NULL DEFAULT '' COMMENT '支付流水号',
  `trade_type` varchar(255) NOT NULL DEFAULT '' COMMENT '业务类型',
  `trade_id` int(11) NOT NULL DEFAULT 0 COMMENT '业务id',
  `trade_no` varchar(255) NOT NULL DEFAULT '' COMMENT '交易单号',
  `body` varchar(1000) NOT NULL DEFAULT '' COMMENT '支付主体',
  `money` decimal(10, 2) NOT NULL COMMENT '支付金额',
  `voucher` varchar(255) NOT NULL DEFAULT '' COMMENT '支付票据',
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '支付状态（0.待支付 1. 支付中 2. 已支付 -1已取消）',
  `json` varchar(255) NOT NULL DEFAULT '' COMMENT '支付扩展用支付信息',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '创建时间',
  `pay_time` int(11) NOT NULL DEFAULT 0 COMMENT '支付时间',
  `cancel_time` int(11) NOT NULL DEFAULT 0 COMMENT '关闭时间',
  `type` varchar(255) NOT NULL DEFAULT '' COMMENT '支付方式',
  `mch_id` varchar(50) NOT NULL DEFAULT '' COMMENT '商户收款账号',
  `main_type` varchar(255) NOT NULL DEFAULT '',
  `channel` varchar(50) NOT NULL DEFAULT '' COMMENT '支付渠道',
  `fail_reason` varchar(255) NOT NULL DEFAULT '' COMMENT '失败原因',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '支付记录表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}pay_channel`;
CREATE TABLE `{table_prefix}pay_channel`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `site_id` int(11) NOT NULL DEFAULT 1 COMMENT '站点id',
  `type` varchar(255) NOT NULL DEFAULT '' COMMENT '支付类型',
  `channel` varchar(255) NOT NULL DEFAULT '' COMMENT '支付渠道',
  `config` text NOT NULL COMMENT '支付配置',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` int(11) NOT NULL DEFAULT 0 COMMENT '修改时间',
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '是否启用',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '支付渠道配置表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}pay_refund`;
CREATE TABLE `{table_prefix}pay_refund`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `site_id` int(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `refund_no` varchar(255) NOT NULL DEFAULT '' COMMENT '退款单号',
  `out_trade_no` varchar(255) NOT NULL DEFAULT '' COMMENT '支付流水号',
  `type` varchar(255) NOT NULL DEFAULT '' COMMENT '支付方式',
  `channel` varchar(50) NOT NULL DEFAULT '' COMMENT '支付渠道',
  `money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '支付金额',
  `reason` varchar(255) NOT NULL DEFAULT '' COMMENT '退款原因',
  `status` varchar(255) NOT NULL DEFAULT '0' COMMENT '支付状态（0.待退款 1. 退款中 2. 已退款 -1已关闭）',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '创建时间',
  `refund_time` int(11) NOT NULL DEFAULT 0 COMMENT '支付时间',
  `close_time` int(11) NOT NULL DEFAULT 0 COMMENT '关闭时间',
  `fail_reason` varchar(255) NOT NULL DEFAULT '' COMMENT '失败原因',
  `voucher` varchar(255) NOT NULL DEFAULT '' COMMENT '支付凭证',
  `trade_type` varchar(255) NOT NULL DEFAULT '' COMMENT '业务类型',
  `trade_id` varchar(50) NOT NULL DEFAULT '' COMMENT '业务关联id',
  `refund_type` varchar(255) NOT NULL DEFAULT '' COMMENT '退款方式',
  `main_type` varchar(255) NOT NULL DEFAULT '' COMMENT '操作人类型',
  `main_id` int NOT NULL DEFAULT 0 COMMENT '操作人',
  `pay_refund_no` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '外部支付方式的退款单号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '退款记录表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}pay_transfer`;
CREATE TABLE `{table_prefix}pay_transfer`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `trade_type` varchar(255) NOT NULL DEFAULT '' COMMENT '业务类型',
  `transfer_no` varchar(50) NOT NULL DEFAULT '' COMMENT '转账单号',
  `main_id` int(11) NOT NULL DEFAULT 0 COMMENT '会员id',
  `main_type` varchar(255) NOT NULL DEFAULT '' COMMENT '主体类型',
  `transfer_type` varchar(20) NOT NULL DEFAULT '' COMMENT '转账类型',
  `transfer_realname` varchar(50) NOT NULL DEFAULT '' COMMENT '联系人名称',
  `transfer_mobile` varchar(11) NOT NULL DEFAULT '' COMMENT '手机号',
  `transfer_bank` varchar(255) NOT NULL DEFAULT '' COMMENT '银行名称',
  `transfer_account` varchar(255) NOT NULL DEFAULT '' COMMENT '收款账号',
  `transfer_voucher` varchar(255) NOT NULL DEFAULT '' COMMENT '凭证',
  `transfer_remark` varchar(255) NOT NULL DEFAULT '' COMMENT '凭证说明',
  `transfer_fail_reason` varchar(255) NOT NULL DEFAULT '' COMMENT '失败原因',
  `transfer_status` varchar(20) NOT NULL DEFAULT '' COMMENT '转账状态',
  `money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '转账金额',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '申请时间',
  `transfer_time` int(11) NOT NULL DEFAULT 0 COMMENT '转账时间',
  `update_time` int(11) NOT NULL DEFAULT 0,
  `openid` varchar(50) NOT NULL DEFAULT '',
  `remark` varchar(255) NOT NULL,
  `batch_id` varchar(500) NOT NULL DEFAULT '' COMMENT '转账批次id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '转账表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}recharge_order`;
CREATE TABLE `{table_prefix}recharge_order`  (
  `order_id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `order_no` varchar(50) NOT NULL DEFAULT '' COMMENT '订单编号',
  `order_from` varchar(55) NOT NULL DEFAULT '' COMMENT '订单来源',
  `order_type` varchar(50) NOT NULL DEFAULT '' COMMENT '订单类型',
  `out_trade_no` varchar(50) NOT NULL DEFAULT '' COMMENT '支付流水号',
  `order_status` int(11) NOT NULL DEFAULT 0 COMMENT '订单状态',
  `refund_status` int(11) NOT NULL DEFAULT 0 COMMENT '退款状态',
  `member_id` int(11) NOT NULL DEFAULT 0 COMMENT '会员id',
  `ip` varchar(20) NOT NULL DEFAULT '' COMMENT '会员ip',
  `member_message` varchar(50) NOT NULL DEFAULT '' COMMENT '会员留言信息',
  `order_item_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '订单项目金额',
  `order_discount_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '订单优惠金额',
  `order_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '订单金额',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '创建时间',
  `pay_time` int(11) NOT NULL DEFAULT 0 COMMENT '订单支付时间',
  `close_time` int(11) NOT NULL DEFAULT 0 COMMENT '订单关闭时间',
  `is_delete` int(11) NOT NULL DEFAULT 0 COMMENT '是否删除(针对后台)',
  `is_enable_refund` int(11) NOT NULL DEFAULT 0 COMMENT '是否允许退款',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '商家留言',
  `invoice_id` int(11) NOT NULL DEFAULT 0 COMMENT '发票id，0表示不开发票',
  `close_reason` varchar(255) NOT NULL DEFAULT '' COMMENT '关闭原因',
  PRIMARY KEY (`order_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}recharge_order_item`;
CREATE TABLE `{table_prefix}recharge_order_item`  (
  `order_item_id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL DEFAULT 0 COMMENT '订单id',
  `site_id` int(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `member_id` int(11) NOT NULL DEFAULT 0 COMMENT '购买会员id',
  `item_id` int(11) NOT NULL DEFAULT 0 COMMENT '项目id',
  `item_type` varchar(255) NOT NULL DEFAULT '' COMMENT '项目类型',
  `item_name` varchar(400) NOT NULL DEFAULT '' COMMENT '项目名称',
  `item_image` varchar(2000) NOT NULL DEFAULT '' COMMENT '项目图片',
  `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '项目单价',
  `num` decimal(10, 3) NOT NULL DEFAULT 0.000 COMMENT '购买数量',
  `item_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '项目总价',
  `is_refund` int(11) NOT NULL DEFAULT 0 COMMENT '是否退款',
  `refund_no` varchar(255) NOT NULL DEFAULT '' COMMENT '退款编号',
  `refund_status` int(11) NOT NULL DEFAULT 0 COMMENT '退款状态',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '创建时间',
  PRIMARY KEY (`order_item_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单商品表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}recharge_order_item_refund`;
CREATE TABLE `{table_prefix}recharge_order_item_refund`  (
  `refund_id` int(11) NOT NULL AUTO_INCREMENT,
  `order_item_id` int(11) NOT NULL DEFAULT 0 COMMENT '订单id',
  `order_id` int(11) NOT NULL DEFAULT 0 COMMENT '订单id',
  `order_no` varchar(255) NOT NULL DEFAULT '' COMMENT '订单编号',
  `refund_no` varchar(255) NOT NULL DEFAULT '0' COMMENT '退款单号',
  `site_id` int(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `member_id` int(11) NOT NULL DEFAULT 0 COMMENT '会员id',
  `num` decimal(10, 3) NOT NULL DEFAULT 0.000 COMMENT '退货数量',
  `money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '总退款',
  `status` int(11) NOT NULL DEFAULT 0 COMMENT '退款状态',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '创建时间',
  `audit_time` int(11) NOT NULL DEFAULT 0 COMMENT '审核时间',
  `transfer_time` int(11) NOT NULL DEFAULT 0 COMMENT '转账时间',
  `item_type` varchar(255) NOT NULL DEFAULT '' COMMENT '项目类型',
  PRIMARY KEY (`refund_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单退款表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}recharge_order_log`;
CREATE TABLE `{table_prefix}recharge_order_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL DEFAULT 0 COMMENT '订单id',
  `site_id` int(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `action` varchar(255) NOT NULL DEFAULT '' COMMENT '操作内容',
  `uid` int(11) NOT NULL DEFAULT 0 COMMENT '操作人id',
  `nick_name` varchar(50) NOT NULL DEFAULT '' COMMENT '操作人名称',
  `order_status` int(11) NOT NULL DEFAULT 0 COMMENT '订单状态，操作后',
  `action_way` bigint(20) NOT NULL DEFAULT 2 COMMENT '操作类型1买家2卖家 3 系统任务',
  `order_status_name` varchar(255) NOT NULL DEFAULT '' COMMENT '订单状态名称，操作后',
  `action_time` int(11) NOT NULL DEFAULT 0 COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单操作记录表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}site`;
CREATE TABLE `{table_prefix}site`  (
  `site_id` int(11) NOT NULL AUTO_INCREMENT,
  `site_name` varchar(50) NOT NULL DEFAULT '' COMMENT '站点名称',
  `group_id` int(11) NOT NULL DEFAULT 0 COMMENT '分组ID(0:不限制)',
  `keywords` varchar(255) NOT NULL DEFAULT '' COMMENT '关键字',
  `app_type` varchar(50) NOT NULL DEFAULT 'admin' COMMENT '站点类型',
  `logo` varchar(255) NOT NULL DEFAULT '' COMMENT '站点logo',
  `desc` varchar(255) NOT NULL DEFAULT '' COMMENT '简介',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态 1-正常 0-体验期 2-已到期',
  `latitude` varchar(255) NOT NULL DEFAULT '' COMMENT '纬度',
  `longitude` varchar(255) NOT NULL DEFAULT '' COMMENT '经度',
  `province_id` int(11) NOT NULL DEFAULT 0 COMMENT '省',
  `city_id` int(11) NOT NULL DEFAULT 0 COMMENT '市',
  `district_id` int(11) NOT NULL DEFAULT 0 COMMENT '区',
  `address` varchar(255) NOT NULL DEFAULT '' COMMENT '详细地址',
  `full_address` varchar(255) NOT NULL DEFAULT '' COMMENT '完整地址',
  `phone` varchar(255) NOT NULL DEFAULT '' COMMENT '客服电话',
  `business_hours` varchar(255) NOT NULL DEFAULT '' COMMENT '营业时间',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '创建时间',
  `expire_time` bigint(20) NOT NULL DEFAULT 0 COMMENT '到期时间（如果是0 无限期）',
  `front_end_name` varchar(50) NOT NULL DEFAULT '' COMMENT '前台名称',
  `front_end_logo` varchar(255) NOT NULL DEFAULT '' COMMENT '前台logo',
  `front_end_icon` varchar(255) NOT NULL DEFAULT '' COMMENT '前台icon',
  `icon` varchar(255) NOT NULL DEFAULT '' COMMENT '网站图标',
  `member_no` varchar(255) NOT NULL DEFAULT '0' COMMENT '最大会员码值',
  `app` text NOT NULL COMMENT '站点应用',
  `addons` text NOT NULL COMMENT '站点包含的插件',
  `initalled_addon` text DEFAULT NULL COMMENT '站点已执行初始化方法的插件',
  `site_domain` varchar(255) NOT NULL DEFAULT '' COMMENT '站点域名',
  PRIMARY KEY (`site_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10000 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '站点表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}site_account_log`;
CREATE TABLE `{table_prefix}site_account_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `site_id` int(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `type` varchar(255) NOT NULL DEFAULT 'pay' COMMENT '账单类型pay,refund,transfer',
  `money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '交易金额',
  `trade_no` varchar(255) NOT NULL DEFAULT '' COMMENT '对应类型交易单号',
  `create_time` varchar(255) NOT NULL DEFAULT '0' COMMENT '添加时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '站点账单记录' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}site_group`;
CREATE TABLE `{table_prefix}site_group`  (
  `group_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '分组ID',
  `group_name` varchar(255) NOT NULL DEFAULT '' COMMENT '分组名称',
  `group_desc` text NULL COMMENT '分组介绍',
  `app` text NOT NULL COMMENT '应用',
  `addon` text NOT NULL COMMENT '插件',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '添加时间',
  `update_time` int(11) NOT NULL DEFAULT 0 COMMENT '更新时间',
  PRIMARY KEY (`group_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '店铺分组（分组权限）' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}stat_hour`;
CREATE TABLE `{table_prefix}stat_hour` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `site_id` int(11) NOT NULL DEFAULT '0' COMMENT '站点id',
  `addon` varchar(255) NOT NULL DEFAULT '' COMMENT '插件',
  `field` varchar(255) NOT NULL DEFAULT '' COMMENT '统计字段',
  `field_total` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '总计',
  `year` int(11) NOT NULL DEFAULT '0' COMMENT '年',
  `month` int(11) NOT NULL DEFAULT '0' COMMENT '月',
  `day` int(11) NOT NULL DEFAULT '0' COMMENT '天',
  `start_time` int(11) NOT NULL DEFAULT '0' COMMENT '当日开始时间戳',
  `last_time` int(11) NOT NULL DEFAULT '0' COMMENT '最后执行时间',
  `hour_0` decimal(10,2) NOT NULL DEFAULT '0.00',
  `hour_1` decimal(10,2) NOT NULL DEFAULT '0.00',
  `hour_2` decimal(10,2) NOT NULL DEFAULT '0.00',
  `hour_3` decimal(10,2) NOT NULL DEFAULT '0.00',
  `hour_4` decimal(10,2) NOT NULL DEFAULT '0.00',
  `hour_5` decimal(10,2) NOT NULL DEFAULT '0.00',
  `hour_6` decimal(10,2) NOT NULL DEFAULT '0.00',
  `hour_7` decimal(10,2) NOT NULL DEFAULT '0.00',
  `hour_8` decimal(10,2) NOT NULL DEFAULT '0.00',
  `hour_9` decimal(10,2) NOT NULL DEFAULT '0.00',
  `hour_10` decimal(10,2) NOT NULL DEFAULT '0.00',
  `hour_11` decimal(10,2) NOT NULL DEFAULT '0.00',
  `hour_12` decimal(10,2) NOT NULL DEFAULT '0.00',
  `hour_13` decimal(10,2) NOT NULL DEFAULT '0.00',
  `hour_14` decimal(10,2) NOT NULL DEFAULT '0.00',
  `hour_15` decimal(10,2) NOT NULL DEFAULT '0.00',
  `hour_16` decimal(10,2) NOT NULL DEFAULT '0.00',
  `hour_17` decimal(10,2) NOT NULL DEFAULT '0.00',
  `hour_18` decimal(10,2) NOT NULL DEFAULT '0.00',
  `hour_19` decimal(10,2) NOT NULL DEFAULT '0.00',
  `hour_20` decimal(10,2) NOT NULL DEFAULT '0.00',
  `hour_21` decimal(10,2) NOT NULL DEFAULT '0.00',
  `hour_22` decimal(10,2) NOT NULL DEFAULT '0.00',
  `hour_23` decimal(10,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '小时统计表' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `{table_prefix}sys_agreement`;
CREATE TABLE `{table_prefix}sys_agreement`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `site_id` int(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `agreement_key` varchar(255) NOT NULL DEFAULT '' COMMENT '协议关键字',
  `title` varchar(255) NOT NULL DEFAULT '' COMMENT '协议标题',
  `content` text NULL COMMENT '协议内容',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '添加时间',
  `update_time` int(11) NOT NULL DEFAULT 0 COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '协议表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}sys_area`;
CREATE TABLE `{table_prefix}sys_area`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `pid` int(11) NOT NULL DEFAULT 0 COMMENT '父级',
  `name` varchar(50) NOT NULL DEFAULT '' COMMENT '名称',
  `shortname` varchar(30) NOT NULL DEFAULT '' COMMENT '简称',
  `longitude` varchar(30) NOT NULL DEFAULT '' COMMENT '经度',
  `latitude` varchar(30) NOT NULL DEFAULT '' COMMENT '纬度',
  `level` smallint(6) NOT NULL DEFAULT 0 COMMENT '级别',
  `sort` mediumint(9) NOT NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态1有效',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '地址表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}sys_attachment`;
CREATE TABLE `{table_prefix}sys_attachment`  (
  `att_id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `name` varchar(100) NOT NULL DEFAULT '' COMMENT '附件名称',
  `real_name` varchar(255) NOT NULL DEFAULT '' COMMENT '原始文件名',
  `path` varchar(255) NOT NULL DEFAULT '' COMMENT '完整地址',
  `dir` varchar(200) NOT NULL DEFAULT '' COMMENT '附件路径',
  `att_size` char(30) NOT NULL DEFAULT '' COMMENT '附件大小',
  `att_type` char(30) NOT NULL DEFAULT '' COMMENT '附件类型image,video',
  `storage_type` varchar(20) NOT NULL DEFAULT '' COMMENT '图片上传类型 local本地  aliyun  阿里云oss  qiniu  七牛 ....',
  `cate_id` int(11) NOT NULL DEFAULT 0 COMMENT '相关分类',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '上传时间',
  `update_time` int(11) NOT NULL DEFAULT 0,
  `url` varchar(255) NOT NULL DEFAULT '' COMMENT '网络地址',
  PRIMARY KEY (`att_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '附件管理表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}sys_attachment_category`;
CREATE TABLE `{table_prefix}sys_attachment_category`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `pid` int(11) NOT NULL DEFAULT 0 COMMENT '父级ID',
  `type` varchar(50) NOT NULL DEFAULT '' COMMENT '文件管理类型（image,video）',
  `name` varchar(50) NOT NULL DEFAULT '' COMMENT '分类名称',
  `enname` varchar(50) NOT NULL DEFAULT '' COMMENT '分类目录',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `id`(`id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '附件分类表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}sys_config`;
CREATE TABLE `{table_prefix}sys_config`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `site_id` int(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `config_key` varchar(255) NOT NULL DEFAULT '' COMMENT '配置项关键字',
  `value` text NULL COMMENT '配置值json',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否启用 1启用 0不启用',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` int(11) NOT NULL DEFAULT 0 COMMENT '修改时间',
  `addon` varchar(255) NOT NULL DEFAULT '' COMMENT '所属插件',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}sys_cron_task`;
CREATE TABLE `{table_prefix}sys_cron_task`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int(11) NOT NULL DEFAULT 0,
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '任务状态',
  `count` int(11) NOT NULL DEFAULT 0 COMMENT '执行次数',
  `title` char(50) NOT NULL DEFAULT '' COMMENT '任务名称',
  `type` varchar(255) NOT NULL DEFAULT '' COMMENT '任务模式  cron  定时任务  crond 周期任务',
  `crond_type` char(200) NOT NULL DEFAULT '' COMMENT '任务周期',
  `crond_length` int(11) NOT NULL DEFAULT 0 COMMENT '任务周期',
  `task` varchar(500) NOT NULL DEFAULT '' COMMENT '任务命令',
  `data` longtext NULL COMMENT '附加参数',
  `status_desc` varchar(1000) NOT NULL DEFAULT '' COMMENT '上次执行结果',
  `last_time` int(11) NOT NULL DEFAULT 0 COMMENT '最后执行时间',
  `next_time` int(11) NOT NULL DEFAULT 0 COMMENT '下次执行时间',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '创建时间',
  `delete_time` int(11) NOT NULL DEFAULT 0 COMMENT '删除时间',
  `update_time` int(11) NOT NULL DEFAULT 0 COMMENT '更新时间',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = ' 系统任务' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}sys_dict`;
CREATE TABLE `{table_prefix}sys_dict`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(50) NOT NULL DEFAULT '' COMMENT '字典名称',
  `key` varchar(100) NOT NULL DEFAULT '' COMMENT '字典关键词',
  `dictionary` text NOT NULL COMMENT '字典数据',
  `memo` varchar(255) NOT NULL DEFAULT '',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` int(11) NOT NULL DEFAULT 0 COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '数据字典表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}sys_export`;
CREATE TABLE `{table_prefix}sys_export` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `site_id` int(11) NOT NULL DEFAULT '0' COMMENT '站点ID',
  `export_key` varchar(255) NOT NULL DEFAULT '' COMMENT '主题关键字',
  `export_num` int(11) NOT NULL DEFAULT '0' COMMENT '导出数据数量',
  `file_path` varchar(255) NOT NULL DEFAULT '' COMMENT '文件存储路径',
  `file_size` varchar(255) NOT NULL DEFAULT '' COMMENT '文件大小',
  `export_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '导出状态',
  `fail_reason` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '失败原因',
  `create_time` int(11) NOT NULL DEFAULT '0' COMMENT '导出时间',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '导出报表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}verifier`;
CREATE TABLE `{table_prefix}verifier` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int(11) NOT NULL DEFAULT '0' COMMENT '站点id',
  `member_id` int(11) NOT NULL DEFAULT '0' COMMENT '会员id',
  `uid` int(11) NOT NULL DEFAULT '0' COMMENT '用户id',
  `create_time` int(11) NOT NULL DEFAULT '0' COMMENT '添加时间',
  `verify_type` varchar(255) NOT NULL DEFAULT '' COMMENT '核销类型',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '核销员表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}verify`;
CREATE TABLE `{table_prefix}verify` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `site_id` int(11) NOT NULL DEFAULT '0' COMMENT '站点id',
  `code` varchar(255) NOT NULL DEFAULT '' COMMENT '核销码',
  `data` varchar(255) NOT NULL DEFAULT '' COMMENT '核销参数',
  `type` varchar(30) NOT NULL DEFAULT '' COMMENT '核销类型',
  `create_time` int(11) NOT NULL DEFAULT '0' COMMENT '核销时间',
  `verifier_member_id` int(11) NOT NULL DEFAULT '0' COMMENT '核销会员id',
  `value` varchar(1000) NOT NULL DEFAULT '' COMMENT '核销内容',
  `body` varchar(500) NOT NULL DEFAULT '' COMMENT '描述',
  `relate_tag` varchar(255) NOT NULL DEFAULT '' COMMENT '业务标识',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '核销记录' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}sys_menu`;
CREATE TABLE `{table_prefix}sys_menu`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `app_type` varchar(255) NOT NULL DEFAULT 'admin' COMMENT '应用类型',
  `menu_name` varchar(32) NOT NULL DEFAULT '' COMMENT '菜单名称',
  `menu_short_name` varchar(50) NOT NULL DEFAULT '' COMMENT '菜单短标题',
  `menu_key` varchar(255) NOT NULL DEFAULT '' COMMENT '菜单标识（菜单输入，接口自动生成）',
  `parent_key` varchar(255) NOT NULL DEFAULT '' COMMENT '父级key',
  `menu_type` tinyint(4) NOT NULL DEFAULT 1 COMMENT '菜单类型 0目录 1菜单 2按钮',
  `icon` varchar(500) NOT NULL DEFAULT '' COMMENT '图标 菜单有效',
  `api_url` varchar(100) NOT NULL DEFAULT '' COMMENT 'api接口地址',
  `router_path` varchar(128) NOT NULL DEFAULT '' COMMENT '菜单路由地址 前端使用',
  `view_path` varchar(255) NOT NULL DEFAULT '' COMMENT '菜单文件地址',
  `methods` varchar(10) NOT NULL DEFAULT '' COMMENT '提交方式POST GET PUT DELETE',
  `sort` int NOT NULL DEFAULT 1 COMMENT '排序',
  `status` tinyint(3) UNSIGNED NOT NULL DEFAULT 1 COMMENT '正常，禁用（禁用后不允许访问）',
  `is_show` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否显示',
  `create_time` int(11) NOT NULL DEFAULT 0,
  `delete_time` int(11) NOT NULL DEFAULT 0,
  `addon` varchar(255) NOT NULL DEFAULT '' COMMENT '所属插件',
  `source` varchar(255) NOT NULL DEFAULT 'system' COMMENT '菜单来源   system 系统文件  create 新建菜单  generator 代码生成器',
  `menu_attr` varchar(50) NOT NULL DEFAULT '' COMMENT '菜单属性 common 公共 system 系统',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}sys_notice`;
CREATE TABLE `{table_prefix}sys_notice`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int(11) NOT NULL DEFAULT 0 COMMENT '站点ID',
  `key` varchar(50) NOT NULL DEFAULT '' COMMENT '标识',
  `sms_content` text NULL COMMENT '短信配置参数',
  `is_wechat` tinyint(4) NOT NULL DEFAULT 0 COMMENT '公众号模板消息（0：关闭，1：开启）',
  `is_weapp` tinyint(4) NOT NULL DEFAULT 0 COMMENT '小程序订阅消息（0：关闭，1：开启）',
  `is_sms` tinyint(4) NOT NULL DEFAULT 0 COMMENT '发送短信（0：关闭，1：开启）',
  `wechat_template_id` varchar(255) NOT NULL DEFAULT '' COMMENT '微信模版消息id',
  `weapp_template_id` varchar(255) NOT NULL DEFAULT '' COMMENT '微信小程序订阅消息id',
  `sms_id` varchar(255) NOT NULL DEFAULT '' COMMENT '短信id（对应短信配置）',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '添加时间',
  `wechat_first` varchar(255) NOT NULL DEFAULT '' COMMENT '微信头部',
  `wechat_remark` varchar(255) NOT NULL DEFAULT '' COMMENT '微信说明',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知模型' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}sys_notice_log`;
CREATE TABLE `{table_prefix}sys_notice_log`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '通知记录ID',
  `site_id` int(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `key` varchar(255) NULL DEFAULT '' COMMENT '消息key',
  `notice_type` varchar(50) NULL DEFAULT 'sms' COMMENT '消息类型（sms,wechat.weapp）',
  `uid` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '通知的用户id',
  `member_id` int(11) NOT NULL DEFAULT 0 COMMENT '消息的会员id',
  `nickname` varchar(255) NOT NULL DEFAULT '' COMMENT '接收人用户昵称或姓名',
  `receiver` varchar(255) NOT NULL DEFAULT '' COMMENT '接收人（对应手机号，openid）',
  `content` text NULL COMMENT '消息数据',
  `is_click` tinyint(3) UNSIGNED NOT NULL DEFAULT 0 COMMENT '点击次数',
  `is_visit` tinyint(3) UNSIGNED NOT NULL DEFAULT 0 COMMENT '访问次数',
  `visit_time` int(11) NOT NULL DEFAULT 0 COMMENT '访问时间',
  `create_time` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '消息时间',
  `result` varchar(1000) NOT NULL DEFAULT '' COMMENT '结果',
  `params` text NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知记录表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}sys_notice_sms_log`;
CREATE TABLE `{table_prefix}sys_notice_sms_log`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `site_id` int(11) NOT NULL DEFAULT 0,
  `mobile` varchar(11) NOT NULL DEFAULT '' COMMENT '手机号码',
  `sms_type` varchar(32) NOT NULL DEFAULT '' COMMENT '发送关键字（注册、找回密码）',
  `key` varchar(32) NOT NULL DEFAULT '' COMMENT '发送关键字（注册、找回密码）',
  `template_id` varchar(50) NOT NULL DEFAULT '',
  `content` text NOT NULL COMMENT '发送内容',
  `params` text NOT NULL COMMENT '数据参数',
  `status` varchar(32) NOT NULL DEFAULT 'sending' COMMENT '发送状态：sending-发送中；success-发送成功；fail-发送失败',
  `result` text NULL COMMENT '短信结果',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '创建时间',
  `send_time` int(11) NOT NULL DEFAULT 0 COMMENT '发送时间',
  `update_time` int(11) NOT NULL DEFAULT 0 COMMENT '更新时间',
  `delete_time` int(11) NOT NULL DEFAULT 0 COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '短信发送表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}sys_role`;
CREATE TABLE `{table_prefix}sys_role`  (
  `role_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `site_id` int(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `role_name` varchar(255) NOT NULL DEFAULT '' COMMENT '角色名称',
  `rules` text NULL COMMENT '角色权限(menus_id)',
  `status` tinyint(3) UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '添加时间',
  `update_time` int(11) NOT NULL DEFAULT 0 COMMENT '最后修改时间',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}sys_poster`;
CREATE TABLE `{table_prefix}sys_poster` (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `site_id` int(11) NOT NULL DEFAULT '0' COMMENT '站点id',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '海报名称',
  `type` varchar(255) NOT NULL DEFAULT '' COMMENT '海报类型',
  `channel` varchar(255) NOT NULL DEFAULT '' COMMENT '海报支持渠道',
  `value` text COMMENT '配置值json',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '是否启用 1启用 2不启用',
  `addon` varchar(255) NOT NULL DEFAULT '' COMMENT '所属插件',
  `is_default` int(11) NOT NULL DEFAULT '0' COMMENT '是否默认海报，1：是，0：否',
  `create_time` int(11) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` int(11) NOT NULL DEFAULT '0' COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '海报表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}sys_printer`;
CREATE TABLE `{table_prefix}sys_printer` (
  `printer_id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `site_id` INT(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `printer_name` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '打印机名称',
  `brand` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '设备品牌（易联云，365，飞鹅）',
  `printer_code` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '打印机编号',
  `printer_key` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '打印机秘钥',
  `open_id` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '开发者id',
  `apikey` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '开发者密钥',
  `template_type` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '小票打印模板类型，多个逗号隔开',
  `trigger` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '触发打印时机',
  `value` LONGTEXT DEFAULT NULL COMMENT '打印模板数据，json格式',
  `print_width` VARCHAR(255) NOT NULL DEFAULT '58mm' COMMENT '纸张宽度',
  `status` TINYINT(4) NOT NULL DEFAULT 1 COMMENT '状态（0，关闭，1：开启）',
  `create_time` INT(11) NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` INT(11) NOT NULL DEFAULT 0 COMMENT '修改时间',
  PRIMARY KEY (`printer_id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '小票打印机' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}sys_printer_template`;
CREATE TABLE `{table_prefix}sys_printer_template` (
  `template_id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `site_id` INT(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `template_name` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '模板名称',
  `template_type` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '模板类型',
  `value` LONGTEXT DEFAULT NULL COMMENT '模板数据，json格式',
  `create_time` INT(11) NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` INT(11) NOT NULL DEFAULT 0 COMMENT '修改时间',
  PRIMARY KEY (`template_id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '小票打印模板' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}sys_schedule`;
CREATE TABLE `{table_prefix}sys_schedule`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int(11) NOT NULL DEFAULT 0,
  `addon` varchar(255) NOT NULL DEFAULT '' COMMENT '所属插件',
  `key` varchar(255) NOT NULL DEFAULT '' COMMENT '计划任务模板key',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '任务状态 是否启用',
  `time` varchar(500) NOT NULL DEFAULT '' COMMENT '任务周期  json结构',
  `count` int(11) NOT NULL DEFAULT 0 COMMENT '执行次数',
  `last_time` int(11) NOT NULL DEFAULT 0 COMMENT '最后执行时间',
  `next_time` int(11) NOT NULL DEFAULT 0 COMMENT '下次执行时间',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '创建时间',
  `delete_time` int(11) NOT NULL DEFAULT 0 COMMENT '删除时间',
  `update_time` int(11) NOT NULL DEFAULT 0 COMMENT '更新时间',
  `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统任务' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}sys_user`;
CREATE TABLE `{table_prefix}sys_user`  (
  `uid` smallint(5) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '系统用户ID',
  `username` varchar(255) NOT NULL DEFAULT '' COMMENT '用户账号',
  `head_img` varchar(255) NOT NULL DEFAULT '',
  `password` varchar(100) NOT NULL DEFAULT '' COMMENT '用户密码',
  `real_name` varchar(16) NOT NULL DEFAULT '' COMMENT '实际姓名',
  `last_ip` varchar(50) NOT NULL DEFAULT '' COMMENT '最后一次登录ip',
  `last_time` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '最后一次登录时间',
  `create_time` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '添加时间',
  `login_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '登录次数',
  `status` tinyint(3) UNSIGNED NOT NULL DEFAULT 1 COMMENT '后台管理员状态 1有效0无效',
  `is_del` tinyint(3) UNSIGNED NOT NULL DEFAULT 0,
  `delete_time` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除时间',
  `update_time` int(11) NOT NULL DEFAULT 0 COMMENT '更新时间',
  PRIMARY KEY (`uid`) USING BTREE,
  INDEX `uid`(`uid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '后台管理员表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}sys_user_log`;
CREATE TABLE `{table_prefix}sys_user_log`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '管理员操作记录ID',
  `ip` varchar(50) NOT NULL DEFAULT '' COMMENT '登录IP',
  `site_id` int(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `uid` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '管理员id',
  `username` varchar(64) NOT NULL DEFAULT '' COMMENT '管理员姓名',
  `url` varchar(128) NOT NULL DEFAULT '' COMMENT '链接',
  `params` longtext DEFAULT NULL COMMENT '参数',
  `type` varchar(32) NOT NULL DEFAULT '' COMMENT '请求方式',
  `create_time` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '管理员操作记录表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}sys_user_role`;
CREATE TABLE `{table_prefix}sys_user_role`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` int(11) NOT NULL DEFAULT 0 COMMENT '用户id',
  `site_id` int(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `role_ids` varchar(255) NOT NULL DEFAULT '' COMMENT '角色id',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '添加时间',
  `is_admin` int(11) NOT NULL DEFAULT 0 COMMENT '是否是超级管理员',
  `status` int(11) NOT NULL DEFAULT 1 COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户权限表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}weapp_version`;
CREATE TABLE `{table_prefix}weapp_version`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int(11) NOT NULL DEFAULT 0,
  `version` varchar(255) NOT NULL DEFAULT '',
  `version_no` int(11) NOT NULL DEFAULT 1,
  `desc` varchar(255) NOT NULL DEFAULT '' COMMENT '说明',
  `create_time` int(11) NOT NULL DEFAULT 0,
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '状态',
  `update_time` int(11) NOT NULL DEFAULT 0,
  `fail_reason` text DEFAULT NULL,
  `task_key` varchar(20) NOT NULL DEFAULT '' COMMENT '上传任务key',
  `from_type` VARCHAR(255) NOT NULL DEFAULT 'cloud_build',
  `auditid` VARCHAR(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '小程序版本' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}wechat_fans`;
CREATE TABLE `{table_prefix}wechat_fans`  (
  `fans_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '粉丝ID',
  `site_id` int(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `nickname` varchar(255) NOT NULL DEFAULT '' COMMENT '昵称',
  `avatar` varchar(500) NOT NULL DEFAULT '' COMMENT '头像',
  `sex` smallint(6) NOT NULL DEFAULT 1 COMMENT '性别',
  `language` varchar(20) NOT NULL DEFAULT '' COMMENT '用户语言',
  `country` varchar(60) NOT NULL DEFAULT '' COMMENT '国家',
  `province` varchar(255) NOT NULL DEFAULT '' COMMENT '省',
  `city` varchar(255) NOT NULL DEFAULT '' COMMENT '城市',
  `district` varchar(255) NOT NULL DEFAULT '' COMMENT '行政区/县',
  `openid` varchar(255) NOT NULL DEFAULT '' COMMENT '用户的标识，对当前公众号唯一     用户的唯一身份ID',
  `unionid` varchar(255) NOT NULL DEFAULT '' COMMENT '粉丝unionid',
  `groupid` int(11) NOT NULL DEFAULT 0 COMMENT '粉丝所在组id',
  `is_subscribe` tinyint(4) NOT NULL DEFAULT 1 COMMENT '是否订阅',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `subscribe_time` int(11) NOT NULL DEFAULT 0 COMMENT '关注时间',
  `subscribe_scene` varchar(100) NOT NULL DEFAULT '' COMMENT '返回用户关注的渠道来源',
  `unsubscribe_time` int(11) NOT NULL DEFAULT 0 COMMENT '取消关注时间',
  `update_time` int(11) NOT NULL DEFAULT 0 COMMENT '粉丝信息最后更新时间',
  `app_id` int(11) NOT NULL DEFAULT 0 COMMENT '应用appid',
  PRIMARY KEY (`fans_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '微信粉丝列表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}wechat_media`;
CREATE TABLE `{table_prefix}wechat_media`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `site_id` int(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `type` varchar(255) NOT NULL DEFAULT '' COMMENT '类型',
  `value` text NULL COMMENT '值',
  `create_time` int(11) NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` int(11) NOT NULL DEFAULT 0 COMMENT '修改时间',
  `media_id` varchar(70) NOT NULL DEFAULT '0' COMMENT '微信端返回的素材id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '微信素材表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}wechat_reply`;
CREATE TABLE `{table_prefix}wechat_reply`  (
 `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
 `name` varchar(64) NOT NULL DEFAULT '' COMMENT '规则名称',
 `site_id` int NOT NULL DEFAULT 0 COMMENT '站点id',
 `keyword` varchar(64) NOT NULL DEFAULT '' COMMENT '关键词',
 `reply_type` varchar(30) NOT NULL DEFAULT '' COMMENT '回复类型 subscribe-关注回复 keyword-关键字回复 default-默认回复',
 `matching_type` varchar(30) NOT NULL DEFAULT '1' COMMENT '匹配方式：full 全匹配；like-模糊匹配',
 `content` text NOT NULL COMMENT '回复内容',
 `sort` int UNSIGNED NOT NULL DEFAULT 50 COMMENT '排序',
 `create_time` int NOT NULL DEFAULT 0 COMMENT '创建时间',
 `update_time` int NOT NULL DEFAULT 0 COMMENT '更新时间',
 `delete_time` int NOT NULL DEFAULT 0 COMMENT '删除时间',
 `reply_method` varchar(50) NOT NULL DEFAULT '' COMMENT '回复方式 all 全部 rand随机',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '公众号消息回调表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}wx_oplatfrom_weapp_version`;
CREATE TABLE `{table_prefix}wx_oplatfrom_weapp_version` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `site_group_id` INT(11) NOT NULL DEFAULT 0 COMMENT '站点套餐id',
  `template_id` VARCHAR(255) NOT NULL DEFAULT '0' COMMENT '代码模板 ID',
  `user_version` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '代码版本号',
  `user_desc` VARCHAR(255) DEFAULT '' COMMENT '代码描述',
  `task_key` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '上传任务key',
  `status` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '状态',
  `fail_reason` TEXT DEFAULT NULL COMMENT '失败原因',
  `version_no` INT(11) NOT NULL DEFAULT 0,
  `create_time` INT(11) NOT NULL DEFAULT 0,
  `update_time` INT(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '微信小程序开发平台版本表' ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `{table_prefix}user_create_site_limit`;
CREATE TABLE `{table_prefix}user_create_site_limit` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `group_id` INT(11) NOT NULL DEFAULT 0,
  `uid` INT(11) NOT NULL DEFAULT 0,
  `num` INT(11) NOT NULL DEFAULT 0,
  `month` INT(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户站点创建限制表' ROW_FORMAT = Dynamic;

INSERT INTO `{table_prefix}site`(site_id, site_name, group_id, keywords, app_type, logo, `desc`, status, latitude, longitude, province_id, city_id, district_id, address, full_address, phone, business_hours, create_time, expire_time, front_end_name, front_end_logo, front_end_icon, icon, member_no, app, addons, initalled_addon, site_domain) VALUES
(1, 'niucloud-admin', 0, '', 'admin', '', '', 1, '', '', 0, 0, 0, '', '', '', '', 0, 0, '', '', '', '', '0', '', '', '', '');

UPDATE `{table_prefix}site` SET site_id = 0 WHERE  site_id = 1;

INSERT INTO `{table_prefix}sys_user` VALUES ('1', 'admin', '', '$2a$10$32zCE/6SkAUCGPhtIsKwG.2XDfsW.t4SHPmhNUjLBF6uQ54Ww6uue', '', '', '0', '0', '0', '1', '0', '0', '0');

INSERT INTO `{table_prefix}sys_user_role` VALUES ('1', '1', '0', '', '0', '1', '1');

