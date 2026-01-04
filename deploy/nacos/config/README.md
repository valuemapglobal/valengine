# Nacos 配置说明

本目录用于存放 RiskSmart 在 Nacos 配置中心中使用的配置文件（便于阅读、手动导入、版本管理）。

## 目录结构

```
deploy/nacos/config/
├── README.md                              # 本说明
├── application-dev.yml                    # 公共配置（shared-config）
├── risk-smart-gateway-dev.yml             # 网关服务配置（路由等）
├── risk-smart-system-dev.yml              # 系统服务配置
├── risk-smart-data-middle-station-dev.yml # 数据中台服务配置
└── risk-smart-decision-manage-dev.yml     # 决策服务配置
```

## 配置从哪里来

项目提供了两种途径：

1. **Docker 自动初始化（推荐）**：首次启动 `docker-compose` 时，MySQL 会执行 `deploy/mysql/init/06-nacos-config-data.sql`，把上述 DataId 写入 `nacos_config.config_info`，Nacos 会从数据库读取配置。
2. **手动导入**：使用 Nacos 控制台将 `deploy/nacos/config/` 下的文件发布到 Nacos（`DEFAULT_GROUP`）。

## Docker 自动初始化

在仓库根目录执行：

```bash
docker-compose up -d
```

然后访问 Nacos 控制台：`http://localhost:8848/nacos`（默认账号密码：`nacos/nacos`）。

## 手动导入（控制台）

1. 打开 `http://localhost:8848/nacos` 登录
2. 进入「配置管理」->「配置列表」
3. 按 DataId 创建/发布（Group：`DEFAULT_GROUP`）：
   - `application-dev.yml`
   - `risk-smart-gateway-dev.yml`
   - `risk-smart-system-dev.yml`
   - `risk-smart-data-middle-station-dev.yml`
   - `risk-smart-decision-manage-dev.yml`

