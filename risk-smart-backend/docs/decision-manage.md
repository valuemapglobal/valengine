# 决策管理服务（Decision Manage）

决策管理是RiskSmart风控系统的核心服务，负责规则引擎、模型管理、策略配置和风控决策执行。

## 目录

- [功能概述](#功能概述)
- [快速开始](#快速开始)
- [规则模型](#规则模型)
- [评分模型](#评分模型)
- [策略配置](#策略配置)
- [版本管理](#版本管理)
- [配置说明](#配置说明)
- [API文档](#api文档)

---

## 功能概述

### 核心功能

| 功能模块 | 说明 |
|---------|------|
| 规则模型 | 配置风控规则，支持规则组、规则条件、规则动作 |
| 评分模型 | 配置评分卡，支持评分指标、权重、分段 |
| 评级模型 | 配置信用评级，支持等级划分、阈值设置 |
| 额度模型 | 配置授信额度计算规则 |
| 定价模型 | 配置利率定价规则 |
| 策略管理 | 管理业务策略，组合多个模型 |
| 版本控制 | 支持模型版本管理、发布、回滚 |

### 服务信息

- **服务名**: `risk-smart-decision-manage`
- **默认端口**: `8991`
- **配置文件**: `bootstrap.yml` + Nacos配置中心

---

## 快速开始

### 1. 环境要求

- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Nacos 2.0+

### 2. 配置数据库

```sql
-- 创建数据库
CREATE DATABASE risk_smart_decision_manage DEFAULT CHARACTER SET utf8mb4;

-- 导入初始化脚本
-- 参见 deploy/mysql/init/ 目录下的SQL文件
```

### 3. 配置Nacos

在Nacos配置中心创建配置文件 `risk-smart-decision-manage-dev.yml`：

```yaml
server:
  port: 8991

spring:
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    database: 0

  datasource:
    druid:
      master:
        url: jdbc:mysql://localhost:3306/risk_smart_decision_manage?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8
        username: root
        password: your-password
      initialSize: 5
      minIdle: 10
      maxActive: 20

mybatis-plus:
  mapper-locations: classpath*:mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true

# 业务配置
business:
  apiTokenDecryptPassword: your-secret-key

# 服务调用配置
service:
  engine:
    ruleUrl: http://localhost:8088/rule/execute
  interfaceManage:
    fieldsUrl: http://localhost:8990/interfaceManage/
    interfaceRequestUrl: http://localhost:8990/interfaceRequest/api
```

### 4. 启动服务

```bash
cd risk-smart-decision-manage
mvn spring-boot:run
```

---

## 规则模型

### 概述

规则模型是风控决策的基础，支持灵活的规则配置和组合。

### 规则层级

```
产品（Product）
└── 业务（Business）
    └── 策略（Strategy）
        └── 规则组（RuleGroup）
            └── 规则（Rule）
                └── 条件（Condition）
```

### 规则类型

| 类型 | 说明 | 适用场景 |
|-----|------|---------|
| 准入规则 | 判断是否准入 | 黑名单、白名单、基础条件 |
| 反欺诈规则 | 欺诈风险识别 | 设备指纹、行为分析、关联分析 |
| 信用规则 | 信用评估 | 征信查询、多头借贷、负债分析 |

### 规则配置示例

```json
{
  "ruleName": "年龄限制规则",
  "ruleCode": "AGE_LIMIT",
  "ruleType": "准入",
  "conditions": [
    {
      "field": "age",
      "operator": ">=",
      "value": "18"
    },
    {
      "field": "age",
      "operator": "<=",
      "value": "60"
    }
  ],
  "action": "REJECT",
  "actionMessage": "年龄不符合要求"
}
```

### 规则条件运算符

| 运算符 | 说明 | 示例 |
|-------|------|------|
| `==` | 等于 | `status == '1'` |
| `!=` | 不等于 | `status != '0'` |
| `>` | 大于 | `age > 18` |
| `>=` | 大于等于 | `score >= 60` |
| `<` | 小于 | `amount < 10000` |
| `<=` | 小于等于 | `debt <= 50000` |
| `in` | 包含 | `province in ['北京','上海']` |
| `not in` | 不包含 | `industry not in ['高危行业']` |
| `contains` | 字符串包含 | `name contains '公司'` |
| `is null` | 为空 | `phone is null` |
| `is not null` | 不为空 | `idCard is not null` |

---

## 评分模型

### 概述

评分模型用于计算风险评分，支持多种评分方式。

### 评分卡配置

```json
{
  "scorecardName": "信用评分卡",
  "scorecardCode": "CREDIT_SCORE",
  "baseScore": 600,
  "maxScore": 950,
  "minScore": 350,
  "indicators": [
    {
      "name": "年龄",
      "field": "age",
      "weight": 0.15,
      "segments": [
        {"min": 18, "max": 25, "score": 60},
        {"min": 25, "max": 35, "score": 80},
        {"min": 35, "max": 45, "score": 90},
        {"min": 45, "max": 60, "score": 70}
      ]
    },
    {
      "name": "月收入",
      "field": "monthlyIncome",
      "weight": 0.25,
      "segments": [
        {"min": 0, "max": 5000, "score": 50},
        {"min": 5000, "max": 10000, "score": 70},
        {"min": 10000, "max": 20000, "score": 85},
        {"min": 20000, "max": null, "score": 95}
      ]
    }
  ]
}
```

### 评分计算

```
最终评分 = 基础分 + Σ(指标评分 × 指标权重)
```

---

## 策略配置

### 策略类型

| 类型 | 说明 |
|-----|------|
| 准入策略 | 控制业务准入条件 |
| 反欺诈策略 | 欺诈风险识别和拦截 |
| 信用策略 | 信用评估和授信 |
| 定价策略 | 利率和费用定价 |

### 策略执行流程

```
请求 → 准入策略 → 反欺诈策略 → 信用策略 → 定价策略 → 决策结果
         ↓           ↓            ↓           ↓
       拒绝        拒绝/人工     评分/评级    利率/额度
```

### 决策结果

| 结果码 | 说明 |
|-------|------|
| PASS | 通过 |
| REJECT | 拒绝 |
| REVIEW | 人工审核 |
| SUPPLEMENT | 补充材料 |

---

## 版本管理

### 版本号规则

```
{产品简称}-{业务简称}-{类型}-V{版本号}-{日期}-{序号}

示例：BDCS-ZR-C-V1.0-20240114-1
- BDCS: 产品简称（拼音首字母）
- ZR: 业务简称（准入）
- C: 类型（C-企业，P-个人）
- V1.0: 版本号
- 20240114: 日期
- 1: 当日序号
```

### 版本状态

| 状态 | 说明 |
|-----|------|
| 草稿 | 编辑中，未发布 |
| 已发布 | 正式启用 |
| 已停用 | 停止使用 |
| 已归档 | 历史版本 |

### 版本操作

| 操作 | 说明 |
|-----|------|
| 发布 | 将草稿版本发布为正式版本 |
| 回滚 | 回退到指定历史版本 |
| 复制 | 基于当前版本创建新版本 |
| 对比 | 对比两个版本的差异 |

---

## 配置说明

### bootstrap.yml

```yaml
server:
  port: ${SERVER_PORT:8991}

spring:
  application:
    name: risk-smart-decision-manage
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
  cloud:
    nacos:
      config:
        server-addr: ${NACOS_SERVER_ADDR:localhost:8848}
        username: ${NACOS_USERNAME:nacos}
        password: ${NACOS_PASSWORD:nacos}
        file-extension: yml
      discovery:
        server-addr: ${NACOS_SERVER_ADDR:localhost:8848}

# 业务配置（兜底默认值）
business:
  apiTokenDecryptPassword: ${API_TOKEN_DECRYPT_PASSWORD:RiskSmart@2024}
```

### 环境变量

| 变量名 | 说明 | 默认值 |
|-------|------|--------|
| SERVER_PORT | 服务端口 | 8991 |
| NACOS_SERVER_ADDR | Nacos地址 | localhost:8848 |
| MYSQL_HOST | MySQL地址 | localhost |
| MYSQL_PORT | MySQL端口 | 3306 |
| REDIS_HOST | Redis地址 | localhost |
| REDIS_PORT | Redis端口 | 6379 |

### 服务调用配置

```yaml
service:
  engine:
    # 规则引擎执行地址
    ruleUrl: http://localhost:8088/rule/execute
    ruleflUrl: http://localhost:8088/rulefl/execute
  interfaceManage:
    # 数据中台请求地址
    fieldsUrl: http://localhost:8990/interfaceManage/
    interfaceRequestUrl: http://localhost:8990/interfaceRequest/api
    interfaceDetailUrl: http://localhost:8990/interfaceManage/getInterfaceDetail
```

---

## API文档

### 产品管理

| 接口 | 方法 | 说明 |
|-----|------|------|
| `/product/list` | GET | 获取产品列表 |
| `/product/add` | POST | 新增产品 |
| `/product/update` | PUT | 更新产品 |
| `/product/delete/{id}` | DELETE | 删除产品 |

### 业务管理

| 接口 | 方法 | 说明 |
|-----|------|------|
| `/business/list` | GET | 获取业务列表 |
| `/business/add` | POST | 新增业务 |
| `/business/update` | PUT | 更新业务 |

### 规则管理

| 接口 | 方法 | 说明 |
|-----|------|------|
| `/rule/list` | GET | 获取规则列表 |
| `/rule/add` | POST | 新增规则 |
| `/rule/update` | PUT | 更新规则 |
| `/rule/delete/{id}` | DELETE | 删除规则 |
| `/rule/copy/{id}` | POST | 复制规则 |

### 规则组管理

| 接口 | 方法 | 说明 |
|-----|------|------|
| `/ruleGroup/list` | GET | 获取规则组列表 |
| `/ruleGroup/add` | POST | 新增规则组 |
| `/ruleGroup/update` | PUT | 更新规则组 |

### 策略管理

| 接口 | 方法 | 说明 |
|-----|------|------|
| `/strategy/list` | GET | 获取策略列表 |
| `/strategy/add` | POST | 新增策略 |
| `/strategy/publish/{id}` | POST | 发布策略 |
| `/strategy/rollback/{id}` | POST | 回滚策略 |

### 决策执行

| 接口 | 方法 | 说明 |
|-----|------|------|
| `/decision/execute` | POST | 执行决策 |
| `/decision/test` | POST | 测试决策（不保存结果） |
| `/decision/batch` | POST | 批量决策 |

### 版本管理

| 接口 | 方法 | 说明 |
|-----|------|------|
| `/version/list` | GET | 获取版本列表 |
| `/version/publish/{id}` | POST | 发布版本 |
| `/version/rollback/{id}` | POST | 回滚版本 |
| `/version/compare` | POST | 版本对比 |

---

## 常见问题

### 1. 规则执行报错

检查：
- 规则条件配置是否正确
- 字段名是否与入参匹配
- 运算符使用是否正确

### 2. 评分结果异常

检查：
- 评分指标权重总和是否为1
- 分段配置是否覆盖所有情况
- 基础分和上下限配置

### 3. 版本发布失败

确保：
- 所有规则配置完整
- 没有循环依赖
- 关联的数据源接口可用

### 4. 决策超时

优化建议：
- 减少不必要的规则
- 优化数据源接口响应时间
- 调整超时配置
