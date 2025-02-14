@echo off
set MODULE_ZIP=NexusControl.zip

adb devices | find "device" > nul
if %errorlevel% neq 0 (
    echo Error: No device connected!
    pause
    exit /b 1
)


echo Flashing NexusControl.zip module...
adb push %MODULE_ZIP% /data/local/tmp/
adb shell "su -c \"magisk --install-module /data/local/tmp/%MODULE_ZIP%\""

echo Rebooting device...
adb reboot

echo Success: Zygisk enabled, module installed, and device rebooted.
pause
exit /b 0
