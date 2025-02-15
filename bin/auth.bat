@echo off
setlocal

set ADB_PATH=adb

set CONFIG_FILE=.\config.json
set CONFIG_TEMP=/sdcard/config.json
set CONFIG_TARGET=/data/adb/lspd/config/config.json

set KEY_FILE=.\key.txt
set KEY_TEMP=/sdcard/key.txt
set KEY_TARGET=/data/adb/lspd/key

%ADB_PATH% push %CONFIG_FILE% %CONFIG_TEMP%
%ADB_PATH% shell "su -c 'mv %CONFIG_TEMP% %CONFIG_TARGET%'"

%ADB_PATH% push %KEY_FILE% %KEY_TEMP%
%ADB_PATH% shell "su -c 'mv %KEY_TEMP% %KEY_TARGET%'"

echo Rebooting device...
adb reboot

endlocal
