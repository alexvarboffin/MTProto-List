@echo off
setlocal
cd /d "%~dp0"

where python >nul 2>&1
if errorlevel 1 (
    echo Python not found in PATH.
    echo Install Python 3 and ensure "python" is available.
    pause
    exit /b 1
)

python "%~dp0firebase_viewer.py"
if errorlevel 1 pause

endlocal
