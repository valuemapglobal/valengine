#!/usr/bin/env python3
"""Rename com.value.engine to com.value.decision in risk-smart-common"""
import os
import shutil

base_dir = r"D:\WorkSpace\Code\RiskSmart-OpenSource\risk-smart-backend\risk-smart-common\src\main\java"

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
        print(f"\nWarning: {decision_dir} already exists, merging...")
        # Move contents instead of replacing
        for item in os.listdir(engine_dir):
            src = os.path.join(engine_dir, item)
            dst = os.path.join(decision_dir, item)
            if os.path.exists(dst):
                shutil.rmtree(dst)
            shutil.move(src, dst)
        os.rmdir(engine_dir)
    else:
        os.rename(engine_dir, decision_dir)
    print(f"\nRenamed/merged directory: com/value/engine -> com/value/decision")
else:
    print(f"\nInfo: {engine_dir} does not exist (may already be renamed)")

print("\nDone!")
