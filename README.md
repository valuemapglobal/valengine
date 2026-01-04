# RiskSmart 企业决策引擎平台 - 开源版

<div align="center">

**🚀 开箱即用的企业级决策引擎系统**

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)

[简体中文](README.md) | [English](README_EN.md)

</div>

---

## 📖 项目简介

RiskSmart 是一个**企业级决策引擎平台**，提供规则引擎、决策流程、数据中台等核心功能，帮助企业快速构建智能决策系统。

### ✨ 核心特性

- 🎯 **规则引擎** - 基于 Drools，支持复杂业务规则配置
- 🔄 **决策流程** - 可视化流程编排，灵活的决策链路
- 📊 **数据中台** - 统一数据接入和管理
- 🔐 **权限管理** - 细粒度的权限控制体系
- 📈 **监控告警** - 实时监控和智能告警
- 🐳 **容器化部署** - 一键 Docker Compose 部署

---

## 🚀 快速开始

### 环境要求

- **Docker** >= 20.10
- **Docker Compose** >= 2.0
- **Git**

### 一键启动（推荐）

```bash
# 1. 克隆项目
git clone https://github.com/your-org/RiskSmart-OpenSource.git
cd RiskSmart-OpenSource

# 2. 复制环境变量配置
cp .env.example .env

# 3. 修改配置（可选）
# 编辑 .env 文件，修改数据库密码等配置

# 4. 启动所有服务
docker-compose up -d

# 5. 查看启动日志
docker-compose logs -f

# 6. 访问系统
# 前端地址: http://localhost
# Nacos 控制台: http://localhost:8848/nacos (用户名/密码: nacos/nacos)
```

### 本地开发环境

如果需要本地开发，请参考：[开发环境搭建指南](docs/DEVELOPMENT.md)

---

## 📁 项目结构

```
RiskSmart-OpenSource/
├── risk-smart-backend/              # 后端项目
│   ├── risk-smart-common/           # 公共模块
│   ├── risk-smart-system/           # 系统管理模块
│   ├── risk-smart-decision-manage/  # 决策引擎模块
│   └── risk-smart-data-middle-station/ # 数据中台模块
├── risk-smart-web/                  # 前端项目
├── deploy/                          # 部署配置
│   ├── mysql/                       # MySQL 配置和初始化脚本
│   ├── redis/                       # Redis 配置
│   └── nacos/                       # Nacos 配置
├── docker-compose.yml               # Docker Compose 配置
├── .env.example                     # 环境变量模板
└── README.md                        # 项目说明
```

---

## 🔧 配置说明

### 环境变量

主要配置项在 `.env` 文件中：

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `MYSQL_ROOT_PASSWORD` | MySQL root 密码 | risksmart123 |
| `MYSQL_PORT` | MySQL 端口 | 3306 |
| `REDIS_PORT` | Redis 端口 | 6379 |
| `NACOS_PORT` | Nacos 端口 | 8848 |
| `DECISION_PORT` | 决策引擎服务端口 | 8080 |
| `WEB_PORT` | 前端服务端口 | 80 |

### 数据库初始化

系统启动时会自动执行 `deploy/mysql/init/` 目录下的 SQL 脚本。

默认管理员账号：
- 用户名：`admin`
- 密码：`admin123`

---

## 📚 文档

- [系统架构](docs/ARCHITECTURE.md)
- [开发指南](docs/DEVELOPMENT.md)
- [API 文档](docs/API.md)
- [部署指南](docs/DEPLOYMENT.md)
- [常见问题](docs/FAQ.md)

---

## 🛠️ 技术栈

### 后端

- **框架**：Spring Boot 3.2.5 + Spring Cloud
- **规则引擎**：Drools 7.73.0
- **数据库**：MySQL 8.0 + MyBatis Plus
- **缓存**：Redis
- **配置中心**：Nacos

### 前端

- **框架**：Vue.js / React（根据实际情况）
- **UI 组件**：Element Plus / Ant Design

---

## 🤝 贡献指南

欢迎贡献代码！请阅读 [贡献指南](CONTRIBUTING.md) 了解详情。

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交改动 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

---

## 📄 开源协议

本项目基于 [Apache License 2.0](LICENSE) 开源协议。

---

## 💬 联系我们

- **Issue**: [GitHub Issues](https://github.com/your-org/RiskSmart-OpenSource/issues)
- **邮箱**: support@risksmart.com

---

## ⭐ Star History

如果这个项目对你有帮助，请给我们一个 ⭐️ Star！

[![Star History Chart](https://api.star-history.com/svg?repos=your-org/RiskSmart-OpenSource&type=Date)](https://star-history.com/#your-org/RiskSmart-OpenSource&Date)
