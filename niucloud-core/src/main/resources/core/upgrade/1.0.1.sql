
DROP TABLE IF EXISTS `{{prefix}}activity_exchange_code`;

CREATE TABLE `{{prefix}}activity_exchange_code`
(
    `id`             INT(11) NOT NULL AUTO_INCREMENT,
    `site_id`        INT(11) NOT NULL DEFAULT 0,
    `code`           VARCHAR(255) NOT NULL DEFAULT '' COMMENT '兑换码',
    `status`         VARCHAR(20)  NOT NULL DEFAULT 'normal' COMMENT 'normal-正常  received-被领取',
    `activity_type`  VARCHAR(20)  NOT NULL DEFAULT '' COMMENT '例seckill-秒杀活动',
    `activity_id`    INT(11) NOT NULL DEFAULT 0 COMMENT '活动ID',
    `type`           VARCHAR(20)  NOT NULL DEFAULT '' COMMENT '类型    例seckill_goods-秒杀商品',
    `type_id`        INT(11) NOT NULL DEFAULT 0 COMMENT '类型对应id  秒杀商品id',
    `expire_time`    INT(11) NOT NULL DEFAULT 0 COMMENT '过期时间 0-不过期',
    `member_id`      INT(11) NOT NULL DEFAULT 0 COMMENT '领取会员',
    `received_time`  INT(11) NOT NULL DEFAULT 0 COMMENT '领取时间',
    `order_id`       INT(11) NOT NULL DEFAULT 0 COMMENT '对应订单id',
    `admin_username` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '操作人名称（添加人）',
    `admin_id`       INT(11) NOT NULL DEFAULT 0 COMMENT '操作人id',
    `create_time`    INT(11) NOT NULL DEFAULT 0 COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='活动兑换码表';

DROP TABLE IF EXISTS `{{prefix}}app_version`;

CREATE TABLE `{{prefix}}app_version` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `site_id` INT(11) NOT NULL DEFAULT 0 COMMENT '站点id',
  `version_code` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '版本号',
  `version_name` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '版本名称',
  `version_desc` VARCHAR(1500) NOT NULL DEFAULT '' COMMENT '版本描述',
  `platform` VARCHAR(255) NOT NULL DEFAULT '' COMMENT 'app平台 Android Ios',
  `package_path` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '安装包路径',
  `status` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '状态',
  `is_forced_upgrade` INT(11) NOT NULL DEFAULT 0 COMMENT '是否需要强制升级',
  `task_key` VARCHAR(255) NOT NULL DEFAULT '',
  `fail_reason` VARCHAR(255) NOT NULL DEFAULT '',
  `upgrade_type` VARCHAR(255) NOT NULL DEFAULT 'app' COMMENT 'app 整包更新 hot 热更新 market 应用市场更新',
  `release_time` INT(11) NOT NULL DEFAULT 0 COMMENT '发布时间',
  `create_time` INT(11) NOT NULL DEFAULT 0 COMMENT '创建时间',
  `update_time` INT(11) NOT NULL DEFAULT 0 COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'app版本管理' ROW_FORMAT = Dynamic;


ALTER TABLE `{{prefix}}member` ADD COLUMN `wxapp_openid` varchar(255) NOT NULL DEFAULT '' COMMENT '微信移动应用openid' AFTER `wx_unionid`;

INSERT INTO `{{prefix}}sys_area` VALUES
(232718, 232700, '加格达奇区', '加格达奇区', '124.126716', '50.424654', 3, 0, 1),
(330113, 330100, '临平区', '临平区', '120.299376', '30.419025', 3, 0, 1),
(330114, 330100, '钱塘区', '钱塘区', '120.493972', '30.322904', 3, 0, 1),
(460301, 460300, '西沙区', '西沙区', '112.3386402', '16.8310066', 3, 0, 1),
(460302, 460300, '南沙区', '南沙区', '112.891018', '9.543575', 3, 0, 1),
(632825, 632800, '海西蒙古族藏族自治州直辖', '海西蒙古族藏族自治州直辖', '95.357233', '37.853631', 3, 0, 1),
(710000, 0, '台湾省', '台湾省', '121.509062', '25.044332', 1, 0, 1),
(810000, 0, '香港特别行政区', '香港特别行政区', '114.173355', '22.320048', 1, 0, 1),
(810001, 810000, '中西区', '中西区', '114.1543731', '22.28198083', 3, 0, 1),
(810002, 810000, '湾仔区', '湾仔区', '114.1829153', '22.27638889', 3, 0, 1),
(810003, 810000, '东区', '东区', '114.2260031', '22.27969306', 3, 0, 1),
(810004, 810000, '南区', '南区', '114.1600117', '22.24589667', 3, 0, 1),
(810005, 810000, '油尖旺区', '油尖旺区', '114.1733317', '22.31170389', 3, 0, 1),
(810006, 810000, '深水埗区', '深水埗区', '114.1632417', '22.33385417', 3, 0, 1),
(810007, 810000, '九龙城区', '九龙城区', '114.1928467', '22.31251', 3, 0, 1),
(810008, 810000, '黄大仙区', '黄大仙区', '114.2038856', '22.33632056', 3, 0, 1),
(810009, 810000, '观塘区', '观塘区', '114.2140542', '22.32083778', 3, 0, 1),
(810010, 810000, '荃湾区', '荃湾区', '114.1210792', '22.36830667', 3, 0, 1),
(810011, 810000, '屯门区', '屯门区', '113.9765742', '22.39384417', 3, 0, 1),
(810012, 810000, '元朗区', '元朗区', '114.0324381', '22.44142833', 3, 0, 1),
(810013, 810000, '北区', '北区', '114.1473639', '22.49610389', 3, 0, 1),
(810014, 810000, '大埔区', '大埔区', '114.1717431', '22.44565306', 3, 0, 1),
(810015, 810000, '西贡区', '西贡区', '114.264645', '22.31421306', 3, 0, 1),
(810016, 810000, '沙田区', '沙田区', '114.1953653', '22.37953167', 3, 0, 1),
(810017, 810000, '葵青区', '葵青区', '114.1393194', '22.36387667', 3, 0, 1),
(810018, 810000, '离岛区', '离岛区', '113.94612', '22.28640778', 3, 0, 1),
(820000, 0, '澳门特别行政区', '澳门特别行政区', '113.54909', '22.198951', 1, 0, 1),
(820001, 820000, '花地玛堂区', '花地玛堂区', '113.5528956', '22.20787', 3, 0, 1),
(820002, 820000, '花王堂区', '花王堂区', '113.5489608', '22.1992075', 3, 0, 1),
(820003, 820000, '望德堂区', '望德堂区', '113.5501828', '22.19372083', 3, 0, 1),
(820004, 820000, '大堂区', '大堂区', '113.5536475', '22.18853944', 3, 0, 1),
(820005, 820000, '风顺堂区', '风顺堂区', '113.5419278', '22.18736806', 3, 0, 1),
(820006, 820000, '嘉模堂区', '嘉模堂区', '113.5587044', '22.15375944', 3, 0, 1),
(820007, 820000, '路凼填海区', '路凼填海区', '113.5695992', '22.13663', 3, 0, 1),
(820008, 820000, '圣方济各堂区', '圣方济各堂区', '113.5599542', '22.12348639', 3, 0, 1);