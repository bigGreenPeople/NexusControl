@echo off
setlocal

set ADB_PATH=adb

set TEMP_TO_PUSH=/sdcard/config.json

set FILE_TO_PUSH=.\config.json



set TARGET_DIR=/data/adb/lspd/config

%ADB_PATH% push %FILE_TO_PUSH% %TEMP_TO_PUSH%

%ADB_PATH% shell "su -c 'mv %TEMP_TO_PUSH% %TARGET_DIR%/config.json'"

endlocal
