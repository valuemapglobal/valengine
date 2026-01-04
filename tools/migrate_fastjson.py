#!/usr/bin/env python3
"""Migrate fastjson 1.x imports to fastjson2"""
import os
import re

base_dir = r"D:\WorkSpace\Code\RiskSmart-OpenSource\risk-smart-backend\risk-smart-decision-manage\src\main\java"

# Pattern to match fastjson 1.x imports
pattern = re.compile(r'import com\.alibaba\.fastjson\.', re.MULTILINE)

modified_files = []

for root, dirs, files in os.walk(base_dir):
    for file in files:
        if file.endswith('.java'):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()

            if 'import com.alibaba.fastjson.' in content:
                new_content = content.replace(
                    'import com.alibaba.fastjson.',
                    'import com.alibaba.fastjson2.'
                )
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(new_content)
                modified_files.append(filepath.replace(base_dir, ''))

print(f"Modified {len(modified_files)} files:")
for f in modified_files:
    print(f"  {f}")
