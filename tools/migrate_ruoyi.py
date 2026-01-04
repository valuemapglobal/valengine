#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
RuoYi-Cloud 代码批量移植脚本
将 RuoYi-Cloud 代码移植到 RiskSmart-OpenSource 项目
"""

import os
import shutil
import re
from pathlib import Path

# 源项目和目标项目路径
RUOYI_PATH = r"D:\WorkSpace\Code\RuoYi-Cloud"
RISKSMART_PATH = r"D:\WorkSpace\Code\RiskSmart-OpenSource\risk-smart-backend"

# 包名替换规则
REPLACEMENTS = [
    # 包名替换
    ("com.ruoyi.common.core", "com.risksmart.common.core"),
    ("com.ruoyi.common.redis", "com.risksmart.common.redis"),
    ("com.ruoyi.common.security", "com.risksmart.common.security"),
    ("com.ruoyi.common.log", "com.risksmart.common.log"),
    ("com.ruoyi.system.api", "com.risksmart.system.api"),
    ("com.ruoyi.system", "com.risksmart.system"),
    ("com.ruoyi.auth", "com.risksmart.auth"),
    ("com.ruoyi.gateway", "com.risksmart.gateway"),
    ("com.ruoyi", "com.risksmart"),

    # Spring Boot 3.x 兼容性
    ("javax.servlet", "jakarta.servlet"),
    ("javax.annotation", "jakarta.annotation"),
    ("javax.validation", "jakarta.validation"),

    # 模块名替换
    ("ruoyi-common-core", "risk-smart-common"),
    ("ruoyi-common-redis", "risk-smart-common-redis"),
    ("ruoyi-common-security", "risk-smart-common-security"),
    ("ruoyi-common-log", "risk-smart-common-log"),
    ("ruoyi-api-system", "risk-smart-common"),
    ("ruoyi-system", "risk-smart-system"),
    ("ruoyi-auth", "risk-smart-auth"),
    ("ruoyi-gateway", "risk-smart-gateway"),
]

def replace_content(content):
    """替换文件内容中的包名和导入"""
    for old, new in REPLACEMENTS:
        content = content.replace(old, new)
    return content

def get_target_path(src_path, src_base, target_base):
    """计算目标文件路径"""
    rel_path = os.path.relpath(src_path, src_base)
    # 替换包路径
    rel_path = rel_path.replace("com/ruoyi", "com/risksmart")
    return os.path.join(target_base, rel_path)

def copy_and_transform(src_file, target_file):
    """复制并转换文件"""
    os.makedirs(os.path.dirname(target_file), exist_ok=True)

    try:
        with open(src_file, 'r', encoding='utf-8') as f:
            content = f.read()

        content = replace_content(content)

        with open(target_file, 'w', encoding='utf-8') as f:
            f.write(content)

        print(f"  [OK] {os.path.basename(target_file)}")
        return True
    except Exception as e:
        print(f"  [FAIL] {os.path.basename(src_file)}: {e}")
        return False

def migrate_module(src_module_path, target_module_path, src_java_subpath="src/main/java"):
    """移植一个模块的所有 Java 文件"""
    src_java_path = os.path.join(src_module_path, src_java_subpath)
    target_java_path = os.path.join(target_module_path, src_java_subpath)

    if not os.path.exists(src_java_path):
        print(f"  源路径不存在: {src_java_path}")
        return 0

    count = 0
    for root, dirs, files in os.walk(src_java_path):
        for file in files:
            if file.endswith('.java'):
                src_file = os.path.join(root, file)
                target_file = get_target_path(src_file, src_java_path, target_java_path)
                if copy_and_transform(src_file, target_file):
                    count += 1

    return count

def migrate_resources(src_module_path, target_module_path):
    """移植资源文件"""
    src_res_path = os.path.join(src_module_path, "src/main/resources")
    target_res_path = os.path.join(target_module_path, "src/main/resources")

    if not os.path.exists(src_res_path):
        return 0

    count = 0
    for root, dirs, files in os.walk(src_res_path):
        for file in files:
            src_file = os.path.join(root, file)
            rel_path = os.path.relpath(src_file, src_res_path)
            target_file = os.path.join(target_res_path, rel_path)

            os.makedirs(os.path.dirname(target_file), exist_ok=True)

            # 文本文件需要替换内容
            if file.endswith(('.xml', '.yml', '.yaml', '.properties', '.factories', '.imports')):
                if copy_and_transform(src_file, target_file):
                    count += 1
            else:
                shutil.copy2(src_file, target_file)
                count += 1

    return count

def main():
    print("=" * 60)
    print("RuoYi-Cloud -> RiskSmart-OpenSource 代码移植脚本")
    print("=" * 60)

    # 1. 移植 common-core 工具类到 risk-smart-common
    print("\n[1/7] 补充 risk-smart-common 模块工具类...")
    count = migrate_module(
        os.path.join(RUOYI_PATH, "ruoyi-common/ruoyi-common-core"),
        os.path.join(RISKSMART_PATH, "risk-smart-common")
    )
    print(f"  共移植 {count} 个文件")

    # 2. 移植 api-system 实体类到 risk-smart-common
    print("\n[2/7] 补充 risk-smart-common 模块 API 实体类...")
    count = migrate_module(
        os.path.join(RUOYI_PATH, "ruoyi-api/ruoyi-api-system"),
        os.path.join(RISKSMART_PATH, "risk-smart-common")
    )
    print(f"  共移植 {count} 个文件")

    # 3. 移植 common-security
    print("\n[3/7] 移植 risk-smart-common-security 模块...")
    count = migrate_module(
        os.path.join(RUOYI_PATH, "ruoyi-common/ruoyi-common-security"),
        os.path.join(RISKSMART_PATH, "risk-smart-common-security")
    )
    count += migrate_resources(
        os.path.join(RUOYI_PATH, "ruoyi-common/ruoyi-common-security"),
        os.path.join(RISKSMART_PATH, "risk-smart-common-security")
    )
    print(f"  共移植 {count} 个文件")

    # 4. 移植 common-log
    print("\n[4/7] 移植 risk-smart-common-log 模块...")
    count = migrate_module(
        os.path.join(RUOYI_PATH, "ruoyi-common/ruoyi-common-log"),
        os.path.join(RISKSMART_PATH, "risk-smart-common-log")
    )
    count += migrate_resources(
        os.path.join(RUOYI_PATH, "ruoyi-common/ruoyi-common-log"),
        os.path.join(RISKSMART_PATH, "risk-smart-common-log")
    )
    print(f"  共移植 {count} 个文件")

    # 5. 移植 auth 服务
    print("\n[5/7] 移植 risk-smart-auth 服务...")
    count = migrate_module(
        os.path.join(RUOYI_PATH, "ruoyi-auth"),
        os.path.join(RISKSMART_PATH, "risk-smart-auth")
    )
    count += migrate_resources(
        os.path.join(RUOYI_PATH, "ruoyi-auth"),
        os.path.join(RISKSMART_PATH, "risk-smart-auth")
    )
    print(f"  共移植 {count} 个文件")

    # 6. 移植 system 模块
    print("\n[6/7] 移植 risk-smart-system 模块...")
    count = migrate_module(
        os.path.join(RUOYI_PATH, "ruoyi-modules/ruoyi-system"),
        os.path.join(RISKSMART_PATH, "risk-smart-system")
    )
    count += migrate_resources(
        os.path.join(RUOYI_PATH, "ruoyi-modules/ruoyi-system"),
        os.path.join(RISKSMART_PATH, "risk-smart-system")
    )
    print(f"  共移植 {count} 个文件")

    # 7. 移植 gateway
    print("\n[7/7] 移植 risk-smart-gateway 服务...")
    count = migrate_module(
        os.path.join(RUOYI_PATH, "ruoyi-gateway"),
        os.path.join(RISKSMART_PATH, "risk-smart-gateway")
    )
    count += migrate_resources(
        os.path.join(RUOYI_PATH, "ruoyi-gateway"),
        os.path.join(RISKSMART_PATH, "risk-smart-gateway")
    )
    print(f"  共移植 {count} 个文件")

    print("\n" + "=" * 60)
    print("移植完成！")
    print("=" * 60)
    print("\n后续步骤：")
    print("1. 检查并修复编译错误")
    print("2. 更新父 pom.xml 添加新模块")
    print("3. 创建各模块的 pom.xml")
    print("4. 配置 Nacos 和数据库")

if __name__ == "__main__":
    main()
