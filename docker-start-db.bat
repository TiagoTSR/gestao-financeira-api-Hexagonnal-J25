@echo off
echo ========================================================
echo  Iniciando PostgreSQL e RabbitMQ no Docker
echo ========================================================
docker compose up -d meu-postgres meu-rabbitmq
if %ERRORLEVEL% EQU 0 (
    echo.
    echo [SUCESSO] PostgreSQL ativo na porta 5435 e RabbitMQ ativo na porta 5672 (Painel: http://localhost:15672)!
    echo Agora voce pode dar Play na IDE ou executar mvnw spring-boot:run
) else (
    echo.
    echo [ERRO] Falha ao subir o container. Verifique se o Docker Desktop esta aberto.
)
pause
