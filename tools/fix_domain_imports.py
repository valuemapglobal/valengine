#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Fix duplicate domain class imports in decision-manage module"""

import os
import re

BASE_DIR = "D:/WorkSpace/Code/RiskSmart-OpenSource/risk-smart-backend/risk-smart-decision-manage/src/main/java"

# Import replacements
REPLACEMENTS = [
    # com.value.decision.common.domain -> com.risksmart.system.domain
    (r'import com\.value\.decision\.common\.domain\.SysDictData;',
     'import com.risksmart.system.domain.SysDictData;'),
    (r'import com\.value\.decision\.common\.domain\.SysUser;',
     'import com.risksmart.system.domain.SysUser;'),
    (r'import com\.value\.decision\.common\.domain\.SysDept;',
     'import com.risksmart.system.domain.SysDept;'),
    # com.value.decision.framework.domain -> com.risksmart.system.domain
    (r'import com\.value\.decision\.framework\.domain\.SysLogininfor;',
     'import com.risksmart.system.domain.SysLogininfor;'),
    (r'import com\.value\.decision\.framework\.domain\.SysOperLog;',
     'import com.risksmart.system.domain.SysOperLog;'),
]

def fix_file(filepath):
    """Fix imports in a single file"""
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    original = content
    for pattern, replacement in REPLACEMENTS:
        content = re.sub(pattern, replacement, content)

    if content != original:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        return True
    return False

def main():
    count = 0
    for root, dirs, files in os.walk(BASE_DIR):
        for file in files:
            if file.endswith('.java'):
                filepath = os.path.join(root, file)
                if fix_file(filepath):
                    print(f"[FIXED] {os.path.relpath(filepath, BASE_DIR)}")
                    count += 1

    print(f"\nFixed {count} files")

    # Delete duplicate domain classes
    duplicates = [
        "D:/WorkSpace/Code/RiskSmart-OpenSource/risk-smart-backend/risk-smart-decision-manage/src/main/java/com/value/decision/common/domain/SysDictData.java",
        "D:/WorkSpace/Code/RiskSmart-OpenSource/risk-smart-backend/risk-smart-decision-manage/src/main/java/com/value/decision/common/domain/SysUser.java",
        "D:/WorkSpace/Code/RiskSmart-OpenSource/risk-smart-backend/risk-smart-decision-manage/src/main/java/com/value/decision/common/domain/SysDept.java",
        "D:/WorkSpace/Code/RiskSmart-OpenSource/risk-smart-backend/risk-smart-decision-manage/src/main/java/com/value/decision/framework/domain/SysLogininfor.java",
        "D:/WorkSpace/Code/RiskSmart-OpenSource/risk-smart-backend/risk-smart-decision-manage/src/main/java/com/value/decision/framework/domain/SysOperLog.java",
    ]

    print("\nDeleting duplicate domain classes:")
    for path in duplicates:
        if os.path.exists(path):
            os.remove(path)
            print(f"[DELETED] {os.path.basename(path)}")

if __name__ == "__main__":
    main()
