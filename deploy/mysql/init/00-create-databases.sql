-- ============================================================
-- RiskSmart 数据库创建脚本
-- 创建所有业务数据库
-- ============================================================

-- 设置字符集确保中文正确处理
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 1. 系统管理库 (RuoYi核心)
CREATE DATABASE IF NOT EXISTS `risk_smart_system`
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- 2. 数据中台库
CREATE DATABASE IF NOT EXISTS `risk_smart_data_middle_station`
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- 3. 决策管理库
CREATE DATABASE IF NOT EXISTS `risk_smart_decision_manage`
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- 4. Nacos配置库
CREATE DATABASE IF NOT EXISTS `nacos_config`
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- ============================================================
-- 数据库创建完成
-- ============================================================
