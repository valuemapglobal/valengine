-- ============================================================
-- RiskSmart Data Middle Station 数据库初始化脚本
-- 数据库: risk_smart_data_middle_station
-- 表数量: 19
-- ============================================================

USE `risk_smart_data_middle_station`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- -----------------------------------------------------------
-- 表: feature_attribute
-- 说明: 特征变量属性表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `feature_attribute`;
CREATE TABLE `feature_attribute` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) NOT NULL COMMENT '特征变量名称',
  `code` varchar(255) NOT NULL COMMENT '参数名',
  `module_id` bigint(20) NOT NULL COMMENT '所属模块的id',
  `type` tinyint(4) NOT NULL COMMENT '特征变量类型（0-字符型，1-小数，2-数值）',
  `threshold_type` tinyint(4) NOT NULL COMMENT '是否固定阈值（0-是，1-否，2-自定义）',
  `config_threshold` text COMMENT '阈值配置',
  `config_compute` text COMMENT '计算配置',
  `config_script` text COMMENT '脚本配置',
  `data_status` bit(1) NOT NULL DEFAULT b'0' COMMENT '逻辑删除（0-正常，1-删除）',
  `user_id` bigint(20) NOT NULL COMMENT '数据创建用户id',
  `dept_id` bigint(20) NOT NULL COMMENT '数据创建用户部门id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '数据更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='特征变量属性表';

-- -----------------------------------------------------------
-- 表: feature_module
-- 说明: 特征变量模块表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `feature_module`;
CREATE TABLE `feature_module` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) NOT NULL COMMENT '模块名称',
  `code` varchar(255) NOT NULL COMMENT '模块唯一标识',
  `type` tinyint(4) NOT NULL COMMENT '模块类型（0-对象，1-集合）',
  `data_type` tinyint(4) NOT NULL COMMENT '数据分类（0-企业，1-个人，2-实体资产，3-供应链）',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `metadata` varchar(255) NOT NULL COMMENT '关联元数据的接口编号，json数组格式',
  `data_status` bit(1) NOT NULL DEFAULT b'0' COMMENT '逻辑删除（0-正常，1-删除）',
  `user_id` bigint(20) NOT NULL COMMENT '数据创建用户id',
  `dept_id` bigint(20) NOT NULL COMMENT '数据创建用户部门id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '数据更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='特征变量模块表';

-- -----------------------------------------------------------
-- 表: interface_dept_app
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `interface_dept_app`;
CREATE TABLE `interface_dept_app` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `dept_id` bigint(11) NOT NULL COMMENT '部门id',
  `dept_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '部门名称',
  `app_key` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'appkey',
  `secret` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'secret',
  `url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '访问路径',
  `cache_time` int(11) NOT NULL DEFAULT '0' COMMENT '缓存时间（天）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `data_status` bit(1) NOT NULL DEFAULT b'0' COMMENT '逻辑删除（0-未删除，1-已删除）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- -----------------------------------------------------------
-- 表: interface_fie_id_manage
-- 说明: 接口参数管理表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `interface_field_id_manage`;
CREATE TABLE `interface_field_id_manage` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `interface_field_id_manage` varchar(64) NOT NULL COMMENT '接口参数唯一标识',
  `interface_manage_no` varchar(64) NOT NULL COMMENT '接口管理唯一标识',
  `interface_no` varchar(128) DEFAULT NULL COMMENT '接口编号',
  `interface_field_id_name` varchar(128) NOT NULL COMMENT '参数名称',
  `interface_field_id_alias` varchar(128) NOT NULL COMMENT '参数别名',
  `interface_field_id_description` varchar(1024) NOT NULL COMMENT '参数说明',
  `interface_field_id_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '参数类型(0-入参，1-出参)',
  `interface_field_id_data_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '数据类型(0-数值，1-字符串，2-日期，3-对象，4-数组，5-文件，6-布尔，7-小数)',
  `interface_field_id_required` tinyint(1) NOT NULL DEFAULT '1' COMMENT '入参必填项(0-是，1-否)',
  `interface_field_id_index` int(11) DEFAULT NULL COMMENT '接口序号',
  `interface_field_id_remark` varchar(128) DEFAULT NULL COMMENT '参数备注',
  `interface_field_id_defult_value` varchar(1024) DEFAULT NULL COMMENT '默认值',
  `interface_field_id_father` varchar(128) DEFAULT NULL COMMENT '父级属性',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `data_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '数据状态(0-正常，1-删除)',
  `user_id` bigint(20) NOT NULL,
  `dept_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `interface_field_id_manage` (`interface_field_id_manage`) USING BTREE,
  KEY `interface_manage_no` (`interface_manage_no`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=458 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='接口参数管理表';

-- -----------------------------------------------------------
-- 表: interface_log
-- 说明: 接口日志表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `interface_log`;
CREATE TABLE `interface_log` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `order_id` varchar(64) DEFAULT NULL COMMENT '订单号',
  `interface_manage_no` varchar(64) NOT NULL COMMENT '接口管理唯一标识',
  `interface_log` varchar(64) NOT NULL COMMENT '接口日志唯一标识',
  `interface_name` varchar(256) NOT NULL COMMENT '接口名称',
  `user_id` bigint(11) DEFAULT NULL COMMENT '用户id',
  `user_dept` varchar(256) DEFAULT NULL COMMENT '用户部门',
  `access_time` datetime NOT NULL COMMENT '调用时间',
  `result_time` datetime DEFAULT NULL COMMENT '响应时间',
  `total_time` decimal(12,2) DEFAULT NULL COMMENT '响应总耗时',
  `ip` varchar(256) DEFAULT NULL COMMENT '调用地址',
  `request_headers` text COMMENT '请求头',
  `request_method` varchar(256) DEFAULT NULL COMMENT '请求方法',
  `code` int(11) DEFAULT NULL COMMENT '响应code',
  `param` text COMMENT '请求参数',
  `result` longtext COMMENT '响应结果',
  `error_msg` longtext COMMENT '异常msg',
  `error` longtext COMMENT '异常信息',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `order_no` varchar(256) DEFAULT NULL COMMENT '订单编号',
  `charging_flag` varchar(255) DEFAULT NULL COMMENT '接口计费 0 不计费  1计费',
  `dept_id` int(20) DEFAULT NULL COMMENT '部门id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `interface_log` (`interface_log`) USING BTREE,
  KEY `interface_manage_no` (`interface_manage_no`) USING BTREE,
  KEY `interface_name` (`interface_name`) USING BTREE,
  KEY `access_time` (`access_time`) USING BTREE,
  KEY `code` (`code`) USING BTREE,
  KEY `order_id` (`order_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2829 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='接口日志表';

-- -----------------------------------------------------------
-- 表: interface_manage
-- 说明: 接口管理表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `interface_manage`;
CREATE TABLE `interface_manage` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `interface_source_no` varchar(64) NOT NULL COMMENT '接口供应商唯一标识',
  `interface_manage_no` varchar(64) NOT NULL COMMENT '接口管理唯一标识',
  `interface_no` varchar(128) DEFAULT NULL COMMENT '接口编号',
  `interface_name` varchar(256) NOT NULL COMMENT '接口名称',
  `interface_type` tinyint(2) DEFAULT NULL COMMENT '接口类型 0个人接口，1 企业接口，2 司法接口',
  `interface_description` varchar(128) DEFAULT NULL COMMENT '接口描述',
  `interface_features` varchar(256) DEFAULT NULL COMMENT '接口特点',
  `interface_scenes` varchar(256) DEFAULT NULL COMMENT '接口场景',
  `interface_covering_volume` varchar(256) DEFAULT NULL COMMENT '接口覆盖体量',
  `interface_tag` varchar(256) DEFAULT NULL COMMENT '接口标签',
  `interface_link` varchar(256) NOT NULL COMMENT '接口网址',
  `interface_quota` varchar(256) DEFAULT NULL COMMENT '接口配额',
  `interface_index` int(11) DEFAULT NULL COMMENT '接口序号',
  `interface_on` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0 开启，1 关闭',
  `param_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0-Json，1-from-data',
  `request_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0-POST, 1-GET',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `data_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '数据状态(0-正常，1-删除)',
  `interface_version` varchar(32) NOT NULL DEFAULT '1.0' COMMENT '版本号',
  `return_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '返回值类型（0-对象，5-数组）',
  `price` decimal(10,1) NOT NULL COMMENT '单价',
  `user_id` bigint(20) NOT NULL,
  `dept_id` bigint(20) NOT NULL,
  `timeout` int(11) NOT NULL DEFAULT '60000' COMMENT '超时时间（毫秒）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `interface_manage_no` (`interface_manage_no`) USING BTREE,
  KEY `interface_name` (`interface_name`) USING BTREE,
  KEY `interface_source_no` (`interface_source_no`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='接口管理表';

-- -----------------------------------------------------------
-- 表: interface_permissions_manage
-- 说明: 接口权限管理表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `interface_permissions_manage`;
CREATE TABLE `interface_permissions_manage` (
  `interface_manage_no` varchar(64) NOT NULL COMMENT '接口管理唯一标识',
  `user_id` bigint(11) DEFAULT NULL COMMENT '用户id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  KEY `interface_manage_no` (`interface_manage_no`) USING BTREE,
  KEY `user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='接口权限管理表';

-- -----------------------------------------------------------
-- 表: interface_source_manage
-- 说明: 接口供应商管理表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `interface_source_manage`;
CREATE TABLE `interface_source_manage` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `interface_source_no` varchar(64) NOT NULL COMMENT '来源唯一标识',
  `data_name` varchar(128) NOT NULL COMMENT '数据名称',
  `source` varchar(128) NOT NULL COMMENT '来源',
  `web_link` varchar(128) NOT NULL COMMENT '网址',
  `admin_name` varchar(128) NOT NULL COMMENT '负责人',
  `contact_details` varchar(128) NOT NULL COMMENT '联系方式',
  `interface_data_type` tinyint(4) NOT NULL COMMENT '0 元数据，1 特征变量，2 分析指标',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `data_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '数据状态(0-正常，1-删除)',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `dept_id` bigint(20) NOT NULL COMMENT '部门id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `interface_source_no` (`interface_source_no`) USING BTREE,
  KEY `data_name` (`data_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='接口供应商管理表';

-- -----------------------------------------------------------
-- 表: interface_user
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `interface_user`;
CREATE TABLE `interface_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int(11) NOT NULL COMMENT '用户Id',
  `app_key` varchar(255) DEFAULT NULL COMMENT 'appKey',
  `secret` varchar(255) DEFAULT NULL COMMENT 'secret',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- -----------------------------------------------------------
-- 表: metrics_attribute
-- 说明: 分析指标属性表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `metrics_attribute`;
CREATE TABLE `metrics_attribute` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) NOT NULL COMMENT '属性名称',
  `code` varchar(255) NOT NULL COMMENT '属性唯一标识',
  `module_id` bigint(20) NOT NULL COMMENT '归属模块id',
  `formula_zh` varchar(255) NOT NULL COMMENT '公式（中文展示）',
  `formula` varchar(255) NOT NULL COMMENT '公式',
  `variable` text NOT NULL COMMENT '预变量，json格式',
  `data_status` bit(1) NOT NULL DEFAULT b'0' COMMENT '逻辑删除（0-正常，1-删除）',
  `user_id` bigint(20) NOT NULL COMMENT '数据创建用户id',
  `dept_id` bigint(20) NOT NULL COMMENT '数据创建用户部门id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '数据更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='分析指标属性表';

-- -----------------------------------------------------------
-- 表: metrics_module
-- 说明: 分析指标模块表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `metrics_module`;
CREATE TABLE `metrics_module` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) NOT NULL COMMENT '模块名称',
  `code` varchar(255) NOT NULL COMMENT '模块唯一标识',
  `type` tinyint(4) NOT NULL COMMENT '模块类型（0-对象，1-集合）',
  `data_type` tinyint(4) NOT NULL COMMENT '数据分类（0-企业，1-个人，2-实体资产，3-供应链）',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `association` text NOT NULL COMMENT '关联的元数据或特征变量，json数组格式',
  `data_status` bit(1) NOT NULL DEFAULT b'0' COMMENT '逻辑删除（0-正常，1-删除）',
  `user_id` bigint(20) NOT NULL COMMENT '数据创建用户id',
  `dept_id` bigint(20) NOT NULL COMMENT '数据创建用户部门id',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '数据更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='分析指标模块表';

-- -----------------------------------------------------------
-- 表: rde_risk_source_group
-- 说明: 源数据分组表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rde_risk_source_group`;
CREATE TABLE `rde_risk_source_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT '' COMMENT '名称',
  `keycode` varchar(100) DEFAULT '' COMMENT '标识',
  `parent_id` int(11) DEFAULT '0' COMMENT '父级别Id',
  `parent_name` varchar(255) DEFAULT '' COMMENT '父级名称',
  `list_name` varchar(255) DEFAULT '' COMMENT '集合名称',
  `source_type` int(2) DEFAULT '1' COMMENT '1.源数据，2.衍生数据,3,应用数据，4.策略数据',
  `type` varchar(255) DEFAULT '0' COMMENT '类型对象 int 字符串',
  `remark` varchar(500) DEFAULT '' COMMENT '备注-别名',
  `create_user_id` int(11) NOT NULL DEFAULT '0' COMMENT '操作人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `data_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '数据状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='源数据分组表';

-- -----------------------------------------------------------
-- 表: rde_risk_source_logic
-- 说明: 源数据关联逻辑表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rde_risk_source_logic`;
CREATE TABLE `rde_risk_source_logic` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `record_id` int(11) DEFAULT '0' COMMENT '记录ID',
  `source_type` int(2) DEFAULT '1' COMMENT '1.小望，2，3，4',
  `standard_biz_type` int(2) DEFAULT NULL COMMENT '1：增值税一般纳税人，2：增值税小规模纳税人，3：资产负债表，4：现金流量表，5：源数据财税附表字段，6：源数据违法违章',
  `tab_type` int(2) DEFAULT NULL COMMENT '1.一般项目本月数，2一般项目本年累计，3.即征即退本月数，4即征即退本年累计',
  `tab_id` int(11) DEFAULT '0' COMMENT '表id',
  `tab_name` varchar(100) DEFAULT '' COMMENT '目标表',
  `tab_col` varchar(100) DEFAULT '' COMMENT '目标列',
  `tab_val` varchar(100) DEFAULT '' COMMENT '目标值',
  `get_val` varchar(255) DEFAULT NULL COMMENT '取到目标值',
  `logic` varchar(200) DEFAULT '' COMMENT '数据逻辑',
  `priority` int(2) DEFAULT '0' COMMENT '优先级1',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_user_id` int(11) DEFAULT NULL COMMENT '操作人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `data_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '数据状态',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_record_id` (`record_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='源数据关联逻辑表';

-- -----------------------------------------------------------
-- 表: rde_risk_source_record
-- 说明: 源数据分组明细表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rde_risk_source_record`;
CREATE TABLE `rde_risk_source_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) DEFAULT '0' COMMENT '分组ID',
  `code` varchar(100) DEFAULT '' COMMENT '标识',
  `name` varchar(100) DEFAULT '' COMMENT '名称',
  `type` varchar(100) DEFAULT '' COMMENT '类型',
  `logic` varchar(200) DEFAULT '' COMMENT '数据逻辑',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_user_id` int(11) DEFAULT NULL COMMENT '操作人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `data_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '数据状态',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_group_id` (`group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='源数据分组明细表';

-- -----------------------------------------------------------
-- 表: rde_risk_variable_group
-- 说明: 风险变量分组表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rde_risk_variable_group`;
CREATE TABLE `rde_risk_variable_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `theme_id` int(11) DEFAULT '0' COMMENT '主题ID',
  `name` varchar(500) DEFAULT '' COMMENT '名称',
  `keycode` varchar(200) DEFAULT '' COMMENT '标识',
  `parent_id` int(11) DEFAULT '0' COMMENT '父级别Id',
  `parent_name` varchar(255) DEFAULT '' COMMENT '父级名称',
  `list_name` varchar(255) DEFAULT '' COMMENT '集合名称',
  `type` varchar(255) DEFAULT '0' COMMENT '类型对象 int 字符串',
  `remark` varchar(500) DEFAULT '' COMMENT '备注-别名',
  `create_user_id` int(11) NOT NULL COMMENT '操作人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `data_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '数据状态',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_theme_id` (`theme_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='风险变量分组表';

-- -----------------------------------------------------------
-- 表: rde_risk_variable_record
-- 说明: 风险变量分组明细表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rde_risk_variable_record`;
CREATE TABLE `rde_risk_variable_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `theme_id` int(11) DEFAULT '0' COMMENT '主题ID',
  `group_id` int(11) DEFAULT '0' COMMENT '分组ID',
  `code` varchar(200) DEFAULT '' COMMENT '标识',
  `name` varchar(500) DEFAULT '' COMMENT '名称',
  `type` varchar(200) DEFAULT '' COMMENT '类型',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_user_id` int(11) DEFAULT NULL COMMENT '操作人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `data_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '数据状态',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_group_id` (`group_id`) USING BTREE,
  KEY `index_theme_id` (`theme_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='风险变量分组明细表';

-- -----------------------------------------------------------
-- 表: rde_risk_variable_theme
-- 说明: 风险变量主题表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rde_risk_variable_theme`;
CREATE TABLE `rde_risk_variable_theme` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(500) DEFAULT '' COMMENT '名称',
  `keycode` varchar(200) DEFAULT '' COMMENT '唯一标识',
  `package_type` varchar(3) DEFAULT '0' COMMENT '默认:1.银行流水，2kyc 3.风险预计 (0分析主体，1衍生主体旧的)',
  `file_url` varchar(255) DEFAULT NULL COMMENT '文档连接',
  `file_status` int(2) DEFAULT '0' COMMENT '解析状态',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_user_id` int(11) NOT NULL COMMENT '操作人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `data_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '数据状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='风险变量主题表';

-- -----------------------------------------------------------
-- 表: rde_risk_warn_result_log
-- 说明: 预警决策结果日志表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rde_risk_warn_result_log`;
CREATE TABLE `rde_risk_warn_result_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `task_no` varchar(64) NOT NULL COMMENT '关联的决策任务编号',
  `warn_code` varchar(100) NOT NULL COMMENT '命中的预警规则Code',
  `hit_meta_ids_json` text COMMENT '命中的metaId列表（JSON格式）',
  `raw_warn_vo_json` text COMMENT '完整的WarnVO对象（JSON格式），用于备份和追溯',
  `process_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '处理状态：0-未处理, 1-已处理, 99-处理失败',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_task_no` (`task_no`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警决策结果日志表';


SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- risk_smart_data_middle_station 初始化完成
-- ============================================================