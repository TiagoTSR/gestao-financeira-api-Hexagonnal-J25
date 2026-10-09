@echo off
echo ========================================================
echo  Iniciando Banco + API Spring no Docker (Build e Subida)
echo ========================================================
docker compose up -d --build
if %ERRORLEVEL% EQU 0 (
    echo.
    echo [SUCESSO] Contêineres iniciados com sucesso!
    echo - PostgreSQL: porta 5435
    echo - API Spring Boot: http://localhost:8081
    echo - Swagger / OpenAPI: http://localhost:8081/swagger-ui.html
) else (
    echo.
    echo [ERRO] Falha ao subir os conteineres. Verifique se o Docker Desktop esta aberto.
)
pause
