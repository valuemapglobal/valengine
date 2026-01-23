-- ============================================================
-- RiskSmart Decision Manage 数据库初始化脚本
-- 数据库: risk_smart_decision_manage
-- 表数量: 62
-- ============================================================

USE `risk_smart_decision_manage`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- -----------------------------------------------------------
-- 表: business
-- 说明: 业务场景表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `business`;
CREATE TABLE `business` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(255) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `dept_id` bigint(20) DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `data_status` bit(1) NOT NULL DEFAULT b'0',
  `dept_flag` tinyint(2) NOT NULL COMMENT '1-标准产品，2-自建产品',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='业务场景表';

-- -----------------------------------------------------------
-- 表: data_calling
-- 说明: 数据调用表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `data_calling`;
CREATE TABLE `data_calling` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `manageNo` varchar(255) DEFAULT NULL COMMENT '数据中台接口唯一键',
  `interfaceType` varchar(255) DEFAULT NULL COMMENT '数据类型',
  `modelApplication` int(2) DEFAULT NULL COMMENT '模型应用',
  `callStatus` varchar(255) DEFAULT NULL COMMENT '调用状态',
  `orderNo` varchar(255) DEFAULT NULL COMMENT '交易号',
  `createTime` datetime(2) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(2) COMMENT '创建时间',
  `responseBody` longtext COMMENT '返还数据',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2823 DEFAULT CHARSET=utf8 COMMENT='数据调用表';

-- -----------------------------------------------------------
-- 表: model_process_data
-- 说明: 任务请求响应存储表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `model_process_data`;
CREATE TABLE `model_process_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_no` varchar(255) DEFAULT NULL COMMENT '任务号',
  `parameter` longtext COMMENT '入参',
  `response_value` longtext COMMENT '响应值',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `data_status` tinyint(1) DEFAULT '0' COMMENT '数据状态 0正常 1删除',
  `rule_code` int(11) DEFAULT NULL COMMENT '策略标识导航  1评分 5规则 6分类',
  `model_id` varchar(255) DEFAULT NULL COMMENT '模型ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2855 DEFAULT CHARSET=utf8mb4 COMMENT='任务请求响应存储表';

-- -----------------------------------------------------------
-- 表: model_regular_data
-- 说明: 模型规则存储表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `model_regular_data`;
CREATE TABLE `model_regular_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_number` varchar(255) DEFAULT NULL COMMENT '任务编号',
  `node_id` int(11) DEFAULT NULL COMMENT '所处节点',
  `rule_data` longtext COMMENT '所选模型规则数据',
  `rule_code` int(11) DEFAULT NULL COMMENT '策略场景标识',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `data_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '数据状态 默认0 删除1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2112 DEFAULT CHARSET=utf8mb4 COMMENT='模型规则存储表';

-- -----------------------------------------------------------
-- 表: model_task_execution_failure
-- 说明: 决策引擎任务执行失败日志表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `model_task_execution_failure`;
CREATE TABLE `model_task_execution_failure` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_no` varchar(64) NOT NULL COMMENT '关联的任务号 (model_task_record.task_no)',
  `batch_id` bigint(20) DEFAULT NULL COMMENT '关联的批次ID',
  `process_id` int(11) DEFAULT NULL COMMENT '关联的流程策略ID',
  `failure_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '失败发生时间',
  `scene_code` varchar(10) NOT NULL COMMENT '场景编号 (例如: A1, E3)',
  `failure_category` varchar(50) NOT NULL COMMENT '失败大类 (外部服务异常/提交信息有误/内部系统异常)',
  `failure_scene` varchar(100) NOT NULL COMMENT '具体失败场景 (数据中台接口超时)',
  `is_retryable` tinyint(1) DEFAULT '0' COMMENT '是否可重试 (1=是, 0=否)',
  `user_message` varchar(500) NOT NULL COMMENT '给用户的友好提示',
  `technical_detail` text COMMENT 'JSON格式的技术细节 (使用TEXT以存储更长的内容)',
  `stack_trace_snippet` text COMMENT '异常堆栈摘要 (使用TEXT以存储更长的内容)',
  `data_status` tinyint(2) DEFAULT '0' COMMENT '数据状态 (0=正常, 1=已删除)',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_task_no` (`task_no`) USING BTREE,
  KEY `idx_batch_failure` (`batch_id`,`failure_time`) USING BTREE,
  KEY `idx_scene_stats` (`scene_code`,`failure_time`) USING BTREE,
  KEY `idx_category_stats` (`failure_category`,`failure_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='决策引擎任务执行失败日志表';

-- -----------------------------------------------------------
-- 表: model_task_record
-- 说明: 模型任务记录表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `model_task_record`;
CREATE TABLE `model_task_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `batch_id` int(11) DEFAULT NULL COMMENT '所属批次ID (model_task_record_batch.id)',
  `task_no` varchar(255) NOT NULL COMMENT '任务号',
  `application_user` varchar(255) DEFAULT NULL COMMENT '申请用户',
  `process_entry` longtext COMMENT '流程入参',
  `business_code` bigint(20) DEFAULT NULL COMMENT '业务场景',
  `process_strategy` varchar(255) DEFAULT NULL COMMENT '流程策略',
  `model_name` varchar(255) DEFAULT NULL COMMENT '模型名称',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `dept_name` varchar(255) DEFAULT NULL COMMENT '部门名称',
  `response_form` int(11) DEFAULT NULL COMMENT '响应形式 1数据 2报告',
  `task_status` int(11) DEFAULT NULL COMMENT '任务状态 1初始化,2生成中,3生成成功,4生成失败',
  `data_status` tinyint(2) DEFAULT '0' COMMENT '数据状态 默认0 删除1',
  `process_id` int(11) DEFAULT NULL COMMENT '流程ID',
  `term_rule` longtext COMMENT '流程运行drl脚本',
  `total_score` decimal(10,2) DEFAULT NULL COMMENT '任务最终总分',
  `process_nodes_snapshot` longtext COMMENT '任务执行时的流程节点快照 (JSON数组)',
  PRIMARY KEY (`id`),
  KEY `idx_batch_id` (`batch_id`) USING BTREE,
  KEY `idx_task_record_query` (`dept_id`,`data_status`,`create_time`),
  KEY `idx_task_no` (`task_no`),
  KEY `idx_task_status` (`task_status`)
) ENGINE=InnoDB AUTO_INCREMENT=1196 DEFAULT CHARSET=utf8mb4 COMMENT='模型任务记录表';

-- -----------------------------------------------------------
-- 表: model_task_record_batch
-- 说明: 模型任务批次表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `model_task_record_batch`;
CREATE TABLE `model_task_record_batch` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `batch_no` varchar(255) NOT NULL COMMENT '批次号 (业务唯一标识, 例如UUID)',
  `process_id` int(11) NOT NULL COMMENT '关联的流程ID',
  `application_user` varchar(255) DEFAULT NULL COMMENT '申请用户',
  `user_id` int(11) NOT NULL COMMENT '创建用户ID',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `dept_name` varchar(255) DEFAULT NULL COMMENT '部门名称',
  `business_code` bigint(20) DEFAULT NULL COMMENT '业务场景',
  `process_strategy` varchar(255) DEFAULT NULL COMMENT '流程策略',
  `model_name` varchar(255) DEFAULT NULL COMMENT '模型名称',
  `response_form` tinyint(4) DEFAULT '1' COMMENT '响应形式 1数据 2报告',
  `file_name` varchar(255) DEFAULT NULL COMMENT '上传的文件名',
  `file_url` varchar(500) DEFAULT NULL COMMENT '文件存储路径',
  `batch_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '批次状态:\r\n1待处理,2处理中,3全部成功,4部分失败,5全部失败,6校验失败',
  `total_count` int(11) DEFAULT '0' COMMENT '总任务数',
  `success_count` int(11) DEFAULT '0' COMMENT '成功任务数',
  `fail_count` int(11) DEFAULT '0' COMMENT '失败任务数',
  `error_message` text COMMENT '错误信息(校验失败时记录)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `start_time` datetime DEFAULT NULL COMMENT '开始处理时间',
  `finish_time` datetime DEFAULT NULL COMMENT '处理完成时间',
  `data_status` tinyint(1) DEFAULT '0' COMMENT '数据状态(0-正常, 1-删除)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_batch_no` (`batch_no`) USING BTREE,
  KEY `idx_process_id` (`process_id`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  KEY `idx_user_id` (`application_user`) USING BTREE,
  KEY `idx_batch_status` (`batch_status`) USING BTREE,
  KEY `idx_data_status` (`data_status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='模型任务批次表';

-- -----------------------------------------------------------
-- 表: model_test_task
-- 说明: 模型测试任务表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `model_test_task`;
CREATE TABLE `model_test_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_no` varchar(255) DEFAULT NULL COMMENT '测试任务号',
  `model_id` int(11) DEFAULT NULL COMMENT '模型编号',
  `model_verson` varchar(255) DEFAULT NULL COMMENT '模型版本号',
  `project_code` varchar(255) DEFAULT NULL COMMENT '产品编号',
  `business_code` bigint(20) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` bigint(20) DEFAULT NULL COMMENT '策略类型导航标识',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `test_drl` longtext COMMENT '测试drl文件',
  `test_status` int(11) DEFAULT NULL COMMENT '测试状态  0初始化 1运行中 2成功 3失败',
  `data_status` bigint(2) DEFAULT '0' COMMENT '数据状态  0正常 1删除',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COMMENT='模型测试任务表';

-- -----------------------------------------------------------
-- 表: model_version_classification
-- 说明: 版本归类冠军标识表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `model_version_classification`;
CREATE TABLE `model_version_classification` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `model_id` int(11) DEFAULT NULL COMMENT '模型id',
  `model_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模型名称',
  `version_control` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '版本控制 版本号',
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `data_status` int(11) DEFAULT '0' COMMENT '数据状态',
  `champion_version` int(11) DEFAULT '0' COMMENT '是否为冠军版本标识 1是 0为挑战者版本',
  `new_version` int(11) DEFAULT '0' COMMENT '是否为决策平台展示版本号 1是 0不是',
  `user_version` int(11) DEFAULT '0' COMMENT '当前模型使用版本号',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门id',
  `project_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产品',
  `business_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '策略类型导航标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=216 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='版本归类冠军标识表';

-- -----------------------------------------------------------
-- 表: price_card_radius
-- 说明: 定价详情表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `price_card_radius`;
CREATE TABLE `price_card_radius` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `price_card_id` int(11) DEFAULT NULL COMMENT '关联额度卡id',
  `parent_id` int(11) DEFAULT NULL COMMENT '父id',
  `ancestors` varchar(255) DEFAULT NULL COMMENT '祖级列表',
  `price_range` varchar(255) DEFAULT NULL COMMENT '定价',
  `price_range_content` varchar(255) DEFAULT NULL COMMENT '定价说明',
  `project_code` varchar(255) DEFAULT NULL COMMENT '左侧产品导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '策略类型导航标识',
  `dept_flag` int(11) DEFAULT NULL COMMENT '部门标识 1超级管理员 2普通用户',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `button_state` int(11) DEFAULT NULL COMMENT '按钮状态 1启用 0禁用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user_id` int(11) DEFAULT NULL COMMENT '操作人ID',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `standard_name` varchar(255) DEFAULT NULL COMMENT '评级名称',
  `quota_min` decimal(18,2) DEFAULT NULL COMMENT '额度下限（万元）',
  `quota_max` decimal(18,2) DEFAULT NULL COMMENT '额度上限（万元）',
  `quota_include_min` tinyint(4) DEFAULT '1' COMMENT '是否包含下限：1包含 0不包含',
  `quota_include_max` tinyint(4) DEFAULT '1' COMMENT '是否包含上限：1包含 0不包含',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_price_card_quota` (`price_card_id`,`standard_name`,`quota_min`,`quota_max`)
) ENGINE=InnoDB AUTO_INCREMENT=127 DEFAULT CHARSET=utf8 COMMENT='定价详情表';

-- -----------------------------------------------------------
-- 表: price_card_radius_snapshot
-- 说明: 定价详情表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `price_card_radius_snapshot`;
CREATE TABLE `price_card_radius_snapshot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `price_card_id` int(11) DEFAULT NULL COMMENT '关联额度卡id',
  `parent_id` int(11) DEFAULT NULL COMMENT '父id',
  `ancestors` varchar(255) DEFAULT NULL COMMENT '祖级列表',
  `price_range` varchar(255) DEFAULT NULL COMMENT '定价',
  `price_range_content` varchar(255) DEFAULT NULL COMMENT '定价说明',
  `project_code` varchar(255) DEFAULT NULL COMMENT '左侧产品导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '策略类型导航标识',
  `dept_flag` int(11) DEFAULT NULL COMMENT '部门标识 1超级管理员 2普通用户',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `button_state` int(11) DEFAULT NULL COMMENT '按钮状态 1启用 0禁用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user_id` int(11) DEFAULT NULL COMMENT '操作人ID',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `standard_name` varchar(255) DEFAULT NULL COMMENT '评级名称',
  `quota_min` decimal(18,2) DEFAULT NULL COMMENT '额度下限（万元）',
  `quota_max` decimal(18,2) DEFAULT NULL COMMENT '额度上限（万元）',
  `quota_include_min` tinyint(4) DEFAULT '1' COMMENT '是否包含下限：1包含 0不包含',
  `quota_include_max` tinyint(4) DEFAULT '1' COMMENT '是否包含上限：1包含 0不包含',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_price_card_quota_snapshot` (`price_card_id`,`standard_name`,`quota_min`,`quota_max`)
) ENGINE=InnoDB AUTO_INCREMENT=127 DEFAULT CHARSET=utf8 COMMENT='定价详情表';

-- -----------------------------------------------------------
-- 表: price_card_record
-- 说明: 定价卡主表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `price_card_record`;
CREATE TABLE `price_card_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `quota_card_id` int(11) DEFAULT NULL COMMENT '关联额度卡id',
  `price_card` varchar(255) DEFAULT NULL COMMENT '定价模型名称(对应额度模型名称)',
  `data_state` int(11) DEFAULT NULL COMMENT '数据状态',
  `project_code` varchar(255) DEFAULT NULL COMMENT '左侧产品导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '策略类型导航标识',
  `dept_flag` int(11) DEFAULT NULL COMMENT '部门标识 1超级管理员 2普通用户',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `button_state` int(11) DEFAULT NULL COMMENT '按钮状态 1启用 0禁用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user_id` int(11) DEFAULT NULL COMMENT '操作人ID',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `rate_card_id` int(11) DEFAULT NULL COMMENT '评级卡id',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COMMENT='定价卡主表';

-- -----------------------------------------------------------
-- 表: price_card_record_snapshot
-- 说明: 定价卡主表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `price_card_record_snapshot`;
CREATE TABLE `price_card_record_snapshot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `quota_card_id` int(11) DEFAULT NULL COMMENT '关联额度卡id',
  `price_card` varchar(255) DEFAULT NULL COMMENT '定价模型名称(对应额度模型名称)',
  `data_state` int(11) DEFAULT NULL COMMENT '数据状态',
  `project_code` varchar(255) DEFAULT NULL COMMENT '左侧产品导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '策略类型导航标识',
  `dept_flag` int(11) DEFAULT NULL COMMENT '部门标识 1超级管理员 2普通用户',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `button_state` int(11) DEFAULT NULL COMMENT '按钮状态 1启用 0禁用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user_id` int(11) DEFAULT NULL COMMENT '操作人ID',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `rate_card_id` int(11) DEFAULT NULL COMMENT '评级卡id',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='定价卡主表';

-- -----------------------------------------------------------
-- 表: process_node
-- 说明: 流程节点表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `process_node`;
CREATE TABLE `process_node` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `process_strategy_id` int(11) DEFAULT NULL COMMENT '流程策略表id',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '策略导航标识',
  `node_id` int(11) DEFAULT NULL COMMENT '节点标识',
  `module_id` int(11) DEFAULT NULL COMMENT '模块id  1:评分 2:评级 3:额度 4:定价 5:规则 6:分类',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `approval_name` varchar(255) DEFAULT NULL COMMENT '审批人',
  `approval_user_id` int(11) DEFAULT NULL COMMENT '审批人id',
  `data_status` int(11) DEFAULT NULL COMMENT '逻辑删除',
  `rule_name` varchar(255) DEFAULT NULL COMMENT '策略导航名称',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `process_strategy_id` (`process_strategy_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=203 DEFAULT CHARSET=utf8 COMMENT='流程节点表';

-- -----------------------------------------------------------
-- 表: process_node_result
-- 说明: 流程策略节点结果表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `process_node_result`;
CREATE TABLE `process_node_result` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `process_id` int(11) DEFAULT NULL COMMENT '流程策略 id',
  `process_node_id` int(11) DEFAULT NULL COMMENT '策略节点 id',
  `result` varchar(500) DEFAULT NULL COMMENT '审批结果',
  `opinion` varchar(500) DEFAULT NULL COMMENT '审批意见',
  `detail` longtext,
  `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `approval_name` varchar(255) DEFAULT NULL COMMENT '审批人',
  `approval_user_id` int(11) DEFAULT NULL COMMENT '审批人id',
  `approval_status` int(11) DEFAULT NULL COMMENT '审批状态',
  `rule_code` int(11) DEFAULT NULL COMMENT '策略导航标识',
  `return_data` longtext,
  `node_id` int(11) DEFAULT NULL COMMENT '节点标识',
  `rule_name` varchar(255) DEFAULT NULL COMMENT '策略导航名称',
  `module_id` int(11) DEFAULT NULL COMMENT '模块id  1:评分 2:评级 3:额度 4:定价 5:规则 6:分类',
  `task_id` int(11) DEFAULT NULL COMMENT '任务id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='流程策略节点结果表 ';

-- -----------------------------------------------------------
-- 表: process_policy
-- 说明: 流程策略表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `process_policy`;
CREATE TABLE `process_policy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `process_strategy` varchar(255) DEFAULT NULL COMMENT '流程策略模型名称',
  `product_name` varchar(255) DEFAULT NULL COMMENT '产品名称',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景',
  `use_if` int(11) DEFAULT NULL COMMENT '是否使用 0关闭,1开启',
  `content` varchar(255) DEFAULT NULL COMMENT '描述',
  `application_time` datetime DEFAULT NULL COMMENT '申请时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `data_status` int(11) DEFAULT NULL COMMENT '逻辑删除',
  `product_id` int(11) DEFAULT NULL COMMENT '产品id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `id` (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8 COMMENT='流程策略表';

-- -----------------------------------------------------------
-- 表: process_policy_task
-- 说明: 流程策略任务表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `process_policy_task`;
CREATE TABLE `process_policy_task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_number` varchar(255) DEFAULT NULL COMMENT '任务编号',
  `process_strategy_id` int(11) DEFAULT NULL COMMENT '流程策略模型id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `node_id` int(11) DEFAULT NULL COMMENT '目前所处节点标识',
  `enterprise_name` varchar(255) DEFAULT NULL COMMENT '企业名称',
  `mobile_phone` varchar(255) DEFAULT NULL COMMENT '手机号',
  `id_number` varchar(255) DEFAULT NULL COMMENT '身份证号码',
  `personal_name` varchar(255) DEFAULT NULL COMMENT '个人名称',
  `apply_user_name` varchar(255) DEFAULT NULL COMMENT '申请用户',
  `apply_user_id` int(11) DEFAULT NULL COMMENT '申请用户id',
  `ckey` varchar(255) DEFAULT NULL COMMENT '企业标识',
  `flag` varchar(255) DEFAULT NULL COMMENT '个人标识0 企业标识 1',
  `enterprise_score` varchar(255) DEFAULT NULL COMMENT '企业得分',
  `credit_status` int(2) DEFAULT NULL COMMENT '授信状态',
  `credit_time` datetime DEFAULT NULL,
  `error_msg` varchar(255) DEFAULT NULL COMMENT '授信失败提示',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `process_strategy_id` (`process_strategy_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='流程策略任务表';

-- -----------------------------------------------------------
-- 表: product
-- 说明: 产品表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(255) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `dept_id` bigint(20) NOT NULL,
  `dept_flag` tinyint(4) NOT NULL DEFAULT '2' COMMENT '1-标准产品，2-自建产品',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `data_status` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COMMENT='产品表';

-- -----------------------------------------------------------
-- 表: quota_card_radius
-- 说明: 额度范围表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `quota_card_radius`;
CREATE TABLE `quota_card_radius` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `quota_card_id` int(11) DEFAULT NULL COMMENT '关联额度卡id',
  `parent_id` int(11) DEFAULT NULL COMMENT '父id',
  `ancestors` varchar(255) DEFAULT NULL COMMENT '祖级列表',
  `standard_rate` varchar(255) DEFAULT NULL COMMENT '标准评级',
  `credit_source` varchar(255) DEFAULT NULL COMMENT '标准额度测算来源',
  `quota_range` varchar(255) DEFAULT NULL COMMENT '额度浮动范围',
  `quota_range_content` varchar(255) DEFAULT NULL COMMENT '额度浮动范围说明',
  `project_code` varchar(255) DEFAULT NULL COMMENT '左侧产品导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '策略类型导航标识',
  `dept_flag` int(11) DEFAULT NULL COMMENT '部门标识 1超级管理员 2普通用户',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `button_state` int(11) DEFAULT NULL COMMENT '按钮状态 1启用 0禁用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user_id` int(11) DEFAULT NULL COMMENT '操作人ID',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `standard_name` varchar(255) DEFAULT NULL COMMENT '额度评级',
  `term_rule` text COMMENT '分析指标生成的DRL规则代码',
  `data_module` text COMMENT '分析指标依赖的接口列表JSON',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=177 DEFAULT CHARSET=utf8 COMMENT='额度范围表';

-- -----------------------------------------------------------
-- 表: quota_card_radius_snapshot
-- 说明: 额度范围表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `quota_card_radius_snapshot`;
CREATE TABLE `quota_card_radius_snapshot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `quota_card_id` int(11) DEFAULT NULL COMMENT '关联额度卡id',
  `parent_id` int(11) DEFAULT NULL COMMENT '父id',
  `ancestors` varchar(255) DEFAULT NULL COMMENT '祖级列表',
  `standard_rate` varchar(255) DEFAULT NULL COMMENT '标准评级',
  `credit_source` varchar(255) DEFAULT NULL COMMENT '标准额度测算来源',
  `quota_range` varchar(255) DEFAULT NULL COMMENT '额度浮动范围',
  `quota_range_content` varchar(255) DEFAULT NULL COMMENT '额度浮动范围说明',
  `project_code` varchar(255) DEFAULT NULL COMMENT '左侧产品导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '策略类型导航标识',
  `dept_flag` int(11) DEFAULT NULL COMMENT '部门标识 1超级管理员 2普通用户',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `button_state` int(11) DEFAULT NULL COMMENT '按钮状态 1启用 0禁用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user_id` int(11) DEFAULT NULL COMMENT '操作人ID',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `standard_name` varchar(255) DEFAULT NULL COMMENT '额度评级',
  `term_rule` text COMMENT '分析指标生成的DRL规则代码',
  `data_module` text COMMENT '分析指标依赖的接口列表JSON',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=168 DEFAULT CHARSET=utf8 COMMENT='额度范围表';

-- -----------------------------------------------------------
-- 表: quota_card_record
-- 说明: 额度卡主表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `quota_card_record`;
CREATE TABLE `quota_card_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rate_card_id` int(11) DEFAULT NULL COMMENT '关联评级卡id',
  `quota_card` varchar(255) DEFAULT NULL COMMENT '额度模型名称(对应评级卡名称)',
  `data_state` int(11) DEFAULT NULL COMMENT '数据状态',
  `project_code` varchar(255) DEFAULT NULL COMMENT '左侧产品导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '策略类型导航标识',
  `dept_flag` int(11) DEFAULT NULL COMMENT '部门标识 1超级管理员 2普通用户',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `button_state` int(11) DEFAULT NULL COMMENT '按钮状态 1启用 0禁用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user_id` int(11) DEFAULT NULL COMMENT '操作人ID',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `standard_quota_formula` text COMMENT '计算公式',
  `standard_quota_formula_zh` text COMMENT '中文公式',
  `formula_variables` json DEFAULT NULL COMMENT '公式变量映射JSON',
  `selected_fields` json DEFAULT NULL COMMENT '已选字段列表JSON',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_project_business_rule` (`project_code`,`business_code`,`rule_code`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COMMENT='额度卡主表';

-- -----------------------------------------------------------
-- 表: quota_card_record_snapshot
-- 说明: 额度卡主表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `quota_card_record_snapshot`;
CREATE TABLE `quota_card_record_snapshot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rate_card_id` int(11) DEFAULT NULL COMMENT '关联评级卡id',
  `quota_card` varchar(255) DEFAULT NULL COMMENT '额度模型名称(对应评级卡名称)',
  `data_state` int(11) DEFAULT NULL COMMENT '数据状态',
  `project_code` varchar(255) DEFAULT NULL COMMENT '左侧产品导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '策略类型导航标识',
  `dept_flag` int(11) DEFAULT NULL COMMENT '部门标识 1超级管理员 2普通用户',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `button_state` int(11) DEFAULT NULL COMMENT '按钮状态 1启用 0禁用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user_id` int(11) DEFAULT NULL COMMENT '操作人ID',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `standard_quota_formula` text COMMENT '计算公式',
  `standard_quota_formula_zh` text COMMENT '中文公式',
  `formula_variables` json DEFAULT NULL COMMENT '公式变量映射JSON',
  `selected_fields` json DEFAULT NULL COMMENT '已选字段列表JSON',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COMMENT='额度卡主表';

-- -----------------------------------------------------------
-- 表: quota_enterprise
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `quota_enterprise`;
CREATE TABLE `quota_enterprise` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rate` varchar(255) DEFAULT NULL COMMENT '评级',
  `benchmark_limit` varchar(255) DEFAULT NULL COMMENT '基准额度',
  `under700` double(11,2) DEFAULT NULL COMMENT '700分以下',
  `between700750` double(11,2) DEFAULT NULL COMMENT '700~750',
  `between750800` double(11,2) DEFAULT NULL COMMENT '750~800',
  `between800850` double(11,2) DEFAULT NULL COMMENT '800~850',
  `between850900` double(11,2) DEFAULT NULL COMMENT '850~900',
  `between900950` double(11,2) DEFAULT NULL COMMENT '900~950',
  `between9501000` double(11,2) DEFAULT NULL COMMENT '950~1000',
  `up1000` double(11,2) DEFAULT NULL COMMENT '1000以上',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------------
-- 表: rate_card_radius
-- 说明: 评级范围表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rate_card_radius`;
CREATE TABLE `rate_card_radius` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rate_card_id` int(11) DEFAULT NULL COMMENT '关联评级卡id',
  `parent_id` int(11) DEFAULT NULL COMMENT '父id',
  `ancestors` varchar(255) DEFAULT NULL COMMENT '祖级列表',
  `standard_rate` varchar(255) DEFAULT NULL COMMENT '标准评级',
  `credit_source` varchar(255) DEFAULT NULL COMMENT '标准额度测算来源',
  `rate_range` varchar(255) DEFAULT NULL COMMENT '评分范围',
  `rate_range_content` varchar(255) DEFAULT NULL COMMENT '评分范围说明',
  `project_code` varchar(255) DEFAULT NULL COMMENT '左侧产品导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '策略类型导航标识',
  `dept_flag` int(11) DEFAULT NULL COMMENT '部门标识 1超级管理员 2普通用户',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `button_state` int(11) DEFAULT NULL COMMENT '按钮状态 1启用 0禁用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user_id` int(11) DEFAULT NULL COMMENT '操作人ID',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8 COMMENT='评级范围表';

-- -----------------------------------------------------------
-- 表: rate_card_radius_snapshot
-- 说明: 评级范围表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rate_card_radius_snapshot`;
CREATE TABLE `rate_card_radius_snapshot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rate_card_id` int(11) DEFAULT NULL COMMENT '关联评级卡id',
  `parent_id` int(11) DEFAULT NULL COMMENT '父id',
  `ancestors` varchar(255) DEFAULT NULL COMMENT '祖级列表',
  `standard_rate` varchar(255) DEFAULT NULL COMMENT '标准评级',
  `credit_source` varchar(255) DEFAULT NULL COMMENT '标准额度测算来源',
  `rate_range` varchar(255) DEFAULT NULL COMMENT '评分范围',
  `rate_range_content` varchar(255) DEFAULT NULL COMMENT '评分范围说明',
  `project_code` varchar(255) DEFAULT NULL COMMENT '左侧产品导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '策略类型导航标识',
  `dept_flag` int(11) DEFAULT NULL COMMENT '部门标识 1超级管理员 2普通用户',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `button_state` int(11) DEFAULT NULL COMMENT '按钮状态 1启用 0禁用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user_id` int(11) DEFAULT NULL COMMENT '操作人ID',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8 COMMENT='评级范围表';

-- -----------------------------------------------------------
-- 表: rate_card_record
-- 说明: 评级卡主表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rate_card_record`;
CREATE TABLE `rate_card_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `score_card_id` int(11) DEFAULT NULL COMMENT '关联评分卡id',
  `rate_card` varchar(255) DEFAULT NULL COMMENT '评级卡名称(对应评分卡名称)',
  `data_state` int(11) DEFAULT NULL COMMENT '数据状态',
  `project_code` varchar(255) DEFAULT NULL COMMENT '左侧产品导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '策略类型导航标识',
  `dept_flag` int(11) DEFAULT NULL COMMENT '部门标识 1超级管理员 2普通用户',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `button_state` int(11) DEFAULT NULL COMMENT '按钮状态 1启用 0禁用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user_id` int(11) DEFAULT NULL COMMENT '操作人ID',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8 COMMENT='评级卡主表';

-- -----------------------------------------------------------
-- 表: rate_card_record_snapshot
-- 说明: 评级卡主表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rate_card_record_snapshot`;
CREATE TABLE `rate_card_record_snapshot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `score_card_id` int(11) DEFAULT NULL COMMENT '关联评分卡id',
  `rate_card` varchar(255) DEFAULT NULL COMMENT '评级卡名称(对应评分卡名称)',
  `data_state` int(11) DEFAULT NULL COMMENT '数据状态',
  `project_code` varchar(255) DEFAULT NULL COMMENT '左侧产品导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '策略类型导航标识',
  `dept_flag` int(11) DEFAULT NULL COMMENT '部门标识 1超级管理员 2普通用户',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `button_state` int(11) DEFAULT NULL COMMENT '按钮状态 1启用 0禁用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user_id` int(11) DEFAULT NULL COMMENT '操作人ID',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8 COMMENT='评级卡主表';

-- -----------------------------------------------------------
-- 表: rde_model_anti_fraud
-- 说明: 反欺诈模型表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rde_model_anti_fraud`;
CREATE TABLE `rde_model_anti_fraud` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(500) DEFAULT '' COMMENT '名称',
  `descr` varchar(500) DEFAULT '' COMMENT '描述',
  `model_type` varchar(10) DEFAULT '1' COMMENT '模型类型：1.银行流水,2.kyc报告，3风险预警。。。',
  `status` varchar(30) DEFAULT '1' COMMENT '状态1启用 0禁用',
  `check_status` tinyint(3) DEFAULT '0' COMMENT '验证状态（0未验证，1已验证，2验证失败）',
  `new_rule` text COMMENT '新规则',
  `run_rule` text COMMENT '运行规则',
  `rule_type` int(11) DEFAULT NULL COMMENT '规则类型（1反欺诈模型 2申请模型3准入模型 4黑灰名单模型 5评分模型 6财务测算模型7额度测算模型 8风险定价模型 9贷后监控模型 10违约预测模型 11宏观预测模型 12行业预测模型 13地域风险模型）-AI风险预警新定义类型（1自身风险，2法人风险，3舆情风险，4关联风险）',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_user_id` int(11) DEFAULT '0' COMMENT '操作人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `dept_id` int(11) DEFAULT '0' COMMENT '公司id',
  `data_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '数据状态 0是正常，1删除',
  `dept_flag` varchar(255) DEFAULT NULL COMMENT '部门标识101为1，其他为2',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '策略类型导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `project_code` varchar(255) DEFAULT NULL COMMENT '模块标识\n,银行流水1001，支付流水1002    左侧产品导航标识',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=123 DEFAULT CHARSET=utf8mb4 COMMENT='反欺诈模型表';

-- -----------------------------------------------------------
-- 表: rde_model_anti_fraud_rule_group
-- 说明: 反欺诈模型规则组表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rde_model_anti_fraud_rule_group`;
CREATE TABLE `rde_model_anti_fraud_rule_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `model_id` int(11) DEFAULT '0' COMMENT '模型ID',
  `name` varchar(500) DEFAULT '' COMMENT '名称',
  `descr` varchar(500) DEFAULT '' COMMENT '描述',
  `status` varchar(1) DEFAULT '1' COMMENT '状态',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_user_id` int(11) DEFAULT '0' COMMENT '操作人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `data_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '数据状态',
  `dept_id` int(11) DEFAULT NULL COMMENT '公司id',
  `dept_flag` varchar(255) DEFAULT NULL COMMENT '部门标识101为1，其他为2',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '分类为1,规则为0 策略类型导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `project_code` varchar(255) DEFAULT NULL COMMENT '模块标识\n,银行流水1001，支付流水1002  左侧产品导航标识',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=128 DEFAULT CHARSET=utf8mb4 COMMENT='反欺诈模型规则组表';

-- -----------------------------------------------------------
-- 表: rde_model_anti_fraud_rule_group_snapshot
-- 说明: 反欺诈模型规则组表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rde_model_anti_fraud_rule_group_snapshot`;
CREATE TABLE `rde_model_anti_fraud_rule_group_snapshot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `model_id` int(11) DEFAULT '0' COMMENT '模型ID',
  `name` varchar(500) DEFAULT '' COMMENT '名称',
  `descr` varchar(500) DEFAULT '' COMMENT '描述',
  `status` varchar(1) DEFAULT '1' COMMENT '状态',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_user_id` int(11) DEFAULT '0' COMMENT '操作人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `data_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '数据状态',
  `dept_id` int(11) DEFAULT NULL COMMENT '公司id',
  `dept_flag` varchar(255) DEFAULT NULL COMMENT '部门标识101为1，其他为2',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '分类为1,规则为0 策略类型导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `project_code` varchar(255) DEFAULT NULL COMMENT '模块标识\n,银行流水1001，支付流水1002  左侧产品导航标识',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=127 DEFAULT CHARSET=utf8mb4 COMMENT='反欺诈模型规则组表';

-- -----------------------------------------------------------
-- 表: rde_model_anti_fraud_rule_group_version
-- 说明: 反欺诈模型规则组表 -- 版本控制
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rde_model_anti_fraud_rule_group_version`;
CREATE TABLE `rde_model_anti_fraud_rule_group_version` (
  `id` int(11) NOT NULL,
  `model_id` int(11) DEFAULT '0' COMMENT '模型ID',
  `name` varchar(500) DEFAULT '' COMMENT '名称',
  `descr` varchar(500) DEFAULT '' COMMENT '描述',
  `status` varchar(1) DEFAULT '1' COMMENT '状态',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_user_id` int(11) DEFAULT '0' COMMENT '操作人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `data_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '数据状态',
  `dept_id` int(11) DEFAULT NULL COMMENT '公司id',
  `dept_flag` varchar(255) DEFAULT NULL COMMENT '部门标识101为1，其他为2',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '分类为1,规则为0 策略类型导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `project_code` varchar(255) DEFAULT NULL COMMENT '模块标识\n,银行流水1001，支付流水1002  左侧产品导航标识',
  `version_control` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '版本控制'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='反欺诈模型规则组表 -- 版本控制';

-- -----------------------------------------------------------
-- 表: rde_model_anti_fraud_rule_record
-- 说明: 反欺诈模型规则明细表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rde_model_anti_fraud_rule_record`;
CREATE TABLE `rde_model_anti_fraud_rule_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) DEFAULT '0' COMMENT '组ID',
  `code_id` int(11) DEFAULT '0' COMMENT '规则codeId',
  `model_id` int(11) DEFAULT '0' COMMENT '模型id',
  `code` varchar(200) DEFAULT '' COMMENT '标识',
  `name` varchar(500) DEFAULT '' COMMENT '名称',
  `descr` varchar(500) DEFAULT '' COMMENT '描述',
  `control` varchar(2) DEFAULT '' COMMENT '控制力度',
  `status` varchar(1) DEFAULT '1' COMMENT '状态',
  `term` varchar(500) DEFAULT '' COMMENT '条件',
  `term_package` varchar(1000) DEFAULT NULL COMMENT '规则所用的包',
  `term_key` varchar(500) DEFAULT NULL COMMENT '处理所需别名',
  `term_value` varchar(500) DEFAULT '' COMMENT '处理所需code',
  `term_other` varchar(500) DEFAULT NULL COMMENT '处理所需其他条件',
  `term_rule` varchar(1000) DEFAULT NULL COMMENT '生成规则',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_user_id` int(11) DEFAULT '0' COMMENT '操作人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `data_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '数据状态',
  `dept_id` int(11) DEFAULT NULL COMMENT '公司id',
  `dept_flag` varchar(255) DEFAULT NULL COMMENT '部门标识101为1，其他为2',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '分类为1,规则为0 策略类型导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `project_code` varchar(255) DEFAULT NULL COMMENT '模块标识\n,银行流水1001，支付流水1002 左侧产品导航标识',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `data_module` varchar(255) DEFAULT NULL COMMENT '数据模块',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_r_data_status_group_id_dept_id_project_code` (`data_status`,`group_id`,`dept_id`,`project_code`),
  KEY `idx_r_update_time` (`update_time`),
  KEY `idx_r_group_id_update_time` (`group_id`,`data_status`,`dept_id`,`project_code`,`update_time`),
  KEY `idx_r_full_coverage` (`data_status`,`group_id`,`dept_id`,`project_code`,`code_id`,`status`,`update_time`,`dept_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=192 DEFAULT CHARSET=utf8mb4 COMMENT='反欺诈模型规则明细表';

-- -----------------------------------------------------------
-- 表: rde_model_anti_fraud_rule_record_snapshot
-- 说明: 反欺诈模型规则明细表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rde_model_anti_fraud_rule_record_snapshot`;
CREATE TABLE `rde_model_anti_fraud_rule_record_snapshot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_id` int(11) DEFAULT '0' COMMENT '组ID',
  `code_id` int(11) DEFAULT '0' COMMENT '规则codeId',
  `model_id` int(11) DEFAULT '0' COMMENT '模型id',
  `code` varchar(200) DEFAULT '' COMMENT '标识',
  `name` varchar(500) DEFAULT '' COMMENT '名称',
  `descr` varchar(500) DEFAULT '' COMMENT '描述',
  `control` varchar(2) DEFAULT '' COMMENT '控制力度',
  `status` varchar(1) DEFAULT '1' COMMENT '状态',
  `term` varchar(500) DEFAULT '' COMMENT '条件',
  `term_package` varchar(1000) DEFAULT NULL COMMENT '规则所用的包',
  `term_key` varchar(500) DEFAULT NULL COMMENT '处理所需别名',
  `term_value` varchar(500) DEFAULT '' COMMENT '处理所需code',
  `term_other` varchar(500) DEFAULT NULL COMMENT '处理所需其他条件',
  `term_rule` varchar(1000) DEFAULT NULL COMMENT '生成规则',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_user_id` int(11) DEFAULT '0' COMMENT '操作人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `data_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '数据状态',
  `dept_id` int(11) DEFAULT NULL COMMENT '公司id',
  `dept_flag` varchar(255) DEFAULT NULL COMMENT '部门标识101为1，其他为2',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '分类为1,规则为0 策略类型导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `project_code` varchar(255) DEFAULT NULL COMMENT '模块标识\n,银行流水1001，支付流水1002 左侧产品导航标识',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `data_module` varchar(255) DEFAULT NULL COMMENT '数据模块',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=190 DEFAULT CHARSET=utf8mb4 COMMENT='反欺诈模型规则明细表';

-- -----------------------------------------------------------
-- 表: rde_model_anti_fraud_rule_record_version
-- 说明: 反欺诈模型规则明细表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rde_model_anti_fraud_rule_record_version`;
CREATE TABLE `rde_model_anti_fraud_rule_record_version` (
  `id` int(11) NOT NULL,
  `group_id` int(11) DEFAULT '0' COMMENT '组ID',
  `code_id` int(11) DEFAULT '0' COMMENT '规则codeId',
  `model_id` int(11) DEFAULT '0' COMMENT '模型id',
  `code` varchar(200) DEFAULT '' COMMENT '标识',
  `name` varchar(500) DEFAULT '' COMMENT '名称',
  `descr` varchar(500) DEFAULT '' COMMENT '描述',
  `control` varchar(2) DEFAULT '' COMMENT '控制力度',
  `status` varchar(1) DEFAULT '1' COMMENT '状态',
  `term` varchar(500) DEFAULT '' COMMENT '条件',
  `term_package` varchar(1000) DEFAULT NULL COMMENT '规则所用的包',
  `term_key` varchar(500) DEFAULT NULL COMMENT '处理所需别名',
  `term_value` varchar(500) DEFAULT '' COMMENT '处理所需code',
  `term_other` varchar(500) DEFAULT NULL COMMENT '处理所需其他条件',
  `term_rule` varchar(1000) DEFAULT NULL COMMENT '生成规则',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_user_id` int(11) DEFAULT '0' COMMENT '操作人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `data_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '数据状态',
  `dept_id` int(11) DEFAULT NULL COMMENT '公司id',
  `dept_flag` varchar(255) DEFAULT NULL COMMENT '部门标识101为1，其他为2',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '分类为1,规则为0 策略类型导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `project_code` varchar(255) DEFAULT NULL COMMENT '模块标识\n,银行流水1001，支付流水1002 左侧产品导航标识',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `data_module` varchar(255) DEFAULT NULL COMMENT '数据模块',
  KEY `idx_r_data_status_group_id_dept_id_project_code` (`data_status`,`group_id`,`dept_id`,`project_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='反欺诈模型规则明细表';

-- -----------------------------------------------------------
-- 表: rde_model_anti_fraud_snapshot
-- 说明: 反欺诈模型表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rde_model_anti_fraud_snapshot`;
CREATE TABLE `rde_model_anti_fraud_snapshot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(500) DEFAULT '' COMMENT '名称',
  `descr` varchar(500) DEFAULT '' COMMENT '描述',
  `model_type` varchar(10) DEFAULT '1' COMMENT '模型类型：1.银行流水,2.kyc报告，3风险预警。。。',
  `status` varchar(30) DEFAULT '1' COMMENT '状态1启用 0禁用',
  `check_status` tinyint(3) DEFAULT '0' COMMENT '验证状态（0未验证，1已验证，2验证失败）',
  `new_rule` text COMMENT '新规则',
  `run_rule` text COMMENT '运行规则',
  `rule_type` int(11) DEFAULT NULL COMMENT '规则类型（1反欺诈模型 2申请模型3准入模型 4黑灰名单模型 5评分模型 6财务测算模型7额度测算模型 8风险定价模型 9贷后监控模型 10违约预测模型 11宏观预测模型 12行业预测模型 13地域风险模型）-AI风险预警新定义类型（1自身风险，2法人风险，3舆情风险，4关联风险）',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_user_id` int(11) DEFAULT '0' COMMENT '操作人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `dept_id` int(11) DEFAULT '0' COMMENT '公司id',
  `data_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '数据状态 0是正常，1删除',
  `dept_flag` varchar(255) DEFAULT NULL COMMENT '部门标识101为1，其他为2',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '分类为1,规则为0  策略类型导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `project_code` varchar(255) DEFAULT NULL COMMENT '模块标识\n,银行流水1001，支付流水1002    左侧产品导航标识',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=120 DEFAULT CHARSET=utf8mb4 COMMENT='反欺诈模型表';

-- -----------------------------------------------------------
-- 表: rde_model_anti_fraud_version
-- 说明: 反欺诈模型表 -- 版本控制
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rde_model_anti_fraud_version`;
CREATE TABLE `rde_model_anti_fraud_version` (
  `id` int(11) NOT NULL,
  `name` varchar(500) DEFAULT '' COMMENT '名称',
  `descr` varchar(500) DEFAULT '' COMMENT '描述',
  `model_type` varchar(10) DEFAULT '1' COMMENT '模型类型：1.银行流水,2.kyc报告，3风险预警。。。',
  `status` varchar(30) DEFAULT '1' COMMENT '状态1启用 0禁用',
  `check_status` tinyint(4) DEFAULT '0' COMMENT '验证状态（0未验证，1已验证，2验证失败）',
  `new_rule` text COMMENT '新规则',
  `run_rule` text COMMENT '运行规则',
  `rule_type` int(11) DEFAULT NULL COMMENT '规则类型（1反欺诈模型 2申请模型3准入模型 4黑灰名单模型 5评分模型 6财务测算模型7额度测算模型 8风险定价模型 9贷后监控模型 10违约预测模型 11宏观预测模型 12行业预测模型 13地域风险模型）-AI风险预警新定义类型（1自身风险，2法人风险，3舆情风险，4关联风险）',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_user_id` int(11) DEFAULT '0' COMMENT '操作人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `dept_id` int(11) DEFAULT '0' COMMENT '公司id',
  `data_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '数据状态 0是正常，1删除',
  `dept_flag` varchar(255) DEFAULT NULL COMMENT '部门标识101为1，其他为2',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '分类为1,规则为0  策略类型导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `project_code` varchar(255) DEFAULT NULL COMMENT '模块标识\n,银行流水1001，支付流水1002    左侧产品导航标识',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='反欺诈模型表 -- 版本控制';

-- -----------------------------------------------------------
-- 表: rde_model_anti_project_fraud
-- 说明: 决策引擎项目-模型关联表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rde_model_anti_project_fraud`;
CREATE TABLE `rde_model_anti_project_fraud` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_code` varchar(30) DEFAULT '' COMMENT '项目模块code，1001：银行流水',
  `model_id` int(11) DEFAULT NULL COMMENT '模型id',
  `group_id` int(11) DEFAULT NULL COMMENT '规则组id',
  `rule_id` int(11) DEFAULT NULL COMMENT '规则id',
  `remark` varchar(200) DEFAULT '' COMMENT '备注',
  `user_id` int(11) DEFAULT NULL COMMENT '暂时不用，当前更新人',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门id 后期存储机构id，根据机构做数据隔离',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `data_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '数据状态',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '策略模型标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='决策引擎项目-模型关联表';

-- -----------------------------------------------------------
-- 表: rde_model_anti_project_fraud_snapshot
-- 说明: 决策引擎项目-模型关联表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rde_model_anti_project_fraud_snapshot`;
CREATE TABLE `rde_model_anti_project_fraud_snapshot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_code` varchar(30) DEFAULT '' COMMENT '项目模块code，1001：银行流水',
  `model_id` int(11) DEFAULT NULL COMMENT '模型id',
  `group_id` int(11) DEFAULT NULL COMMENT '规则组id',
  `rule_id` int(11) DEFAULT NULL COMMENT '规则id',
  `remark` varchar(200) DEFAULT '' COMMENT '备注',
  `user_id` int(11) DEFAULT NULL COMMENT '暂时不用，当前更新人',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门id 后期存储机构id，根据机构做数据隔离',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `data_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '数据状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='决策引擎项目-模型关联表';

-- -----------------------------------------------------------
-- 表: rde_model_decision_code_level
-- 说明: 决策code登记表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rde_model_decision_code_level`;
CREATE TABLE `rde_model_decision_code_level` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(200) DEFAULT '' COMMENT 'code',
  `level` varchar(255) DEFAULT '' COMMENT '等级',
  `type` int(11) DEFAULT '0' COMMENT '类型，0默认评分策略，1风险预警策略，2贷前准入策略,3银行流水预警策略',
  `risk_type` int(11) DEFAULT '0' COMMENT '风险分类',
  `content` varchar(255) DEFAULT '' COMMENT '说明',
  `risk_description` varchar(255) DEFAULT '' COMMENT '话术',
  `sort_num` int(11) DEFAULT '0' COMMENT '排序',
  `start_rate` decimal(12,2) DEFAULT NULL COMMENT '开始比例%',
  `end_rate` decimal(12,2) DEFAULT NULL COMMENT '结束比例%',
  `status` varchar(1) CHARACTER SET utf8mb4 DEFAULT '1' COMMENT '状态 0 未开放 1 已开放',
  `term` varchar(500) CHARACTER SET utf8mb4 DEFAULT '' COMMENT '条件',
  `term_package` varchar(1000) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '规则所用的包',
  `term_key` varchar(500) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '处理所需别名',
  `term_value` varchar(500) CHARACTER SET utf8mb4 DEFAULT '' COMMENT '处理所需code',
  `term_other` varchar(500) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '处理所需其他条件',
  `term_rule` longtext,
  `remark` varchar(255) DEFAULT '' COMMENT '备注',
  `update_by` varchar(200) DEFAULT '' COMMENT '更新人',
  `create_by` varchar(200) DEFAULT '' COMMENT '更新名称',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `dept_id` int(11) DEFAULT '100' COMMENT '公司id',
  `data_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '数据状态',
  `dept_flag` varchar(255) DEFAULT NULL COMMENT '部门标识101为1，其他为2',
  `project_code` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '模块标识\n,银行流水1001，支付流水1002 左侧产品导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '分类为1,规则为0 策略类型导航标识',
  `purpose_category` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '分类名称',
  `category_type` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '流水性质',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `data_module` longtext,
  `conditions` longtext COMMENT '判断条件',
  `strongly_reject` varchar(2) DEFAULT NULL COMMENT '是否强拒绝1是0否',
  `quato_rate` varchar(255) DEFAULT NULL,
  `transfer_to_person` varchar(2) NOT NULL DEFAULT '0' COMMENT '是否转人工 1是 0 否',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_name` (`code`) USING BTREE,
  KEY `idx_l_project_code_data_status` (`project_code`,`data_status`),
  KEY `idx_l_full_coverage` (`id`,`project_code`,`data_status`,`code`,`content`,`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=198 DEFAULT CHARSET=utf8 COMMENT='决策code登记表';

-- -----------------------------------------------------------
-- 表: rde_model_decision_code_level_snapshot
-- 说明: 决策code登记表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rde_model_decision_code_level_snapshot`;
CREATE TABLE `rde_model_decision_code_level_snapshot` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` varchar(200) DEFAULT '' COMMENT 'code',
  `level` varchar(255) DEFAULT '' COMMENT '等级',
  `type` int(11) DEFAULT '0' COMMENT '类型，0默认评分策略，1风险预警策略，2贷前准入策略,3银行流水预警策略',
  `risk_type` int(11) DEFAULT '0' COMMENT '风险分类，1运营商风险，2欺诈风险，3身份认证风险，4失信被执行风险，5.黑名单风险，6多头风险，7.交易行为风险 ，8.催收风险 , 9.法人年龄 , 10.法人性别 , 11.关联人潜在风险 ,  12.特殊名单 , 13.借贷意向 , 14.经营年限 , 15.自然人股东占比 , 16.潜在经营性风险 , 17.工商变更-法人变更 , 18.工商变更-高管变更  ,19.行政处罚 , 20.环保处罚 , 21.经营异常 , 22.法院公告  , 23严重违法 , 24.终本案件 , 25.成为箱讯客户时长 , 26.货物品类波动变化水平 , 26.货物路线波动变化水平',
  `content` varchar(255) DEFAULT '' COMMENT '说明',
  `risk_description` varchar(255) DEFAULT '' COMMENT '话术',
  `sort_num` int(11) DEFAULT '0' COMMENT '排序',
  `start_rate` decimal(12,2) DEFAULT NULL COMMENT '开始比例%',
  `end_rate` decimal(12,2) DEFAULT NULL COMMENT '结束比例%',
  `status` varchar(1) CHARACTER SET utf8mb4 DEFAULT '1' COMMENT '状态 0 未开放 1 已开放',
  `term` varchar(500) CHARACTER SET utf8mb4 DEFAULT '' COMMENT '条件',
  `term_package` varchar(1000) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '规则所用的包',
  `term_key` varchar(500) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '处理所需别名',
  `term_value` varchar(500) CHARACTER SET utf8mb4 DEFAULT '' COMMENT '处理所需code',
  `term_other` varchar(500) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '处理所需其他条件',
  `term_rule` longtext CHARACTER SET utf8mb4 COMMENT '生成规则',
  `remark` varchar(255) DEFAULT '' COMMENT '备注',
  `update_by` varchar(200) DEFAULT '' COMMENT '更新人',
  `create_by` varchar(200) DEFAULT '' COMMENT '更新名称',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `dept_id` int(11) DEFAULT '100' COMMENT '公司id',
  `data_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '数据状态',
  `dept_flag` varchar(255) DEFAULT NULL COMMENT '部门标识101为1，其他为2',
  `project_code` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '模块标识\n,银行流水1001，支付流水1002 左侧产品导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '分类为1,规则为0 策略类型导航标识',
  `purpose_category` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '分类名称',
  `category_type` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '流水性质',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `data_module` longtext COMMENT '数据模型',
  `conditions` longtext COMMENT '判断条件',
  `strongly_reject` varchar(11) DEFAULT NULL COMMENT '是否强拒绝1是0否',
  `quato_rate` varchar(255) DEFAULT NULL,
  `transfer_to_person` varchar(2) DEFAULT NULL COMMENT '是否转人工 1是 0 否',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_name` (`code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=196 DEFAULT CHARSET=utf8 COMMENT='决策code登记表';

-- -----------------------------------------------------------
-- 表: rde_model_decision_code_level_version
-- 说明: 决策code登记表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rde_model_decision_code_level_version`;
CREATE TABLE `rde_model_decision_code_level_version` (
  `id` int(11) NOT NULL COMMENT 'id',
  `code` varchar(200) DEFAULT '' COMMENT 'code',
  `level` varchar(255) DEFAULT '' COMMENT '等级',
  `type` int(11) DEFAULT '0' COMMENT '类型，0默认评分策略，1风险预警策略，2贷前准入策略,3银行流水预警策略',
  `risk_type` int(11) DEFAULT '0' COMMENT '风险分类，1运营商风险，2欺诈风险，3身份认证风险，4失信被执行风险，5.黑名单风险，6多头风险，7.交易行为风险 ，8.催收风险 , 9.法人年龄 , 10.法人性别 , 11.关联人潜在风险 ,  12.特殊名单 , 13.借贷意向 , 14.经营年限 , 15.自然人股东占比 , 16.潜在经营性风险 , 17.工商变更-法人变更 , 18.工商变更-高管变更  ,19.行政处罚 , 20.环保处罚 , 21.经营异常 , 22.法院公告  , 23严重违法 , 24.终本案件 , 25.成为箱讯客户时长 , 26.货物品类波动变化水平 , 26.货物路线波动变化水平',
  `content` varchar(255) DEFAULT '' COMMENT '说明',
  `risk_description` varchar(255) DEFAULT '' COMMENT '话术',
  `sort_num` int(11) DEFAULT '0' COMMENT '排序',
  `start_rate` decimal(12,2) DEFAULT NULL COMMENT '开始比例%',
  `end_rate` decimal(12,2) DEFAULT NULL COMMENT '结束比例%',
  `status` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '1' COMMENT '状态 0 未开放 1 已开放',
  `term` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '条件',
  `term_package` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '规则所用的包',
  `term_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '处理所需别名',
  `term_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '处理所需code',
  `term_other` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '处理所需其他条件',
  `term_rule` longtext COMMENT '生成规则',
  `remark` varchar(255) DEFAULT '' COMMENT '备注',
  `update_by` varchar(200) DEFAULT '' COMMENT '更新人',
  `create_by` varchar(200) DEFAULT '' COMMENT '更新名称',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `dept_id` int(11) DEFAULT '100' COMMENT '公司id',
  `data_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '数据状态',
  `dept_flag` varchar(255) DEFAULT NULL COMMENT '部门标识101为1，其他为2',
  `project_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模块标识\n,银行流水1001，支付流水1002 左侧产品导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类为1,规则为0 策略类型导航标识',
  `purpose_category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类名称',
  `category_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流水性质',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `data_module` text COMMENT '数据模型',
  `conditions` longtext COMMENT '判断条件',
  `strongly_reject` varchar(11) DEFAULT NULL COMMENT '是否强拒绝1是0否',
  `quato_rate` varchar(255) DEFAULT NULL,
  `transfer_to_person` varchar(2) DEFAULT '0' COMMENT '是否转人工 1是 0 否',
  KEY `index_name` (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='决策code登记表';

-- -----------------------------------------------------------
-- 表: rde_model_rule_method
-- 说明: 规则方法表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rde_model_rule_method`;
CREATE TABLE `rde_model_rule_method` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code_id` int(11) DEFAULT NULL COMMENT '决策code id',
  `rule_id` int(11) DEFAULT NULL COMMENT '规则明细id',
  `name` varchar(500) DEFAULT '' COMMENT '方法名称',
  `obj_alias` varchar(500) DEFAULT '' COMMENT '对象别名',
  `key_code` varchar(500) DEFAULT '' COMMENT '对象属性',
  `key_value` varchar(500) DEFAULT '' COMMENT '对象属性值',
  `descr` varchar(500) DEFAULT '' COMMENT '描述',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_user_id` int(11) NOT NULL COMMENT '操作人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `dept_id` int(11) DEFAULT NULL COMMENT '公司id',
  `data_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '数据状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='规则方法表';

-- -----------------------------------------------------------
-- 表: rde_model_rule_property
-- 说明: 规则属性表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rde_model_rule_property`;
CREATE TABLE `rde_model_rule_property` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code_id` int(11) DEFAULT NULL COMMENT '决策code id',
  `rule_id` int(11) DEFAULT NULL COMMENT '规则明细id',
  `key_code` varchar(500) DEFAULT '' COMMENT '名称',
  `key_value` varchar(500) DEFAULT '' COMMENT '属性code',
  `descr` varchar(500) DEFAULT '' COMMENT '描述',
  `type` int(1) DEFAULT '0' COMMENT '类型（预授信/授信）',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_user_id` int(11) NOT NULL COMMENT '操作人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `dept_id` int(11) DEFAULT NULL COMMENT '公司id',
  `data_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '数据状态',
  `quato_rate` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='规则属性表';

-- -----------------------------------------------------------
-- 表: rde_model_rule_record
-- 说明: 新规则条件表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rde_model_rule_record`;
CREATE TABLE `rde_model_rule_record` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '自增长ID',
  `code_id` int(11) DEFAULT NULL COMMENT '决策code id',
  `object_id` int(11) DEFAULT '0' COMMENT '分析对象id',
  `theme_ids` varchar(500) DEFAULT '' COMMENT '分析对象ids，第一级是大对象id，',
  `sorder` int(11) DEFAULT '0' COMMENT '规则顺序',
  `rule_type` tinyint(3) DEFAULT '1' COMMENT '类型:1wehn条件 2.then响应code,3更新对象',
  `rule_obj_alias` varchar(200) DEFAULT '' COMMENT '规则对象别名(限200个英文字符)',
  `rule_obj` varchar(200) DEFAULT '' COMMENT '规则对象(限200个英文字符)',
  `rule_properties` varchar(200) DEFAULT '' COMMENT '规则属性(限200个英文字符)',
  `rule_operator` varchar(200) DEFAULT '' COMMENT '规则运算符(限200个英文字符)',
  `rule_value` text COMMENT '规则值(限200个英文字符)',
  `packages` varchar(200) DEFAULT '' COMMENT '拼接之后的包路径',
  `create_by` varchar(60) DEFAULT 'admin' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(60) DEFAULT NULL COMMENT '修改者',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `dept_id` int(11) DEFAULT NULL COMMENT '公司id',
  `data_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '数据状态',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '策略模型标识',
  `project_code` varchar(255) DEFAULT NULL COMMENT '左侧产品导航标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='新规则条件表';

-- -----------------------------------------------------------
-- 表: rule_business
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rule_business`;
CREATE TABLE `rule_business` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `businessCode` varchar(255) NOT NULL COMMENT '业务编号',
  `detail` longtext,
  `type` varchar(255) DEFAULT NULL COMMENT '业务类型',
  `modelNo` int(2) DEFAULT NULL COMMENT '模型标识（1.借款人，2.实控人，3.担保人 4.共债人（配偶,5.企业）\r\n）',
  `userName` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `idNumber` varchar(255) DEFAULT NULL,
  `enterpriseName` varchar(255) DEFAULT NULL,
  `creditCode` varchar(255) DEFAULT NULL,
  `ckey` varchar(255) DEFAULT NULL,
  `applyUserId` int(11) DEFAULT NULL COMMENT '客户id',
  `interfaceType` int(2) DEFAULT NULL COMMENT '接口类型(0-营销初筛,1-初筛,2-准入,3个体评分,4-财务评分)',
  `createTime` datetime(2) DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(2) COMMENT '创建时间',
  `modelName` varchar(255) DEFAULT NULL COMMENT '应用模型名称',
  `businessType` varchar(255) DEFAULT NULL COMMENT '0  企业  1个人',
  `subject` varchar(255) DEFAULT NULL COMMENT '调用主体',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_index_name` (`businessCode`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- -----------------------------------------------------------
-- 表: rule_pool_monthly_snapshot
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rule_pool_monthly_snapshot`;
CREATE TABLE `rule_pool_monthly_snapshot` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `snapshot_month` varchar(7) NOT NULL COMMENT '快照月份',
  `rule_id` int(11) NOT NULL COMMENT '规则ID',
  `status` varchar(10) NOT NULL COMMENT '当时状态',
  `project_code` varchar(100) DEFAULT NULL COMMENT '产品',
  `business_code` varchar(100) DEFAULT NULL COMMENT '业务场景',
  `rule_code` varchar(100) DEFAULT NULL COMMENT '模型类型',
  `create_time` datetime DEFAULT NULL COMMENT '快照创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_month_rule` (`snapshot_month`,`rule_id`),
  KEY `idx_snapshot_month` (`snapshot_month`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4;

-- -----------------------------------------------------------
-- 表: rule_record_reuse
-- 说明: 策略规则复用关联表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rule_record_reuse`;
CREATE TABLE `rule_record_reuse` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `build_project_code` int(11) DEFAULT NULL COMMENT '自建产品id',
  `build_business_code` int(11) DEFAULT NULL COMMENT '自建业务场景id',
  `build_rule_code` int(11) DEFAULT NULL COMMENT '自建策略导航id',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `parent_card_id` int(11) DEFAULT NULL COMMENT '标准策略id',
  `group_id` int(11) DEFAULT NULL COMMENT '自建规则组id',
  `rule_id` int(11) DEFAULT NULL COMMENT '自建策略id',
  `moudle_id` int(11) DEFAULT NULL COMMENT '模块区分 1策略2规则组3规则',
  `button_state` tinyint(1) DEFAULT NULL COMMENT '按钮状态  1启用0停用',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `data_status` tinyint(1) DEFAULT '0' COMMENT '数据状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='策略规则复用关联表';

-- -----------------------------------------------------------
-- 表: rule_record_reuse_snapshot
-- 说明: 策略规则复用关联快照表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `rule_record_reuse_snapshot`;
CREATE TABLE `rule_record_reuse_snapshot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `build_project_code` int(11) DEFAULT NULL COMMENT '自建产品id',
  `build_business_code` int(11) DEFAULT NULL COMMENT '自建业务场景id',
  `build_rule_code` int(11) DEFAULT NULL COMMENT '自建策略导航id',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `parent_card_id` int(11) DEFAULT NULL COMMENT '标准策略id',
  `group_id` int(11) DEFAULT NULL COMMENT '自建规则组id',
  `rule_id` int(11) DEFAULT NULL COMMENT '自建策略id',
  `moudle_id` int(11) DEFAULT NULL COMMENT '模块区分 1策略2规则组3规则',
  `button_state` tinyint(1) DEFAULT NULL COMMENT '按钮状态  1启用0停用',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `data_status` tinyint(1) DEFAULT '0' COMMENT '数据状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='策略规则复用关联快照表';

-- -----------------------------------------------------------
-- 表: score_card_record
-- 说明: 评分卡主表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `score_card_record`;
CREATE TABLE `score_card_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `score_card` varchar(255) DEFAULT NULL COMMENT '评分卡名称',
  `profession` varchar(255) DEFAULT NULL COMMENT '行业',
  `argument` varchar(255) DEFAULT NULL COMMENT '参数',
  `calculate_score` varchar(255) DEFAULT NULL COMMENT '计算评分',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `data_state` int(255) DEFAULT NULL COMMENT '数据状态 0正常 1删除',
  `project_code` varchar(255) DEFAULT NULL COMMENT '左侧产品导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '策略类型导航标识',
  `dept_flag` int(11) DEFAULT NULL COMMENT '部门标识 1超级管理员 2普通用户',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `button_state` int(11) DEFAULT NULL COMMENT '按钮状态 1启用 0禁用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user_id` int(11) DEFAULT NULL COMMENT '操作人ID',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `model_score` varchar(255) DEFAULT NULL COMMENT '模型总分数',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `id` (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8 COMMENT='评分卡主表';

-- -----------------------------------------------------------
-- 表: score_card_record_snapshot
-- 说明: 评分卡主表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `score_card_record_snapshot`;
CREATE TABLE `score_card_record_snapshot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `score_card` varchar(255) DEFAULT NULL COMMENT '评分卡名称',
  `profession` varchar(255) DEFAULT NULL COMMENT '行业',
  `argument` varchar(255) DEFAULT NULL COMMENT '参数',
  `calculate_score` varchar(255) DEFAULT NULL COMMENT '计算评分',
  `model_score` varchar(255) DEFAULT NULL COMMENT '模型总分数',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `data_state` int(255) DEFAULT NULL COMMENT '数据状态 0正常 1删除',
  `project_code` varchar(255) DEFAULT NULL COMMENT '左侧产品导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '策略类型导航标识',
  `dept_flag` int(11) DEFAULT NULL COMMENT '部门标识 1超级管理员 2普通用户',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `button_state` int(11) DEFAULT NULL COMMENT '按钮状态 1启用 0禁用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user_id` int(11) DEFAULT NULL COMMENT '操作人ID',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8 COMMENT='评分卡主表';

-- -----------------------------------------------------------
-- 表: score_card_record_version
-- 说明: 评分卡主表 -- 版本控制
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `score_card_record_version`;
CREATE TABLE `score_card_record_version` (
  `id` int(11) NOT NULL,
  `score_card` varchar(255) DEFAULT NULL COMMENT '评分卡名称',
  `profession` varchar(255) DEFAULT NULL COMMENT '行业',
  `argument` varchar(255) DEFAULT NULL COMMENT '参数',
  `calculate_score` varchar(255) DEFAULT NULL COMMENT '计算评分',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `data_state` int(11) DEFAULT NULL COMMENT '数据状态 0正常 1删除',
  `project_code` varchar(255) DEFAULT NULL COMMENT '左侧产品导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '策略类型导航标识',
  `dept_flag` int(11) DEFAULT NULL COMMENT '部门标识 1超级管理员 2普通用户',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `button_state` int(11) DEFAULT NULL COMMENT '按钮状态 1启用 0禁用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user_id` int(11) DEFAULT NULL COMMENT '操作人ID',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `model_score` varchar(255) DEFAULT NULL COMMENT '模型总分数'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='评分卡主表 -- 版本控制';

-- -----------------------------------------------------------
-- 表: score_card_reuse
-- 说明: 评分卡复用关联表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `score_card_reuse`;
CREATE TABLE `score_card_reuse` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `build_project_code` int(11) DEFAULT NULL COMMENT '自建产品id',
  `build_business_code` int(11) DEFAULT NULL COMMENT '自建业务场景id',
  `build_rule_code` int(11) DEFAULT NULL COMMENT '自建策略导航id',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `parent_card_id` int(11) DEFAULT NULL COMMENT '标准评分卡id',
  `button_state` tinyint(1) DEFAULT NULL COMMENT '按钮状态  1启用0停用',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `data_status` tinyint(1) DEFAULT '0' COMMENT '数据状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='评分卡复用关联表';

-- -----------------------------------------------------------
-- 表: score_card_reuse_snapshot
-- 说明: 评分卡复用关联快照表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `score_card_reuse_snapshot`;
CREATE TABLE `score_card_reuse_snapshot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `build_project_code` int(11) DEFAULT NULL COMMENT '自建产品id',
  `build_business_code` int(11) DEFAULT NULL COMMENT '自建业务场景id',
  `build_rule_code` int(11) DEFAULT NULL COMMENT '自建策略导航id',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `parent_card_id` int(11) DEFAULT NULL COMMENT '标准评分卡id',
  `button_state` tinyint(1) DEFAULT NULL COMMENT '按钮状态  1启用0停用',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `data_status` tinyint(1) DEFAULT '0' COMMENT '数据状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评分卡复用关联快照表';

-- -----------------------------------------------------------
-- 表: score_debt
-- 说明: 海翔评分表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `score_debt`;
CREATE TABLE `score_debt` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `score_min` int(11) DEFAULT NULL,
  `score_max` int(11) DEFAULT NULL,
  `debt_min` int(11) DEFAULT NULL,
  `debt_max` int(11) DEFAULT NULL,
  `quota` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='海翔评分表';

-- -----------------------------------------------------------
-- 表: score_index_rule
-- 说明: 指标规则表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `score_index_rule`;
CREATE TABLE `score_index_rule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `index_rule` varchar(255) DEFAULT NULL COMMENT '指标规则',
  `data_module` text COMMENT '数据模块',
  `code` varchar(255) DEFAULT NULL COMMENT 'code',
  `level` int(11) DEFAULT NULL COMMENT '等级',
  `decision_group` varchar(255) DEFAULT NULL COMMENT '决策组',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `term_package` varchar(255) DEFAULT NULL COMMENT '规则所用的包',
  `term_key` varchar(255) DEFAULT NULL COMMENT '处理所需别名',
  `term_rule` text COMMENT '生成规则',
  `data_state` int(11) DEFAULT NULL COMMENT '数据状态 0正常 1删除',
  `project_code` varchar(255) DEFAULT NULL COMMENT '左侧产品导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '策略类型导航标识',
  `dept_flag` int(11) DEFAULT NULL COMMENT '部门标识 1超级管理员 2普通用户',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `button_state` int(11) DEFAULT NULL COMMENT '按钮状态 1启用 0禁用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user_id` int(11) DEFAULT NULL COMMENT '操作人ID',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `default_rule` int(11) DEFAULT NULL COMMENT '默认指标规则  0 否 1是',
  `takeEffect` int(11) DEFAULT NULL COMMENT '是否生效 0 否 1是',
  `score_primary_id` int(11) DEFAULT NULL COMMENT '指标卡id',
  `score` double(20,2) DEFAULT NULL COMMENT '评分',
  `condition_array` text COMMENT '规则组',
  `scord_card_id` int(11) DEFAULT NULL COMMENT '评分卡id',
  `scord_primary_ids` varchar(255) DEFAULT NULL COMMENT '指标卡id集',
  `strongly_reject` varchar(2) DEFAULT NULL COMMENT '是否强拒绝1是0否',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8 COMMENT='指标规则表';

-- -----------------------------------------------------------
-- 表: score_index_rule_snapshot
-- 说明: 指标规则表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `score_index_rule_snapshot`;
CREATE TABLE `score_index_rule_snapshot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `index_rule` varchar(255) DEFAULT NULL COMMENT '指标规则',
  `data_module` text COMMENT '数据模块',
  `code` varchar(255) DEFAULT NULL COMMENT 'code',
  `level` int(11) DEFAULT NULL COMMENT '等级',
  `decision_group` varchar(255) DEFAULT NULL COMMENT '决策组',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `term_package` varchar(255) DEFAULT NULL COMMENT '规则所用的包',
  `term_key` varchar(255) DEFAULT NULL COMMENT '处理所需别名',
  `term_rule` text COMMENT '生成规则',
  `data_state` int(11) DEFAULT NULL COMMENT '数据状态 0正常 1删除',
  `project_code` varchar(255) DEFAULT NULL COMMENT '左侧产品导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '策略类型导航标识',
  `dept_flag` int(11) DEFAULT NULL COMMENT '部门标识 1超级管理员 2普通用户',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `button_state` int(11) DEFAULT NULL COMMENT '按钮状态 1启用 0禁用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user_id` int(11) DEFAULT NULL COMMENT '操作人ID',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `default_rule` int(11) DEFAULT NULL COMMENT '默认指标规则  0 否 1是',
  `takeEffect` int(11) DEFAULT NULL COMMENT '是否生效 0 否 1是',
  `score_primary_id` int(11) DEFAULT NULL COMMENT '指标卡id',
  `score` double(20,2) DEFAULT NULL COMMENT '评分',
  `condition_array` text COMMENT '规则组',
  `scord_card_id` int(11) DEFAULT NULL COMMENT '评分卡id',
  `scord_primary_ids` varchar(255) DEFAULT NULL COMMENT '指标卡id集',
  `strongly_reject` varchar(2) DEFAULT NULL COMMENT '是否强拒绝1是0否',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8 COMMENT='指标规则表';

-- -----------------------------------------------------------
-- 表: score_index_rule_version
-- 说明: 指标规则表 -- 版本控制
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `score_index_rule_version`;
CREATE TABLE `score_index_rule_version` (
  `id` int(11) NOT NULL,
  `index_rule` varchar(255) DEFAULT NULL COMMENT '指标规则',
  `data_module` text COMMENT '数据模块',
  `code` varchar(255) DEFAULT NULL COMMENT 'code',
  `level` int(11) DEFAULT NULL COMMENT '等级',
  `decision_group` varchar(255) DEFAULT NULL COMMENT '决策组',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `term_package` varchar(255) DEFAULT NULL COMMENT '规则所用的包',
  `term_key` varchar(255) DEFAULT NULL COMMENT '处理所需别名',
  `term_rule` text COMMENT '生成规则',
  `data_state` int(11) DEFAULT NULL COMMENT '数据状态 0正常 1删除',
  `project_code` varchar(255) DEFAULT NULL COMMENT '左侧产品导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '策略类型导航标识',
  `dept_flag` int(11) DEFAULT NULL COMMENT '部门标识 1超级管理员 2普通用户',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `button_state` int(11) DEFAULT NULL COMMENT '按钮状态 1启用 0禁用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user_id` int(11) DEFAULT NULL COMMENT '操作人ID',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `default_rule` int(11) DEFAULT NULL COMMENT '默认指标规则  0 否 1是',
  `takeEffect` int(11) DEFAULT NULL COMMENT '是否生效 0 否 1是',
  `score_primary_id` int(11) DEFAULT NULL COMMENT '指标卡id',
  `score` double(20,2) DEFAULT NULL COMMENT '评分',
  `condition_array` text COMMENT '规则组',
  `scord_card_id` int(11) DEFAULT NULL COMMENT '评分卡id',
  `scord_primary_ids` varchar(255) DEFAULT NULL COMMENT '指标卡id集',
  `strongly_reject` varchar(2) DEFAULT NULL COMMENT '是否强拒绝1是0否'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='指标规则表 -- 版本控制';

-- -----------------------------------------------------------
-- 表: score_primary_index
-- 说明: 一级指标存储表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `score_primary_index`;
CREATE TABLE `score_primary_index` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `primary_index` varchar(255) DEFAULT NULL COMMENT '一级指标名称',
  `weight` double(20,2) DEFAULT NULL COMMENT '权重',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `score_card_id` int(11) DEFAULT NULL COMMENT '关联评分卡id',
  `pointer_rule_id` int(11) DEFAULT NULL COMMENT '关联指标规则',
  `data_state` int(11) DEFAULT NULL COMMENT '数据状态 0正常 1删除',
  `project_code` varchar(255) DEFAULT NULL COMMENT '左侧产品导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '策略类型导航标识',
  `dept_flag` int(11) DEFAULT NULL COMMENT '部门标识 1超级管理员 2普通用户',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `button_state` int(11) DEFAULT NULL COMMENT '按钮状态 1启用 0禁用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user_id` int(11) DEFAULT NULL COMMENT '操作人ID',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `parent_card_id` int(11) DEFAULT NULL COMMENT '父指标id',
  `leaf_node` int(11) DEFAULT NULL COMMENT '是否叶子节点 0 是 1否',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8 COMMENT='一级指标存储表';

-- -----------------------------------------------------------
-- 表: score_primary_index_snapshot
-- 说明: 一级指标存储表
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `score_primary_index_snapshot`;
CREATE TABLE `score_primary_index_snapshot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `primary_index` varchar(255) DEFAULT NULL COMMENT '一级指标名称',
  `weight` double(20,2) DEFAULT NULL COMMENT '权重',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `score_card_id` int(11) DEFAULT NULL COMMENT '关联评分卡id',
  `pointer_rule_id` int(11) DEFAULT NULL COMMENT '关联指标规则',
  `data_state` int(11) DEFAULT NULL COMMENT '数据状态 0正常 1删除',
  `project_code` varchar(255) DEFAULT NULL COMMENT '左侧产品导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '策略类型导航标识',
  `dept_flag` int(11) DEFAULT NULL COMMENT '部门标识 1超级管理员 2普通用户',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `button_state` int(11) DEFAULT NULL COMMENT '按钮状态 1启用 0禁用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user_id` int(11) DEFAULT NULL COMMENT '操作人ID',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `parent_card_id` int(11) DEFAULT NULL COMMENT '父指标id',
  `leaf_node` int(11) DEFAULT NULL COMMENT '是否叶子节点 0 是 1否',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8 COMMENT='一级指标存储表';

-- -----------------------------------------------------------
-- 表: score_primary_index_version
-- 说明: 一级指标存储表  -- 版本控制
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `score_primary_index_version`;
CREATE TABLE `score_primary_index_version` (
  `id` int(11) NOT NULL,
  `primary_index` varchar(255) DEFAULT NULL COMMENT '一级指标名称',
  `weight` double(20,2) DEFAULT NULL COMMENT '权重',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `score_card_id` int(11) DEFAULT NULL COMMENT '关联评分卡id',
  `pointer_rule_id` int(11) DEFAULT NULL COMMENT '关联指标规则',
  `data_state` int(11) DEFAULT NULL COMMENT '数据状态 0正常 1删除',
  `project_code` varchar(255) DEFAULT NULL COMMENT '左侧产品导航标识',
  `business_code` varchar(255) DEFAULT NULL COMMENT '业务场景导航标识',
  `rule_code` varchar(255) DEFAULT NULL COMMENT '策略类型导航标识',
  `dept_flag` int(11) DEFAULT NULL COMMENT '部门标识 1超级管理员 2普通用户',
  `dept_id` int(11) DEFAULT NULL COMMENT '部门ID',
  `button_state` int(11) DEFAULT NULL COMMENT '按钮状态 1启用 0禁用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_user_id` int(11) DEFAULT NULL COMMENT '操作人ID',
  `version_control` varchar(255) DEFAULT NULL COMMENT '版本控制',
  `parent_card_id` int(11) DEFAULT NULL COMMENT '父指标id',
  `leaf_node` int(11) DEFAULT NULL COMMENT '是否叶子节点 0 是 1否'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='一级指标存储表  -- 版本控制';

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- risk_smart_decision_manage 初始化完成
-- ============================================================