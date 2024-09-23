@echo off
%1 mshta vbscript:CreateObject("Shell.Application").ShellExecute("cmd.exe","/c %~s0 ::","","runas",1)(window.close)&&exit
cd /d %~dp0

chcp 65001

taskkill -f -t -im javaw.exe

echo ------------停止成功!--------------------

timeout /t 1

exit
