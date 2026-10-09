@echo off
echo ========================================================
echo  Parando conteineres do Docker
echo ========================================================
docker compose down
if %ERRORLEVEL% EQU 0 (
    echo.
    echo [SUCESSO] Conteineres parados com sucesso!
) else (
    echo.
    echo [ERRO] Falha ao parar os conteineres.
)
pause
