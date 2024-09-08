@echo off

chcp 65001

taskkill -f -t -im javaw.exe

echo ------------停止成功!--------------------

timeout /t 1

exit
