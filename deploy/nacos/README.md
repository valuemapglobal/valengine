# Nacos 配置文件说明
#
# 使用说明:
# 1. Nacos 配置将通过 docker-compose 环境变量注入
# 2. 默认使用 standalone 单机模式
# 3. 数据持久化到 MySQL
#
# 主要配置项:
# - NACOS_AUTH_ENABLE: 是否开启鉴权
# - SPRING_DATASOURCE_PLATFORM: mysql
# - MYSQL_SERVICE_HOST: MySQL 地址
# - MYSQL_SERVICE_DB_NAME: nacos_config
# - MYSQL_SERVICE_USER: 数据库用户
# - MYSQL_SERVICE_PASSWORD: 数据库密码
#
# 详细配置请参考 docker-compose.yml 中的 nacos 服务配置
