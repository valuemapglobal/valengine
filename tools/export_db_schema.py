#!/usr/bin/env python3
"""
导出 RiskSmart 数据库 Schema
生成完整的 SQL 初始化脚本
"""
import pymysql
from datetime import datetime

# 数据库配置
DB_CONFIG = {
    "host": "192.168.1.33",
    "port": 3306,
    "user": "root",
    "password": "Vm123456",
    "charset": "utf8mb4"
}

# 要导出的数据库和目标库名映射
DB_MAPPING = {
    "risk_smart_manage": "risk_smart",  # 决策引擎库 -> 统一为 risk_smart
    "risk_smart_data_middle_station_dev": "risk_smart"  # 中台库 -> 合并到 risk_smart
}

# 输出文件
OUTPUT_FILE = r"D:\WorkSpace\Code\RiskSmart-OpenSource\deploy\mysql\init\00-risksmart-schema.sql"

def get_create_table(cursor, db_name, table_name):
    """获取建表语句"""
    cursor.execute(f"SHOW CREATE TABLE `{db_name}`.`{table_name}`")
    result = cursor.fetchone()
    if result:
        return result[1]
    return None

def get_tables(cursor, db_name):
    """获取所有表名"""
    cursor.execute(f"""
        SELECT table_name, table_comment, table_type
        FROM information_schema.tables
        WHERE table_schema = '{db_name}'
        ORDER BY table_name
    """)
    return cursor.fetchall()

def export_schema():
    """导出完整 Schema"""
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()

    all_tables = {}  # 用于去重（两个库可能有同名表）

    # 收集所有表
    for source_db, target_db in DB_MAPPING.items():
        print(f"\n[*] Analyzing {source_db}...")
        tables = get_tables(cursor, source_db)

        for table_name, table_comment, table_type in tables:
            if table_type == 'VIEW':
                print(f"  [SKIP] View: {table_name}")
                continue

            if table_name in all_tables:
                print(f"  [DUP] Already exists: {table_name}")
                continue

            create_sql = get_create_table(cursor, source_db, table_name)
            if create_sql:
                all_tables[table_name] = {
                    "source_db": source_db,
                    "comment": table_comment or "",
                    "create_sql": create_sql
                }
                print(f"  [OK] {table_name}")

    cursor.close()
    conn.close()

    # 生成 SQL 文件
    print(f"\n[*] Generating SQL file...")

    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        f.write(f"""-- ============================================================
-- RiskSmart 数据库初始化脚本
-- 生成时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}
-- 表数量: {len(all_tables)}
-- ============================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `risk_smart`
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE `risk_smart`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

""")

        # 按表名排序输出
        for table_name in sorted(all_tables.keys()):
            info = all_tables[table_name]
            f.write(f"""
-- -----------------------------------------------------------
-- 表: {table_name}
-- 来源: {info['source_db']}
-- 说明: {info['comment']}
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `{table_name}`;
{info['create_sql']};

""")

        f.write("""
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 初始化完成
-- ============================================================
""")

    print(f"\n[DONE] Exported {len(all_tables)} tables")
    print(f"[FILE] {OUTPUT_FILE}")

    return len(all_tables)

if __name__ == "__main__":
    try:
        export_schema()
    except Exception as e:
        print(f"[ERROR] {e}")
        import traceback
        traceback.print_exc()
