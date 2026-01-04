#!/bin/bash
# ============================================================
# RiskSmart Docker 一键部署脚本
# ============================================================

set -e

echo "=============================================="
echo "  RiskSmart 一键部署"
echo "=============================================="

# 切换到项目根目录
cd "$(dirname "$0")/.."

# 检查 Docker
if ! command -v docker &> /dev/null; then
    echo "[ERROR] Docker 未安装，请先安装 Docker"
    exit 1
fi

# 检查 Docker Compose
if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
    echo "[ERROR] Docker Compose 未安装"
    exit 1
fi

# 检查 .env 文件
if [ ! -f .env ]; then
    echo "[INFO] 复制 .env.example 到 .env"
    cp .env.example .env
fi

# 选择部署模式
echo ""
echo "请选择部署模式:"
echo "  1) 完整部署 (包含所有服务)"
echo "  2) 仅基础设施 (MySQL + Redis + Nacos)"
echo "  3) 仅后端服务"
echo ""
read -p "请输入选项 [1]: " choice
choice=${choice:-1}

case $choice in
    1)
        echo "[INFO] 启动完整部署..."
        docker compose up -d
        ;;
    2)
        echo "[INFO] 启动基础设施..."
        docker compose up -d mysql redis nacos
        ;;
    3)
        echo "[INFO] 启动后端服务..."
        docker compose up -d decision-manage data-middle-station
        ;;
    *)
        echo "[ERROR] 无效选项"
        exit 1
        ;;
esac

echo ""
echo "=============================================="
echo "  部署完成!"
echo "=============================================="
echo ""
echo "服务访问地址:"
echo "  - Web 前端:    http://localhost:${WEB_PORT:-80}"
echo "  - Nacos 控制台: http://localhost:${NACOS_PORT:-8848}/nacos"
echo "  - 决策引擎 API: http://localhost:${DECISION_PORT:-8991}/api/doc.html"
echo "  - 数据中台 API: http://localhost:${DATA_STATION_PORT:-8990}/api/doc.html"
echo ""
echo "默认账号:"
echo "  - 系统管理员: root / root"
echo "  - Nacos: nacos / nacos"
echo ""
echo "查看日志: docker compose logs -f [服务名]"
echo "停止服务: docker compose down"
echo ""
