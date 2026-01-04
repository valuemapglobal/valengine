-- =====================================================================
-- 数据库迁移脚本: FieId -> FieldId 拼写错误修复
-- 数据库: risk_smart_data_middle_station_dev
-- 说明: 修复表名和字段名中 "FieId" 的拼写错误为 "FieldId"
--
-- 执行前请备份数据库！
-- =====================================================================

-- 1. 重命名表: interface_fie_id_manage -> interface_field_id_manage
RENAME TABLE `interface_fie_id_manage` TO `interface_field_id_manage`;

-- 2. 修改字段名 (根据实际表结构调整)
-- 注意: 以下语句需要根据实际字段类型和长度进行调整
-- 请先执行 DESCRIBE interface_field_id_manage; 查看字段定义

-- 假设字段结构如下，请根据实际情况修改:
ALTER TABLE `interface_field_id_manage`
    CHANGE COLUMN `interface_fie_id_name` `interface_field_id_name` VARCHAR(100) COMMENT '参数名称',
    CHANGE COLUMN `interface_fie_id_alias` `interface_field_id_alias` VARCHAR(100) COMMENT '参数别名',
    CHANGE COLUMN `interface_fie_id_data_type` `interface_field_id_data_type` INT COMMENT '数据类型(0-数值，1-字符串，2-日期，3-对象，4-数组，5-文件，6-布尔，7-小数)',
    CHANGE COLUMN `interface_fie_id_type` `interface_field_id_type` INT COMMENT '参数类型(0-入参，1-出参)',
    CHANGE COLUMN `interface_fie_id_description` `interface_field_id_description` VARCHAR(500) COMMENT '参数描述';

-- 3. 如果其他表有外键引用，也需要修改
-- 例如: rde_risk_variable_record 表中的 interface_fie_id_manage 字段
-- ALTER TABLE `rde_risk_variable_record`
--     CHANGE COLUMN `interface_fie_id_manage` `interface_field_id_manage` VARCHAR(64) COMMENT '关联的接口参数';

-- =====================================================================
-- 执行完成后，请验证:
-- 1. SHOW TABLES LIKE '%field_id%';
-- 2. DESCRIBE interface_field_id_manage;
-- 3. SELECT COUNT(*) FROM interface_field_id_manage;
-- =====================================================================
