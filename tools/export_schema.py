#!/usr/bin/env python3
"""
导出数据库 Schema 脚本
"""
import subprocess
import os

# 数据库配置
DB_HOST = "192.168.1.33"
DB_PORT = "3306"
DB_USER = "root"
DB_PASS = "Vm123456"

# 要导出的数据库
DATABASES = [
    "risk_smart_manage",
    "risk_smart_data_middle_station_dev"
]

# 输出目录
OUTPUT_DIR = r"D:\WorkSpace\Code\RiskSmart-OpenSource\deploy\mysql\init"

# MySQL Shell 路径
MYSQL_SHELL = r"C:\Program Files\MySQL\MySQL Shell 8.0\bin\mysqlsh.exe"

def export_schema():
    """使用 MySQL Shell 导出 schema"""

    for db in DATABASES:
        print(f"正在导出 {db}...")

        # 使用 MySQL Shell 的 util.dumpSchemas
        js_code = f'''
var session = mysql.getClassicSession("{DB_USER}:{DB_PASS}@{DB_HOST}:{DB_PORT}");
shell.setSession(session);
util.dumpSchemas(["{db}"], "{OUTPUT_DIR.replace(chr(92), '/')}/{db}", {{
    "threads": 1,
    "ddlOnly": true,
    "consistent": false
}});
'''

        cmd = [MYSQL_SHELL, "--js", "-e", js_code]

        try:
            result = subprocess.run(cmd, capture_output=True, text=True, timeout=120)
            if result.returncode == 0:
                print(f"  ✓ {db} 导出成功")
            else:
                print(f"  ✗ {db} 导出失败: {result.stderr}")
        except Exception as e:
            print(f"  ✗ {db} 导出异常: {e}")

if __name__ == "__main__":
    os.makedirs(OUTPUT_DIR, exist_ok=True)
    export_schema()
    print("\n完成！")
