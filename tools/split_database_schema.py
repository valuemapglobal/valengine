#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
数据库拆分脚本
将 00-risksmart-schema.sql 拆分为三个独立的数据库脚本
"""

import re
import os

# 定义表归属规则
TABLE_MAPPING = {
    'risk_smart_system': [
        # 系统表在 01-system-schema.sql 中已单独处理
    ],
    'risk_smart_data': [
        # 数据中台表
        'interface_dept_app',
        'interface_exception_log',
        'interface_fie_id_manage',  # 需要重命名为 interface_field_id_manage
        'interface_log',
        'interface_manage',
        'interface_permissions_manage',
        'interface_source_manage',
        'interface_user',
        'feature_attribute',
        'feature_module',
        'metrics_attribute',
        'metrics_module',
        'rde_risk_variable_group',
        'rde_risk_variable_record',
        'rde_risk_variable_theme',
        'rde_risk_source_group',
        'rde_risk_source_logic',
        'rde_risk_source_record',
        'rde_risk_warn_result_log',
    ],
    'risk_smart_manage': [
        # 决策管理表 - 所有其他表
    ]
}

# 需要排除的表（已在其他脚本中处理）
EXCLUDE_TABLES = [
    'sys_user', 'sys_dept', 'sys_role', 'sys_menu',
    'sys_user_role', 'sys_role_menu', 'sys_role_dept',
    'sys_dict_type', 'sys_dict_data', 'sys_config',
    'sys_post', 'sys_user_post', 'sys_oper_log', 'sys_run_log'
]

def read_sql_file(filepath):
    """读取SQL文件"""
    with open(filepath, 'r', encoding='utf-8') as f:
        return f.read()

def extract_tables(content):
    """提取所有表定义"""
    # 匹配表定义块
    pattern = r'-- -----------------------------------------------------------\n-- 表: (\w+)\n-- 来源: (\w+)\n-- 说明: (.*?)\n-- -----------------------------------------------------------\nDROP TABLE IF EXISTS `\1`;(?:\nCREATE TABLE[^;]+;)?'

    tables = {}

    # 使用更简单的方式解析
    lines = content.split('\n')
    current_table = None
    current_source = None
    current_desc = None
    table_content = []
    in_table = False

    for i, line in enumerate(lines):
        if line.startswith('-- 表: '):
            current_table = line.replace('-- 表: ', '').strip()
        elif line.startswith('-- 来源: '):
            current_source = line.replace('-- 来源: ', '').strip()
        elif line.startswith('-- 说明: '):
            current_desc = line.replace('-- 说明: ', '').strip()
        elif line.startswith('DROP TABLE IF EXISTS'):
            in_table = True
            table_content = [line]
        elif in_table:
            table_content.append(line)
            if line.strip().endswith(';') and ('ENGINE=' in line or line.strip() == ';'):
                # 表定义结束
                if current_table:
                    tables[current_table] = {
                        'source': current_source,
                        'description': current_desc,
                        'content': '\n'.join(table_content)
                    }
                in_table = False
                table_content = []
                current_table = None

    return tables

def categorize_tables(tables):
    """将表分类到不同数据库"""
    data_tables = {}
    manage_tables = {}

    data_table_names = TABLE_MAPPING['risk_smart_data']

    for table_name, table_info in tables.items():
        if table_name in EXCLUDE_TABLES:
            continue

        if table_name in data_table_names:
            data_tables[table_name] = table_info
        else:
            manage_tables[table_name] = table_info

    return data_tables, manage_tables

def generate_schema_sql(database_name, tables, description):
    """生成数据库Schema SQL"""
    lines = [
        f'-- ============================================================',
        f'-- {description}',
        f'-- 数据库: {database_name}',
        f'-- 表数量: {len(tables)}',
        f'-- ============================================================',
        f'',
        f'USE `{database_name}`;',
        f'',
        f'SET NAMES utf8mb4;',
        f'SET FOREIGN_KEY_CHECKS = 0;',
        f''
    ]

    for table_name in sorted(tables.keys()):
        table_info = tables[table_name]
        lines.append(f'-- -----------------------------------------------------------')
        lines.append(f'-- 表: {table_name}')
        if table_info.get('description'):
            lines.append(f'-- 说明: {table_info["description"]}')
        lines.append(f'-- -----------------------------------------------------------')

        content = table_info['content']
        # 修复 fie_id -> field_id
        content = content.replace('fie_id', 'field_id')
        content = content.replace('FieId', 'FieldId')

        lines.append(content)
        lines.append('')

    lines.append('')
    lines.append('SET FOREIGN_KEY_CHECKS = 1;')
    lines.append('')
    lines.append(f'-- ============================================================')
    lines.append(f'-- {database_name} 初始化完成')
    lines.append(f'-- ============================================================')

    return '\n'.join(lines)

def main():
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    sql_dir = os.path.join(base_dir, 'deploy', 'mysql', 'init')

    # 读取原始SQL
    original_sql = read_sql_file(os.path.join(sql_dir, '00-risksmart-schema.sql'))

    # 提取表
    tables = extract_tables(original_sql)
    print(f'共解析到 {len(tables)} 个表')

    # 分类
    data_tables, manage_tables = categorize_tables(tables)
    print(f'数据中台表: {len(data_tables)} 个')
    print(f'决策管理表: {len(manage_tables)} 个')

    # 生成数据中台SQL
    data_sql = generate_schema_sql(
        'risk_smart_data',
        data_tables,
        'RiskSmart Data Middle Station 数据库初始化脚本'
    )
    with open(os.path.join(sql_dir, '02-data-schema.sql'), 'w', encoding='utf-8') as f:
        f.write(data_sql)
    print(f'已生成: 02-data-schema.sql')

    # 生成决策管理SQL
    manage_sql = generate_schema_sql(
        'risk_smart_manage',
        manage_tables,
        'RiskSmart Decision Manage 数据库初始化脚本'
    )
    with open(os.path.join(sql_dir, '03-manage-schema.sql'), 'w', encoding='utf-8') as f:
        f.write(manage_sql)
    print(f'已生成: 03-manage-schema.sql')

    # 打印表分类详情
    print('\n=== 数据中台表 ===')
    for name in sorted(data_tables.keys()):
        print(f'  - {name}')

    print('\n=== 决策管理表 ===')
    for name in sorted(manage_tables.keys()):
        print(f'  - {name}')

if __name__ == '__main__':
    main()
