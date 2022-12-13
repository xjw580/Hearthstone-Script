set curdir=%cd%
for /f "delims=\" %%a in ('dir /b /a-d /o-d "%curdir%\*.jar"') do (
    start jdk-17.0.5\bin\javaw -jar %%a
)

echo .
echo ------------ Start successfully --------------
echo .
echo ------------ Start successfully --------------
echo .
echo ------------ Start successfully --------------

timeout /t 5

exit