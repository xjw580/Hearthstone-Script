set curdir=%cd%
for /f "delims=\" %%a in ('dir /b /a-d /o-d "%curdir%\*.jar"') do (
    start javaw -jar %%a && (echo SUCCESS: Being started! & timeout /t 1 & exit) || (echo ERROR: Requires JDK environment, is downloading, after downloading can be installed! & explorer.exe "https://download.oracle.com/java/17/latest/jdk-17_windows-x64_bin.exe")
)