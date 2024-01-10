@echo off

timeout /t 3

cmd /c start stop.bat

timeout /t 3

del /Q *.jar

del /s /Q lib\*.jar

xcopy %1\* .\ /s /i /y

hs-script.exe %2

timeout /t 3

exit