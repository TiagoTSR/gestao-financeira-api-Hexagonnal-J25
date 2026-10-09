@echo off
echo ========================================================
echo  Iniciando apenas o Banco de Dados PostgreSQL no Docker
echo ========================================================
docker compose up -d meu-postgres
if %ERRORLEVEL% EQU 0 (
    echo.
    echo [SUCESSO] PostgreSQL ativo na porta 5435!
    echo Agora voce pode dar Play na IDE ou executar mvnw spring-boot:run
) else (
    echo.
    echo [ERRO] Falha ao subir o container. Verifique se o Docker Desktop esta aberto.
)
pause
