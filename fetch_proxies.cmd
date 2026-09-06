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

python -c "import selenium" >nul 2>&1
if errorlevel 1 (
    echo Installing selenium...
    python -m pip install selenium
    if errorlevel 1 (
        echo Failed to install selenium.
        pause
        exit /b 1
    )
)

REM Tor Browser SOCKS by default. For direct Chrome:
REM   set MTPROTO_SOCKS=
REM Or pass extra flags after, e.g.  fetch_proxies.cmd --dry-run
if not defined MTPROTO_SOCKS set "MTPROTO_SOCKS=socks5://127.0.0.1:9150"

echo === MTProto: scrape + geo + Firebase push ===
echo Working dir: %CD%
if defined MTPROTO_SOCKS (
    echo SOCKS: %MTPROTO_SOCKS%
) else (
    echo SOCKS: none ^(direct^)
)
echo Extra args: %*
echo.

if defined MTPROTO_SOCKS (
    python "%~dp0fetch_proxies.py" --socks %MTPROTO_SOCKS% %*
) else (
    python "%~dp0fetch_proxies.py" %*
)
set ERR=%ERRORLEVEL%
echo.
if %ERR% neq 0 (
    echo FAILED exit=%ERR%
) else (
    echo DONE
)
pause
exit /b %ERR%
