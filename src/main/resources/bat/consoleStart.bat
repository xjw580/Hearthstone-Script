@echo off

chcp 65001

set curdir=%cd%

for /f "delims=\" %%a in ('dir /b /a-d /o-d "%curdir%\*.jar"') do (
    java -jar %%a %1
)