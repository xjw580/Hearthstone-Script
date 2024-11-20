@echo off
setlocal enabledelayedexpansion
chcp 65001

:: 检查是否在git仓库中
git rev-parse --git-dir >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 当前目录不是git仓库
    exit /b 1
)

:: 获取最新的tag
for /f "tokens=*" %%i in ('git describe --tags --abbrev^=0 2^>nul') do set latest_tag=%%i

:: 检查是否存在tag
if not defined latest_tag (
    echo 错误: 仓库中没有找到tag
    exit /b 1
)
echo 推送!latest_tag!

:: 推送master分支和最新tag到GitHub
git push Github master !latest_tag!
echo 执行完毕
exit /b 0