@echo off
setlocal

REM 设置ADB路径，确保ADB在系统环境变量中，或者根据实际路径修改
set ADB_PATH=adb

set TEMP_TO_PUSH=/sdcard/key.txt

REM 要推送的文件路径
set FILE_TO_PUSH=.\key.txt



REM 目标设备目录
set TARGET_DIR=/data/adb/lspd/

REM 推送文件到设备
%ADB_PATH% push %FILE_TO_PUSH% %TEMP_TO_PUSH%

REM 切换到设备shell，并获取root权限
%ADB_PATH% shell "su -c 'mv %TEMP_TO_PUSH% %TARGET_DIR%key'"

endlocal
