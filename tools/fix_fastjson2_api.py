#!/usr/bin/env python3
"""Fix fastjson2 API changes"""
import os
import re

base_dir = r"D:\WorkSpace\Code\RiskSmart-OpenSource\risk-smart-backend\risk-smart-decision-manage\src\main\java"

# Files that need fixing
files_to_fix = [
    r"\com\value\engine\rdenew\function\CommonRuleFunctionDataNew.java",
    r"\com\value\engine\decisionmanage\common\TxdRuleFunctionNew.java",
    r"\com\value\engine\decisionmanage\common\RuleFunctionNew.java",
]

for rel_path in files_to_fix:
    filepath = base_dir + rel_path
    print(f"Fixing: {rel_path}")

    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # Remove old imports
    content = re.sub(r'import com\.alibaba\.fastjson2\.serializer\.SerializerFeature;\n', '', content)
    content = re.sub(r'import com\.alibaba\.fastjson2\.parser\.Feature;\n', '', content)

    # Add new import after JSONObject import
    if 'import com.alibaba.fastjson2.JSONWriter;' not in content:
        content = content.replace(
            'import com.alibaba.fastjson2.JSONObject;',
            'import com.alibaba.fastjson2.JSONObject;\nimport com.alibaba.fastjson2.JSONWriter;'
        )

    # Replace usage
    content = content.replace('SerializerFeature.WriteMapNullValue', 'JSONWriter.Feature.WriteMapNullValue')

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)

print("Done!")
