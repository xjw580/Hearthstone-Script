@echo off

set curdir=%cd%

for /f "delims=\" %%a in ('dir /b /a-d /o-d "%curdir%\*.jar"') do (
    java -Dfile.encoding=gbk -jar %%a %1
)