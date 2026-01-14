# 数据中台服务（Data Middle Station）

数据中台是RiskSmart风控系统的数据层服务，负责对接外部数据源、管理接口配置、提供统一的数据访问入口。

## 目录

- [功能概述](#功能概述)
- [快速开始](#快速开始)
- [接口管理](#接口管理)
- [开放接口（SPI机制）](#开放接口spi机制)
- [配置说明](#配置说明)
- [API文档](#api文档)

---

## 功能概述

### 核心功能

| 功能模块 | 说明 |
|---------|------|
| 接口管理 | 管理外部数据源接口配置，支持多数据源 |
| 字段映射 | 配置接口字段与系统变量的映射关系 |
| 指标管理 | 管理风控指标定义和计算规则 |
| 开放接口 | 提供SPI机制，支持自定义数据源对接 |

### 服务信息

- **服务名**: `risk-smart-data-middle-station`
- **默认端口**: `8990`
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
CREATE DATABASE risk_smart_data_middle_station DEFAULT CHARACTER SET utf8mb4;
```

### 3. 配置Nacos

在Nacos配置中心创建配置文件 `risk-smart-data-middle-station-dev.yml`：

```yaml
server:
  port: 8990

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/risk_smart_data_middle_station?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8
    username: root
    password: your-password
  redis:
    host: localhost
    port: 6379

mybatis-plus:
  mapper-locations: classpath*:mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true

# 业务配置
business:
  apiTokenDecryptPassword: your-secret-key
```

### 4. 启动服务

```bash
cd risk-smart-data-middle-station
mvn spring-boot:run
```

---

## 接口管理

### 接口配置

数据中台支持配置多个外部数据源接口，通过统一的方式进行调用。

#### 接口配置表结构

| 字段 | 说明 |
|-----|------|
| interface_no | 接口编号（唯一标识） |
| interface_name | 接口名称 |
| interface_url | 接口地址 |
| request_method | 请求方法（GET/POST） |
| timeout | 超时时间（毫秒） |
| status | 状态（0-禁用，1-启用） |

#### 字段映射

配置接口返回字段与系统变量的映射关系，支持JSONPath表达式。

---

## 开放接口（SPI机制）

### 概述

数据中台提供了数据源适配器的SPI（Service Provider Interface）机制，允许用户通过实现标准接口来对接自己的数据源，无需修改核心代码。

### 核心组件

| 组件 | 说明 |
|-----|------|
| `DataSourceAdapter` | 数据源适配器接口，用户需要实现此接口 |
| `DataSourceContext` | 调用上下文，包含用户信息、订单号等 |
| `DataSourceAdapterManager` | 适配器管理器，自动发现和管理所有适配器 |
| `OpenApiController` | 开放接口控制器，提供统一的API入口 |

### 快速实现自定义适配器

#### 步骤1：实现DataSourceAdapter接口

```java
package com.yourcompany.adapter;

import com.alibaba.fastjson2.JSONObject;
import com.value.data.openapi.spi.DataSourceAdapter;
import com.value.data.openapi.spi.DataSourceContext;
import org.springframework.stereotype.Component;

@Component
public class MyDataSourceAdapter implements DataSourceAdapter {

    @Override
    public String getName() {
        return "my-datasource";
    }

    @Override
    public String getDescription() {
        return "我的自定义数据源";
    }

    @Override
    public boolean supports(String apiName) {
        // 定义支持的接口，返回true表示支持
        return apiName.startsWith("my_");
    }

    @Override
    public JSONObject invoke(String apiName, JSONObject param, DataSourceContext context) throws Exception {
        // 实现你的数据源调用逻辑
        // context 包含：userId, userName, deptId, orderId, requestIp

        // 调用外部API示例
        String result = HttpUtil.post("https://api.example.com/" + apiName, param.toJSONString());

        // 返回标准格式
        JSONObject response = new JSONObject();
        response.put("code", 0);
        response.put("msg", "success");
        response.put("data", JSONObject.parseObject(result));
        return response;
    }

    @Override
    public int getOrder() {
        return 10; // 优先级，数值越小优先级越高
    }
}
```

#### 步骤2：调用接口

```bash
# 自动选择适配器
POST /openApi/v2/invoke/{apiName}
Content-Type: application/json

{
    "param1": "value1",
    "param2": "value2"
}

# 指定适配器
POST /openApi/v2/invoke/{adapterName}/{apiName}

# 查看可用适配器列表
GET /openApi/v2/adapters
```

### 内置适配器

#### 1. HTTP适配器

通用的HTTP接口对接适配器，支持配置化方式对接第三方RESTful API。

**配置示例：**

```yaml
openapi:
  http:
    enabled: true
    base-url: https://api.example.com
    timeout: 30000
    headers:
      Authorization: Bearer your-token
      Content-Type: application/json
    api-mappings:
      getCompanyInfo: /company/info
      getPersonInfo: /person/info
      queryCredit: /credit/query
```

**使用方式：**

```bash
# 调用已配置的接口
POST /openApi/v2/invoke/getCompanyInfo
{
    "companyName": "某某公司"
}
```

#### 2. 示例适配器

仅供参考学习，启用方式：

```yaml
openapi:
  sample:
    enabled: true
```

### 适配器优先级

当多个适配器都支持同一接口时，按优先级（order值）选择，数值越小优先级越高。

```java
@Override
public int getOrder() {
    return 10; // 优先级
}
```

---

## 配置说明

### bootstrap.yml

```yaml
server:
  port: ${SERVER_PORT:8990}

spring:
  application:
    name: risk-smart-data-middle-station
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
| SERVER_PORT | 服务端口 | 8990 |
| NACOS_SERVER_ADDR | Nacos地址 | localhost:8848 |
| MYSQL_HOST | MySQL地址 | localhost |
| MYSQL_PORT | MySQL端口 | 3306 |
| REDIS_HOST | Redis地址 | localhost |
| REDIS_PORT | Redis端口 | 6379 |

---

## API文档

### 接口管理

| 接口 | 方法 | 说明 |
|-----|------|------|
| `/interfaceManage/list` | GET | 获取接口列表 |
| `/interfaceManage/add` | POST | 新增接口 |
| `/interfaceManage/update` | PUT | 更新接口 |
| `/interfaceManage/delete/{id}` | DELETE | 删除接口 |

### 开放接口

| 接口 | 方法 | 说明 |
|-----|------|------|
| `/openApi/v2/adapters` | GET | 获取可用适配器列表 |
| `/openApi/v2/invoke/{apiName}` | POST | 调用数据源接口（自动选择适配器） |
| `/openApi/v2/invoke/{adapter}/{apiName}` | POST | 调用指定适配器的接口 |

### 指标管理

| 接口 | 方法 | 说明 |
|-----|------|------|
| `/metrics/list` | GET | 获取指标列表 |
| `/metrics/add` | POST | 新增指标 |
| `/metrics/calculate` | POST | 计算指标值 |

---

## 常见问题

### 1. 适配器未被发现

确保适配器类：
- 添加了 `@Component` 注解
- 在Spring的包扫描路径下
- 实现了 `DataSourceAdapter` 接口

### 2. 接口调用超时

检查配置：
- HTTP适配器的 `timeout` 配置
- 网络连通性
- 目标服务响应时间

### 3. 权限校验失败

确保请求携带了有效的认证Token，或配置了正确的 `apiTokenDecryptPassword`。
