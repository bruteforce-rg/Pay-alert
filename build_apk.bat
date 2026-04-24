@echo off
echo ========================================
echo    Pay Alert APK Builder
echo ========================================
echo.

cd /d "%~dp0"

echo Checking Java...
java -version
if %errorlevel% neq 0 (
    echo ERROR: Java not found! Please install Java 11+
    pause
    exit /b 1
)
echo.

echo Checking Gradle...
if not exist gradlew.bat (
    echo ERROR: gradlew.bat not found!
    pause
    exit /b 1
)
echo.

echo Starting build...
echo This may take 5-10 minutes on first run...
echo.

call gradlew.bat assembleDebug

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo         BUILD SUCCESSFUL!
    echo ========================================
    echo.
    echo APK Location: %~dp0app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo Transfer this APK to your phone and install it!
    echo.
) else (
    echo.
    echo ========================================
    echo         BUILD FAILED!
    echo ========================================
    echo.
    echo Check the error messages above.
    echo.
)

echo Press any key to exit...
pause >nul