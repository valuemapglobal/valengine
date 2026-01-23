@echo off
chcp 65001 >nul
REM ============================================================
REM RiskSmart Docker 一键部署脚本 (Windows)
REM ============================================================

echo ==============================================
echo   RiskSmart 一键部署
echo ==============================================

REM 切换到项目根目录
cd /d "%~dp0.."

REM 检查 Docker
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Docker 未安装，请先安装 Docker Desktop
    pause
    exit /b 1
)

REM 检查 .env 文件
if not exist .env (
    echo [INFO] 复制 .env.example 到 .env
    copy .env.example .env
)

echo.
echo 请选择部署模式:
echo   1^) 完整部署 (包含所有服务)
echo   2^) 仅基础设施 (MySQL + Redis + Nacos)
echo   3^) 仅后端服务
echo.
set /p choice="请输入选项 [1]: "
if "%choice%"=="" set choice=1

if "%choice%"=="1" (
    echo [INFO] 启动完整部署...
    docker compose up -d
) else if "%choice%"=="2" (
    echo [INFO] 启动基础设施...
    docker compose up -d mysql redis nacos
) else if "%choice%"=="3" (
    echo [INFO] 启动后端服务...
    docker compose up -d decision-manage data-middle-station
) else (
    echo [ERROR] 无效选项
    pause
    exit /b 1
)

echo.
echo ==============================================
echo   部署完成!
echo ==============================================
echo.
echo 服务访问地址:
echo   - Web 前端:    http://localhost:80
echo   - Nacos 控制台: http://localhost:8848/nacos
echo   - 决策引擎 API: http://localhost:8991/api/doc.html
echo   - 数据中台 API: http://localhost:8990/api/doc.html
echo.
echo 默认账号:
echo   - 系统管理员: root / root
echo   - Nacos: nacos / nacos
echo.
echo 查看日志: docker compose logs -f [服务名]
echo 停止服务: docker compose down
echo.
pause
