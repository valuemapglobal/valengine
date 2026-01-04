#!/usr/bin/env python3
"""
导出 RuoYi 核心表 Schema
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

SOURCE_DB = "vm_ry_cloud_uat"

# RuoYi 核心表（只导出这些）
CORE_TABLES = [
    "sys_user",         # 用户信息表
    "sys_dept",         # 部门表
    "sys_role",         # 角色信息表
    "sys_menu",         # 菜单权限表
    "sys_user_role",    # 用户和角色关联表
    "sys_role_menu",    # 角色和菜单关联表
    "sys_role_dept",    # 角色和部门关联表
    "sys_dict_type",    # 字典类型表
    "sys_dict_data",    # 字典数据表
    "sys_config",       # 参数配置表
    "sys_post",         # 岗位信息表
    "sys_user_post",    # 用户与岗位关联表
]

OUTPUT_FILE = r"D:\WorkSpace\Code\RiskSmart-OpenSource\deploy\mysql\init\01-ruoyi-core-schema.sql"

def get_create_table(cursor, db_name, table_name):
    """获取建表语句"""
    cursor.execute(f"SHOW CREATE TABLE `{db_name}`.`{table_name}`")
    result = cursor.fetchone()
    if result:
        return result[1]
    return None

def get_table_comment(cursor, db_name, table_name):
    """获取表注释"""
    cursor.execute(f"""
        SELECT table_comment
        FROM information_schema.tables
        WHERE table_schema = '{db_name}' AND table_name = '{table_name}'
    """)
    result = cursor.fetchone()
    return result[0] if result else ""

def export_schema():
    """导出核心表 Schema"""
    conn = pymysql.connect(**DB_CONFIG)
    cursor = conn.cursor()

    tables_data = []

    print(f"[*] Exporting RuoYi core tables from {SOURCE_DB}...")

    for table_name in CORE_TABLES:
        try:
            create_sql = get_create_table(cursor, SOURCE_DB, table_name)
            comment = get_table_comment(cursor, SOURCE_DB, table_name)

            if create_sql:
                tables_data.append({
                    "name": table_name,
                    "comment": comment,
                    "create_sql": create_sql
                })
                print(f"  [OK] {table_name} ({comment})")
            else:
                print(f"  [SKIP] {table_name} - Not found")
        except Exception as e:
            print(f"  [ERROR] {table_name}: {e}")

    cursor.close()
    conn.close()

    # 生成 SQL 文件
    print(f"\n[*] Generating SQL file...")

    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        f.write(f"""-- ============================================================
-- RuoYi 核心表初始化脚本
-- 生成时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}
-- 表数量: {len(tables_data)}
-- ============================================================

USE `risk_smart`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

""")

        for table in tables_data:
            f.write(f"""
-- -----------------------------------------------------------
-- 表: {table['name']}
-- 说明: {table['comment']}
-- -----------------------------------------------------------
DROP TABLE IF EXISTS `{table['name']}`;
{table['create_sql']};

""")

        f.write("""
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- RuoYi 核心表初始化完成
-- ============================================================
""")

    print(f"\n[DONE] Exported {len(tables_data)} tables")
    print(f"[FILE] {OUTPUT_FILE}")

if __name__ == "__main__":
    try:
        export_schema()
    except Exception as e:
        print(f"[ERROR] {e}")
        import traceback
        traceback.print_exc()
