# Script PowerShell de Automação do Teste de Sobrecarga (1.000 VUs com k6)
param(
    [string]$ApiUrl = "http://localhost:8080"
)

$ErrorActionPreference = "Stop"

Write-Host "`n============================================================" -ForegroundColor Cyan
Write-Host "   PREPARANDO TESTE DE SOBRECARGA COM GRAFANA k6           " -ForegroundColor Cyan
Write-Host "============================================================`n" -ForegroundColor Cyan

# Verifica se k6 está no PATH
$k6Cmd = Get-Command "k6" -ErrorAction SilentlyContinue

if (-not $k6Cmd) {
    # Procura na pasta local ./tools/k6.exe
    $toolsDir = Join-Path $PSScriptRoot "tools"
    $localK6 = Join-Path $toolsDir "k6.exe"

    if (Test-Path $localK6) {
        $k6Executable = $localK6
        Write-Host "✓ Executável portátil k6 encontrado em: $localK6" -ForegroundColor Green
    } else {
        Write-Host "O utilitário k6 não foi encontrado no sistema." -ForegroundColor Yellow
        Write-Host "Baixando versão portátil do k6 (oficial Grafana, ~18MB)..." -ForegroundColor Yellow

        if (-not (Test-Path $toolsDir)) {
            New-Item -ItemType Directory -Path $toolsDir | Out-Null
        }

        $zipPath = Join-Path $toolsDir "k6.zip"
        $downloadUrl = "https://github.com/grafana/k6/releases/download/v0.56.0/k6-v0.56.0-windows-amd64.zip"

        try {
            Invoke-WebRequest -Uri $downloadUrl -OutFile $zipPath -UseBasicParsing
            Expand-Archive -Path $zipPath -DestinationPath $toolsDir -Force
            Remove-Item $zipPath -Force

            # Procura o binário descompactado
            $extractedK6 = Get-ChildItem -Path $toolsDir -Filter "k6.exe" -Recurse | Select-Object -First 1
            if ($extractedK6) {
                Move-Item -Path $extractedK6.FullName -Destination $localK6 -Force
            }
            $k6Executable = $localK6
            Write-Host "✓ k6 portátil instalado com sucesso em $localK6!" -ForegroundColor Green
        } catch {
            Write-Host "Aviso: Não foi possível baixar automaticamente via PowerShell." -ForegroundColor Red
            Write-Host "Por favor, instale o k6 manualmente via:" -ForegroundColor Yellow
            Write-Host "   winget install k6" -ForegroundColor White
            Write-Host "Ou baixe em: https://k6.io/docs/get-started/installation/" -ForegroundColor White
            exit 1
        }
    }
} else {
    $k6Executable = "k6"
    Write-Host "✓ k6 encontrado no PATH do sistema." -ForegroundColor Green
}

Write-Host "`nIniciando execução contra API: $ApiUrl" -ForegroundColor Cyan
Write-Host "Duração estimada: ~4 minutos (Aquecimento -> 1.000 Usuários -> Desaceleração)`n" -ForegroundColor Yellow

$env:API_URL = $ApiUrl
& $k6Executable run (Join-Path $PSScriptRoot "stress-test-1000-users.js")

if (Test-Path (Join-Path $PSScriptRoot "stress-test-report.html")) {
    Write-Host "`n✓ Relatório HTML gerado em: stress-test-report.html" -ForegroundColor Green
}
