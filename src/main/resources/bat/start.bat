@echo off

set JDK_VERSION=21

set curdir=%cd%

rem 检查是否存在 JAVA_HOME 环境变量
if not defined JAVA_HOME (
    echo unknow
    goto :end
)

rem 获取 Java 版本信息
for /f "tokens=3" %%i in ('%JAVA_HOME%\bin\java -version 2^>^&1 ^| findstr /i "version"') do set JAVA_VERSION=%%i

rem 输出 Java 版本信息
echo JDK Version:%JAVA_VERSION%

rem 检查是否包含 "21"
echo %JAVA_VERSION% | find %JDK_VERSION% > nul
if %errorlevel% equ 0 (
    for /f "delims=\" %%a in ('dir /b /a-d /o-d "%curdir%\*.jar"') do (
            start javaw -jar %%a %1
    )
) else (
    msg * "No JDK%JDK_VERSION% installed. Downloading it for you."
    start https://download.oracle.com/java/21/latest/jdk-21_windows-x64_bin.exe
)

:end