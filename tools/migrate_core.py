#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
将 RuoYi common-core 迁移到 risk-smart-common
"""

import os
import shutil

RUOYI_PATH = r"D:\WorkSpace\Code\RuoYi-Cloud"
RISKSMART_PATH = r"D:\WorkSpace\Code\RiskSmart-OpenSource\risk-smart-backend"

REPLACEMENTS = [
    ("com.ruoyi.common.core", "com.risksmart.common.core"),
    ("com.ruoyi.common.redis", "com.risksmart.common.redis"),
    ("com.ruoyi.common.security", "com.risksmart.common.security"),
    ("com.ruoyi.common.log", "com.risksmart.common.log"),
    ("com.ruoyi.system.api", "com.risksmart.system.api"),
    ("com.ruoyi.system", "com.risksmart.system"),
    ("com.ruoyi.auth", "com.risksmart.auth"),
    ("com.ruoyi", "com.risksmart"),
    ("javax.servlet", "jakarta.servlet"),
    ("javax.annotation", "jakarta.annotation"),
    ("javax.validation", "jakarta.validation"),
]

def replace_content(content):
    for old, new in REPLACEMENTS:
        content = content.replace(old, new)
    return content

def copy_java_files(src_base, target_base, package_path):
    """复制并转换 Java 文件"""
    src_path = os.path.join(src_base, "src/main/java", package_path.replace(".", "/"))
    target_path = os.path.join(target_base, "src/main/java", package_path.replace("com.ruoyi", "com.risksmart").replace(".", "/"))

    if not os.path.exists(src_path):
        print(f"  [SKIP] Source not found: {src_path}")
        return 0

    os.makedirs(target_path, exist_ok=True)
    count = 0

    for root, dirs, files in os.walk(src_path):
        for file in files:
            if file.endswith('.java'):
                src_file = os.path.join(root, file)
                rel_path = os.path.relpath(root, src_path)
                target_dir = os.path.join(target_path, rel_path) if rel_path != '.' else target_path
                os.makedirs(target_dir, exist_ok=True)
                target_file = os.path.join(target_dir, file)

                with open(src_file, 'r', encoding='utf-8') as f:
                    content = replace_content(f.read())
                with open(target_file, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"  [OK] {file}")
                count += 1
    return count

def main():
    print("=" * 50)
    print("Migrating common-core to risk-smart-common")
    print("=" * 50)

    common_target = os.path.join(RISKSMART_PATH, "risk-smart-common")

    # common-core -> common
    print("\n[1/1] Merging common-core -> common...")
    count = copy_java_files(
        os.path.join(RUOYI_PATH, "ruoyi-common/ruoyi-common-core"),
        common_target,
        "com.ruoyi.common.core"
    )
    print(f"  Total: {count} files")

    print("\n" + "=" * 50)
    print("Done!")
    print("=" * 50)

if __name__ == "__main__":
    main()
