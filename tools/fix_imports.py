#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
修复包路径导入
"""

import os
import re

RISKSMART_PATH = r"D:\WorkSpace\Code\RiskSmart-OpenSource\risk-smart-backend\risk-smart-common"

REPLACEMENTS = [
    ("com.risksmart.common.core.web.domain.", "com.risksmart.common.core.web."),
]

def fix_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    original = content
    for old, new in REPLACEMENTS:
        content = content.replace(old, new)

    if content != original:
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        return True
    return False

def main():
    print("Fixing imports...")
    count = 0
    for root, dirs, files in os.walk(RISKSMART_PATH):
        for file in files:
            if file.endswith('.java'):
                file_path = os.path.join(root, file)
                if fix_file(file_path):
                    print(f"  [FIXED] {file}")
                    count += 1
    print(f"Total fixed: {count} files")

if __name__ == "__main__":
    main()
