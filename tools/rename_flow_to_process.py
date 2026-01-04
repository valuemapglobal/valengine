#!/usr/bin/env python3
"""Rename flow package to process"""
import os
import shutil

base_dir = r"D:\WorkSpace\Code\RiskSmart-OpenSource\risk-smart-backend\risk-smart-decision-manage\src\main\java"

# 1. First, replace all references in all Java files
modified_count = 0
for root, dirs, files in os.walk(base_dir):
    for file in files:
        if file.endswith('.java'):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()

            if 'com.value.engine.flow' in content:
                new_content = content.replace(
                    'com.value.engine.flow',
                    'com.value.engine.process'
                )
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(new_content)
                modified_count += 1
                print(f"Modified: {filepath.replace(base_dir, '')}")

print(f"\nModified {modified_count} files")

# 2. Rename the directory
flow_dir = os.path.join(base_dir, "com", "value", "engine", "flow")
process_dir = os.path.join(base_dir, "com", "value", "engine", "process")

if os.path.exists(flow_dir):
    if os.path.exists(process_dir):
        print(f"\nWarning: {process_dir} already exists, removing it first")
        shutil.rmtree(process_dir)

    os.rename(flow_dir, process_dir)
    print(f"\nRenamed directory: flow -> process")
else:
    print(f"\nError: {flow_dir} does not exist")

print("\nDone!")
