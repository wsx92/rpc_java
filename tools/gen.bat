for /r ./ %%i in (*.proto) do (
    protoc.exe --java_out=../src -I=. %%~nxi
)
pause