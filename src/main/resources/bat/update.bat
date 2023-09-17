cmd /c start stop.bat
ping -n 4 127.0.0.1>nul
del /Q *.jar
del /s /Q lib\*.jar
xcopy %1\* %1\..\ /s /i /y
cmd /c start start.bat
timeout /t 1
exit