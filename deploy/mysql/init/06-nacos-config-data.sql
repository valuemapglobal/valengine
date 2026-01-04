-- ============================================================
-- Nacos 配置数据初始化脚本
-- 包含各服务的配置信息
-- ============================================================

-- 设置字符集确保中文正确处理
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

USE `nacos_config`;

-- 清空旧配置
DELETE FROM config_info WHERE data_id LIKE 'risk-smart-%';

-- ===================== 公共配置 =====================
INSERT INTO config_info(data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, encrypted_data_key) VALUES
('application-dev.yml', 'DEFAULT_GROUP',
'# 公共配置
spring:
  main:
    allow-circular-references: true
  cloud:
    nacos:
      discovery:
        server-addr: ${NACOS_HOST:localhost}:${NACOS_PORT:8848}
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    database: 0
    timeout: 10000ms
    lettuce:
      pool:
        max-active: 200
        max-wait: -1ms
        max-idle: 10
        min-idle: 0

# 日志配置
logging:
  level:
    com.value: debug
    org.springframework: warn
', MD5('application-dev.yml'), NOW(), NOW(), NULL, NULL, '', '', '公共配置', NULL, NULL, 'yaml', '');

-- ===================== 系统服务配置 =====================
INSERT INTO config_info(data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, encrypted_data_key) VALUES
('risk-smart-system-dev.yml', 'DEFAULT_GROUP',
'# 系统服务配置
server:
  port: 8992

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3306}/risk_smart_system?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true
    username: ${MYSQL_USER:root}
    password: ${MYSQL_PASSWORD:root}

# MyBatis配置
mybatis-plus:
  mapper-locations: classpath*:mapper/**/*.xml
  type-aliases-package: com.value.system.domain
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
', MD5('risk-smart-system-dev.yml'), NOW(), NOW(), NULL, NULL, '', '', '系统服务配置', NULL, NULL, 'yaml', '');

-- ===================== 数据中台服务配置 =====================
INSERT INTO config_info(data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, encrypted_data_key) VALUES
('risk-smart-data-middle-station-dev.yml', 'DEFAULT_GROUP',
'# 数据中台服务配置
server:
  port: 8990

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3306}/risk_smart_data_middle_station?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true
    username: ${MYSQL_USER:root}
    password: ${MYSQL_PASSWORD:root}

# MyBatis配置
mybatis-plus:
  mapper-locations: classpath*:mapper/**/*.xml
  type-aliases-package: com.value.data.domain
  configuration:
    map-underscore-to-camel-case: true

# 签名配置
signature:
  expirationTimeInSeconds: 300

# 业务配置
business:
  apiTokenDecryptPassword: 6460201d23954f8e
', MD5('risk-smart-data-middle-station-dev.yml'), NOW(), NOW(), NULL, NULL, '', '', '数据中台服务配置', NULL, NULL, 'yaml', '');

-- ===================== 决策管理服务配置 =====================
INSERT INTO config_info(data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, encrypted_data_key) VALUES
('risk-smart-decision-manage-dev.yml', 'DEFAULT_GROUP',
'# 决策管理服务配置
server:
  port: 8991

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3306}/risk_smart_decision_manage?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true
    username: ${MYSQL_USER:root}
    password: ${MYSQL_PASSWORD:root}

# MyBatis配置
mybatis-plus:
  mapper-locations: classpath*:mapper/**/*.xml
  type-aliases-package: com.value.decision
  configuration:
    map-underscore-to-camel-case: true

# 规则引擎配置
service:
  engine:
    ruleUrl: http://localhost:8088/rule/execute
    ruleflUrl: http://localhost:8088/rulefl/execute
  interfaceManage:
    interfaceRequestUrl: http://localhost:8990/interfaceRequest/api
    interfaceDetailUrl: http://localhost:8990/interfaceManage/findInterfaceInfo
    apiToken: your-api-token

# AppKey配置
appkey: your-app-key
sercet: your-secret
', MD5('risk-smart-decision-manage-dev.yml'), NOW(), NOW(), NULL, NULL, '', '', '决策管理服务配置', NULL, NULL, 'yaml', '');

-- ===================== 网关服务配置 =====================
INSERT INTO config_info(data_id, group_id, content, md5, gmt_create, gmt_modified, src_user, src_ip, app_name, tenant_id, c_desc, c_use, effect, type, encrypted_data_key) VALUES
('risk-smart-gateway-dev.yml', 'DEFAULT_GROUP',
'# 网关服务配置
server:
  port: 8081

spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lower-case-service-id: true
      routes:
        - id: risk-smart-system
          uri: lb://risk-smart-system
          predicates:
            - Path=/system/**
          filters:
            - StripPrefix=1
        - id: risk-smart-data-middle-station
          uri: lb://risk-smart-data-middle-station
          predicates:
            - Path=/data/**
          filters:
            - StripPrefix=1
        - id: risk-smart-decision-manage
          uri: lb://risk-smart-decision-manage
          predicates:
            - Path=/decision/**
          filters:
            - StripPrefix=1
', MD5('risk-smart-gateway-dev.yml'), NOW(), NOW(), NULL, NULL, '', '', '网关服务配置', NULL, NULL, 'yaml', '');

-- ============================================================
-- Nacos配置数据初始化完成
-- ============================================================
