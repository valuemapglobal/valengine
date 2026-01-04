#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Migrate missing domain classes from RuoYi-Cloud to RiskSmart"""

import os
import shutil
import re

RUOYI_BASE = "D:/WorkSpace/Code/RuoYi-Cloud"
TARGET_BASE = "D:/WorkSpace/Code/RiskSmart-OpenSource/risk-smart-backend/risk-smart-system/src/main/java/com/risksmart/system/domain"

# Files to migrate
files_to_migrate = [
    # From ruoyi-modules/ruoyi-system
    (f"{RUOYI_BASE}/ruoyi-modules/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysConfig.java", "SysConfig.java"),
    (f"{RUOYI_BASE}/ruoyi-modules/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysMenu.java", "SysMenu.java"),
    (f"{RUOYI_BASE}/ruoyi-modules/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysNotice.java", "SysNotice.java"),
    (f"{RUOYI_BASE}/ruoyi-modules/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysPost.java", "SysPost.java"),
    (f"{RUOYI_BASE}/ruoyi-modules/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysRoleDept.java", "SysRoleDept.java"),
    (f"{RUOYI_BASE}/ruoyi-modules/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysRoleMenu.java", "SysRoleMenu.java"),
    (f"{RUOYI_BASE}/ruoyi-modules/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysUserPost.java", "SysUserPost.java"),
    (f"{RUOYI_BASE}/ruoyi-modules/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysUserRole.java", "SysUserRole.java"),
    # From ruoyi-api/ruoyi-api-system
    (f"{RUOYI_BASE}/ruoyi-api/ruoyi-api-system/src/main/java/com/ruoyi/system/api/domain/SysLogininfor.java", "SysLogininfor.java"),
    (f"{RUOYI_BASE}/ruoyi-api/ruoyi-api-system/src/main/java/com/ruoyi/system/api/domain/SysOperLog.java", "SysOperLog.java"),
    (f"{RUOYI_BASE}/ruoyi-api/ruoyi-api-system/src/main/java/com/ruoyi/system/api/domain/SysRole.java", "SysRole.java"),
]

def migrate_file(src_path, target_name):
    """Migrate a single file"""
    target_path = os.path.join(TARGET_BASE, target_name)

    if os.path.exists(target_path):
        print(f"[SKIP] {target_name} already exists")
        return False

    if not os.path.exists(src_path):
        print(f"[FAIL] Source not found: {src_path}")
        return False

    with open(src_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # Replace package names
    content = re.sub(r'package com\.ruoyi\.system\.api\.domain;', 'package com.risksmart.system.domain;', content)
    content = re.sub(r'package com\.ruoyi\.system\.domain;', 'package com.risksmart.system.domain;', content)

    # Replace imports
    content = re.sub(r'com\.ruoyi\.common\.core', 'com.risksmart.common.core', content)
    content = re.sub(r'com\.ruoyi\.system\.api\.domain', 'com.risksmart.system.domain', content)
    content = re.sub(r'com\.ruoyi\.system\.domain', 'com.risksmart.system.domain', content)

    # Fix javax -> jakarta for Spring Boot 3
    content = re.sub(r'import javax\.validation', 'import jakarta.validation', content)

    os.makedirs(os.path.dirname(target_path), exist_ok=True)
    with open(target_path, 'w', encoding='utf-8') as f:
        f.write(content)

    print(f"[OK] {target_name}")
    return True

def main():
    print("Migrating domain classes...")
    count = 0
    for src, target_name in files_to_migrate:
        if migrate_file(src, target_name):
            count += 1
    print(f"\nMigrated {count} files")

if __name__ == "__main__":
    main()
