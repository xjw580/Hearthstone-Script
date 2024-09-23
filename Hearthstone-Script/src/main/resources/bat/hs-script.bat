@echo off
%1 mshta vbscript:CreateObject("Shell.Application").ShellExecute("cmd.exe","/c %~s0 ::","","runas",1)(window.close)&&exit
cd /d %~dp0

set curdir=%cd%

for /f "delims=\" %%a in ('dir /b /a-d /o-d "%curdir%\*.jar"') do (
    java -Dfile.encoding=gbk -Djna.library.path="%curdir%" -jar %%a %1
)