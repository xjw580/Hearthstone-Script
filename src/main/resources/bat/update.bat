@echo off

timeout /t 3

cmd /c start stop.bat

timeout /t 3

del /Q *.jar

del /s /Q lib\*.jar

cmd /c start xcopy %1\* .\ /s /i /y & timeout /t 5 & hs-script.exe %2

exit