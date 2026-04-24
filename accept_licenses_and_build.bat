@echo off
echo Accepting Android SDK licenses...
cd /d "C:\Users\Rakesh\Downloads\Compressed\cmdline-tools\bin"

echo y | sdkmanager.bat --licenses --sdk_root="C:\Users\Rakesh\Downloads\Compressed\cmdline-tools"

echo.
echo Licenses accepted! Now building APK...
echo.

cd /d "C:\Users\Rakesh\Desktop\Pay Alert"
call gradlew.bat assembleDebug

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo         BUILD SUCCESSFUL!
    echo ========================================
    echo.
    echo APK Location: C:\Users\Rakesh\Desktop\Pay Alert\app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo Transfer this APK to your phone and install it!
    echo.
) else (
    echo.
    echo ========================================
    echo         BUILD FAILED!
    echo ========================================
    echo.
    echo Try using GitHub Actions instead - upload to GitHub and it will build automatically.
    echo.
)

pause