#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
清理 SQL 脚本中指定的表
删除审批流、模型回溯、预警相关的表
"""

import re

# 需要删除的表
TABLES_TO_REMOVE = [
    # 审批流
    'approval_batches',
    'approval_change_logs',
    'approval_compare_control',
    'approval_compare_source',
    'approval_submission_history',
    'approval_user_permission',
    # 预警
    'monitor_contrast',
    'monitor_data',
    'monitor_log',
    'monitor_warning_record',
    'monitor_warning_target',
    'monitor_warning_task',
    'risk_type_info',
    # 回溯
    'traceback_task_details',
    'traceback_tasks',
]

def clean_sql_file(input_file, output_file):
    with open(input_file, 'r', encoding='utf-8') as f:
        content = f.read()

    original_length = len(content)
    removed_tables = []

    for table in TABLES_TO_REMOVE:
        # 匹配 CREATE TABLE 语句（包括注释和完整的表定义）
        # 模式: 可选的注释 + CREATE TABLE `table_name` ( ... ) ...;
        pattern = r'(?:--[^\n]*\n)*CREATE TABLE `' + re.escape(table) + r'`[^;]+;[\s]*'

        if re.search(pattern, content, re.IGNORECASE | re.DOTALL):
            content = re.sub(pattern, '', content, flags=re.IGNORECASE | re.DOTALL)
            removed_tables.append(table)
            print(f'[OK] Removed table: {table}')
        else:
            print(f'[SKIP] Table not found: {table}')

    # 清理多余的空行
    content = re.sub(r'\n{3,}', '\n\n', content)

    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(content)

    new_length = len(content)
    print(f'\n[Summary]')
    print(f'  Removed {len(removed_tables)} tables')
    print(f'  Original size: {original_length:,} bytes')
    print(f'  New size: {new_length:,} bytes')
    print(f'  Reduced: {original_length - new_length:,} bytes')

if __name__ == '__main__':
    input_file = r'D:\WorkSpace\Code\RiskSmart-OpenSource\deploy\mysql\init\00-risksmart-schema.sql'
    output_file = input_file  # 直接覆盖

    clean_sql_file(input_file, output_file)
    print('\n[Done] SQL file cleaned successfully!')
