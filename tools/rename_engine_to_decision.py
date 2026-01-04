#!/usr/bin/env python3
"""Rename com.value.engine to com.value.decision"""
import os
import shutil

base_dir = r"D:\WorkSpace\Code\RiskSmart-OpenSource\risk-smart-backend\risk-smart-decision-manage\src\main\java"

# 1. Replace all references in Java files
modified_count = 0
total_replacements = 0

for root, dirs, files in os.walk(base_dir):
    for file in files:
        if file.endswith('.java'):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()

            if 'com.value.engine' in content:
                count = content.count('com.value.engine')
                new_content = content.replace(
                    'com.value.engine',
                    'com.value.decision'
                )
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(new_content)
                modified_count += 1
                total_replacements += count

print(f"Modified {modified_count} files, {total_replacements} replacements")

# 2. Rename directory structure
engine_dir = os.path.join(base_dir, "com", "value", "engine")
decision_dir = os.path.join(base_dir, "com", "value", "decision")

if os.path.exists(engine_dir):
    if os.path.exists(decision_dir):
        print(f"\nWarning: {decision_dir} already exists, removing it first")
        shutil.rmtree(decision_dir)

    os.rename(engine_dir, decision_dir)
    print(f"\nRenamed directory: com/value/engine -> com/value/decision")
else:
    print(f"\nError: {engine_dir} does not exist")

print("\nDone!")
