# ValEngine - 开源决策引擎平台

<div align="center">

**企业级智能风控决策引擎系统**

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.9-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.1-blue.svg)](https://spring.io/projects/spring-cloud)

</div>

---

## 项目简介

ValEngine 是一个**企业级智能风控平台**，提供规则引擎、决策管理、数据中台等核心功能，帮助企业快速构建智能风控决策系统。

### 核心特性

- **规则引擎** - 基于 Drools，支持复杂业务规则配置
- **决策管理** - 可视化决策流程编排，灵活的决策链路
- **数据中台** - 统一数据接入、接口管理和特征变量管理
- **权限管理** - 基于 RBAC 的细粒度权限控制体系
- **微服务架构** - Spring Cloud 微服务，支持弹性扩展
- **容器化部署** - 一键 Docker Compose 部署

---

## 系统架构

```
                    ┌─────────────────┐
                    │   Vue.js 前端    │
                    │   (端口: 80)     │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │  Spring Gateway │
                    │   (端口: 8080)   │
                    └────────┬────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
┌───────▼───────┐   ┌───────▼───────┐   ┌───────▼───────┐
│  系统服务      │   │  数据中台      │   │  决策管理      │
│ (端口: 8992)  │   │ (端口: 8990)  │   │ (端口: 8991)  │
└───────┬───────┘   └───────┬───────┘   └───────┬───────┘
        │                    │                    │
        └────────────────────┼────────────────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
┌───────▼───────┐   ┌───────▼───────┐   ┌───────▼───────┐
│    MySQL 8.4  │   │    Redis 7    │   │  Nacos 3.1.1  │
│ (端口: 3306)  │   │ (端口: 6379)  │   │ (端口: 8848)  │
└───────────────┘   └───────────────┘   └───────────────┘
```

---

## 快速开始

### 环境要求

- **Docker** >= 20.10
- **Docker Compose** >= 2.0
- **Git**

### 一键启动

```bash
# 1. 克隆项目
git clone https://github.com/valuemapglobal/valengine.git
cd valengine

# 2. 复制环境变量配置
cp .env.example .env

# 3. 启动基础服务 (MySQL, Redis, Nacos)
docker-compose up -d

# 4. 查看服务状态
docker-compose ps

# 5. 查看启动日志
docker-compose logs -f
```

### 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 前端 | http://localhost | 管理后台 |
| 网关 | http://localhost:8080 | API 网关 |
| Nacos | http://localhost:8858/nacos | 配置中心 (nacos/nacos) |

### 默认账号

- 用户名：`admin`
- 密码：`admin123`

---

## 项目结构

```
ValEngine/
├── risk-smart-backend/                    # 后端项目
│   ├── risk-smart-common/                 # 公共模块 (工具类、API定义)
│   ├── risk-smart-gateway/                # API 网关服务
│   ├── risk-smart-system/                 # 系统管理服务 (用户、角色、权限)
│   ├── risk-smart-data-middle-station/    # 数据中台服务 (接口、变量管理)
│   └── risk-smart-decision-manage/        # 决策管理服务 (规则、模型)
├── web/                                   # Vue.js 前端项目
├── deploy/                                # 部署配置
│   ├── mysql/                             # MySQL 配置和初始化脚本
│   │   ├── conf/my.cnf                    # MySQL 配置文件
│   │   └── init/                          # 数据库初始化 SQL
│   ├── redis/                             # Redis 配置
│   │   └── redis.conf                     # Redis 配置文件
│   └── nacos/                             # Nacos 配置
│       └── config/                        # 服务配置文件
├── docker-compose.yml                     # Docker Compose 配置
├── .env.example                           # 环境变量模板
└── README.md
```

---

## 服务说明

| 服务 | 端口 | 说明 |
|------|------|------|
| risk-smart-gateway | 8080 | API 网关，路由转发和鉴权 |
| risk-smart-system | 8992 | 系统服务，用户、角色、权限、登录认证 |
| risk-smart-data-middle-station | 8990 | 数据中台，接口管理、特征变量、指标管理 |
| risk-smart-decision-manage | 8991 | 决策管理，规则引擎、决策模型、策略配置 |

### API 路由

| 路径前缀 | 目标服务 | 说明 |
|----------|----------|------|
| /vm/system/** | risk-smart-system | 前端代理路径 |
| /vm/auth/** | risk-smart-system | 前端认证路径 |
| /vm/smartDecision/** | risk-smart-decision-manage | 前端决策路径 |
| /vm/smartData/** | risk-smart-data-middle-station | 前端数据路径 |
| /system/** | risk-smart-system | 标准路径 |
| /data/** | risk-smart-data-middle-station | 标准路径 |
| /decision/** | risk-smart-decision-manage | 标准路径 |
| /login, /logout | risk-smart-system | 认证接口 |

---

## 技术栈

### 后端

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | JDK |
| Spring Boot | 3.5.9 | 基础框架 |
| Spring Cloud | 2025.0.1 | 微服务框架 |
| Spring Cloud Alibaba | 2025.0.0.0 | 微服务组件 |
| MyBatis Plus | 3.5.14 | ORM 框架 |
| Druid | 1.2.24 | 数据库连接池 |
| Drools | 7.73.0.Final | 规则引擎 |
| MySQL | 8.4.0 | 数据库 |
| Redis | 7.x | 缓存 |
| Nacos | 3.1.1 | 注册/配置中心 |
| Hutool | 5.8.22 | 工具类库 |
| Redisson | 3.17.7 | Redis 客户端 |

### 前端

| 技术 | 说明 |
|------|------|
| Vue.js 2.x | 前端框架 |
| Element UI | UI 组件库 |
| Axios | HTTP 客户端 |

---

## 本地开发

### 环境准备

1. JDK 17
2. Maven 3.8+
3. Node.js 16+
4. MySQL 8.x
5. Redis 7.x

### 启动基础服务

使用 Docker 启动 MySQL、Redis、Nacos：

```bash
# 复制环境变量配置
cp .env.example .env

# 启动基础服务
docker-compose up -d mysql redis nacos
```

### 环境变量配置

本地开发启动后端服务时，如果 MySQL 端口映射为非 3306（如 13306），需要设置环境变量：

```bash
# Windows PowerShell
$env:MYSQL_PORT="13306"

# Windows CMD
set MYSQL_PORT=13306

# Linux/Mac
export MYSQL_PORT=13306
```

### 启动顺序

1. 启动 MySQL、Redis、Nacos
2. 启动 risk-smart-gateway
3. 启动 risk-smart-system
4. 启动 risk-smart-data-middle-station
5. 启动 risk-smart-decision-manage
6. 启动前端项目

### 后端启动

```bash
cd risk-smart-backend

# 编译打包
mvn clean package -DskipTests

# 启动各服务 (在各模块目录下)
java -jar risk-smart-gateway/target/risk-smart-gateway.jar
java -jar risk-smart-system/target/risk-smart-system.jar
java -jar risk-smart-decision-manage/target/risk-smart-decision-manage.jar
java -jar risk-smart-data-middle-station/target/risk-smart-data-middle-station.jar

# 或在 IDE 中启动各 Application 类
```

### 前端启动

```bash
cd web

# 安装依赖
npm install

# 启动开发服务器
npm run serve
```

---

## 数据库

系统包含以下数据库：

| 数据库 | 说明 |
|--------|------|
| nacos_config | Nacos 配置中心 |
| risk_smart_system | 系统管理 (用户、角色、菜单) |
| risk_smart_data_middle_station | 数据中台 (接口、变量) |
| risk_smart_decision_manage | 决策管理 (规则、模型) |

---

## 开源协议

本项目基于 [Apache License 2.0](LICENSE) 开源协议。

---

## 联系我们

- **GitHub**: https://github.com/valuemapglobal/valengine
- **邮箱**: vmgitadmin@valuemapglobal.com

---

## Star History

如果这个项目对你有帮助，请给我们一个 Star！

[![Star History Chart](https://api.star-history.com/svg?repos=valuemapglobal/valengine&type=Date)](https://star-history.com/#valuemapglobal/valengine&Date)
