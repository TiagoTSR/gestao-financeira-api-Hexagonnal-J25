package com.decodex.br.adapters.in.web;

import com.decodex.br.adapters.in.web.documentation.RelatorioControllerDoc;
import com.decodex.br.application.dto.relatorio.RelatorioDownloadDTO;
import com.decodex.br.application.dto.relatorio.RelatorioProtocoloResponseDTO;
import com.decodex.br.application.dto.relatorio.RelatorioSolicitacaoRequestDTO;
import com.decodex.br.application.dto.relatorio.RelatorioStatusResponseDTO;
import com.decodex.br.domain.port.in.GerarRelatorioEstatisticaInputPort;
import com.decodex.br.domain.port.in.RelatorioAsyncInputPort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping({"/api/relatorios", "/relatorios", "/lancamentos/relatorios"})
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class RelatorioController implements RelatorioControllerDoc {

    private static final String EXCEL_MEDIA_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final GerarRelatorioEstatisticaInputPort gerarRelatorioInputPort;
    private final RelatorioAsyncInputPort relatorioAsyncInputPort;

    public RelatorioController(GerarRelatorioEstatisticaInputPort gerarRelatorioInputPort,
                               RelatorioAsyncInputPort relatorioAsyncInputPort) {
        this.gerarRelatorioInputPort = gerarRelatorioInputPort;
        this.relatorioAsyncInputPort = relatorioAsyncInputPort;
    }

    @Override
    @GetMapping({"/lancamentos-por-pessoa", "/por-pessoa"})
    public ResponseEntity<byte[]> relatorioPorPessoa(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        byte[] relatorio = gerarRelatorioInputPort.executarPorPessoa(inicio, fim);
        String filename = String.format("relatorio-lancamentos_%s_%s.pdf", inicio, fim);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(relatorio);
    }

    @Override
    @GetMapping({"/lancamentos-por-pessoa/excel", "/por-pessoa/excel", "/lancamentos-excel"})
    public ResponseEntity<byte[]> relatorioPorPessoaExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        byte[] relatorio = gerarRelatorioInputPort.executarPorPessoaExcel(inicio, fim);
        String filename = String.format("relatorio-lancamentos_%s_%s.xlsx", inicio, fim);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, EXCEL_MEDIA_TYPE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(relatorio);
    }

    @Override
    @PostMapping("/solicitar")
    public ResponseEntity<RelatorioProtocoloResponseDTO> solicitarRelatorio(@RequestBody RelatorioSolicitacaoRequestDTO request) {
        RelatorioProtocoloResponseDTO response = relatorioAsyncInputPort.solicitar(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @Override
    @GetMapping("/{protocolo}/status")
    public ResponseEntity<RelatorioStatusResponseDTO> consultarStatus(@PathVariable UUID protocolo) {
        RelatorioStatusResponseDTO response = relatorioAsyncInputPort.consultarStatus(protocolo);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/{protocolo}/download")
    public ResponseEntity<byte[]> baixarRelatorio(@PathVariable UUID protocolo) {
        RelatorioDownloadDTO download = relatorioAsyncInputPort.baixar(protocolo);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, download.contentType())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + download.nomeArquivo() + "\"")
                .body(download.conteudo());
    }

    @Override
    @GetMapping(value = "/painel", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> painelVisual() {
        String html = """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Painel RabbitMQ - Geração Assíncrona de Relatórios</title>
                    <link rel="preconnect" href="https://fonts.googleapis.com">
                    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
                    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
                    <style>
                        :root {
                            --bg: #0b0f19;
                            --card-bg: rgba(22, 30, 49, 0.75);
                            --border: rgba(255, 255, 255, 0.1);
                            --primary: #38bdf8;
                            --primary-hover: #0284c7;
                            --success: #34d399;
                            --warning: #fbbf24;
                            --danger: #f87171;
                            --text: #f8fafc;
                            --text-muted: #94a3b8;
                        }
                        * { box-sizing: border-box; margin: 0; padding: 0; }
                        body {
                            font-family: 'Inter', sans-serif;
                            background: radial-gradient(circle at 15% 15%, #1e1b4b 0%, #0b0f19 70%);
                            color: var(--text);
                            min-height: 100vh;
                            padding: 2rem 1rem;
                            display: flex;
                            flex-direction: column;
                            align-items: center;
                        }
                        .container { width: 100%; max-width: 900px; }
                        header { text-align: center; margin-bottom: 2.5rem; }
                        header h1 {
                            font-size: 2.2rem;
                            font-weight: 800;
                            background: linear-gradient(135deg, #38bdf8, #818cf8, #c084fc);
                            -webkit-background-clip: text;
                            -webkit-text-fill-color: transparent;
                            margin-bottom: 0.5rem;
                        }
                        header p { color: var(--text-muted); font-size: 1rem; }
                        .badge-container {
                            display: flex;
                            gap: 0.75rem;
                            justify-content: center;
                            margin-top: 1rem;
                            flex-wrap: wrap;
                        }
                        .badge {
                            display: inline-flex;
                            align-items: center;
                            gap: 0.4rem;
                            padding: 0.35rem 0.85rem;
                            border-radius: 9999px;
                            font-size: 0.8rem;
                            font-weight: 600;
                            border: 1px solid var(--border);
                            background: rgba(255, 255, 255, 0.05);
                        }
                        .badge-dot { width: 8px; height: 8px; border-radius: 50%; }
                        .badge-rabbitmq .badge-dot { background: #ff6600; }
                        .badge-dlq .badge-dot { background: var(--danger); }
                        .card {
                            background: var(--card-bg);
                            backdrop-filter: blur(16px);
                            -webkit-backdrop-filter: blur(16px);
                            border: 1px solid var(--border);
                            border-radius: 1.25rem;
                            padding: 2rem;
                            margin-bottom: 1.5rem;
                            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.4);
                        }
                        .card-title {
                            font-size: 1.25rem;
                            font-weight: 700;
                            margin-bottom: 1.25rem;
                            display: flex;
                            align-items: center;
                            gap: 0.6rem;
                        }
                        .form-grid {
                            display: grid;
                            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                            gap: 1.25rem;
                            margin-bottom: 1.5rem;
                        }
                        label { display: block; font-size: 0.85rem; font-weight: 500; color: var(--text-muted); margin-bottom: 0.4rem; }
                        input, select {
                            width: 100%;
                            padding: 0.75rem 1rem;
                            background: rgba(15, 23, 42, 0.8);
                            border: 1px solid var(--border);
                            border-radius: 0.75rem;
                            color: var(--text);
                            font-size: 0.95rem;
                            outline: none;
                            transition: border-color 0.2s;
                        }
                        input:focus, select:focus { border-color: var(--primary); }
                        button {
                            cursor: pointer;
                            border: none;
                            padding: 0.85rem 1.75rem;
                            border-radius: 0.75rem;
                            font-weight: 600;
                            font-size: 0.95rem;
                            transition: all 0.2s ease;
                            display: inline-flex;
                            align-items: center;
                            justify-content: center;
                            gap: 0.5rem;
                        }
                        .btn-primary {
                            background: linear-gradient(135deg, #0284c7, #2563eb);
                            color: #fff;
                            box-shadow: 0 4px 14px rgba(37, 99, 235, 0.4);
                        }
                        .btn-primary:hover {
                            background: linear-gradient(135deg, #0369a1, #1d4ed8);
                            transform: translateY(-1px);
                        }
                        .btn-download {
                            background: linear-gradient(135deg, #059669, #10b981);
                            color: #fff;
                            box-shadow: 0 4px 14px rgba(16, 185, 129, 0.4);
                            width: 100%;
                            margin-top: 1rem;
                        }
                        .btn-download:hover { transform: translateY(-1px); }
                        .status-box {
                            margin-top: 1.5rem;
                            padding: 1.25rem;
                            border-radius: 0.75rem;
                            border: 1px solid var(--border);
                            background: rgba(15, 23, 42, 0.6);
                            display: none;
                        }
                        .status-header {
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                            margin-bottom: 0.75rem;
                        }
                        .status-pill {
                            padding: 0.3rem 0.8rem;
                            border-radius: 9999px;
                            font-size: 0.75rem;
                            font-weight: 700;
                            text-transform: uppercase;
                        }
                        .status-PENDENTE { background: rgba(251, 191, 36, 0.15); color: var(--warning); border: 1px solid var(--warning); }
                        .status-PROCESSANDO { background: rgba(56, 189, 248, 0.15); color: var(--primary); border: 1px solid var(--primary); }
                        .status-CONCLUIDO { background: rgba(52, 211, 153, 0.15); color: var(--success); border: 1px solid var(--success); }
                        .status-ERRO { background: rgba(248, 113, 113, 0.15); color: var(--danger); border: 1px solid var(--danger); }
                        .protocol-text {
                            font-family: monospace;
                            font-size: 0.9rem;
                            word-break: break-all;
                            color: #cbd5e1;
                            background: rgba(0, 0, 0, 0.3);
                            padding: 0.5rem;
                            border-radius: 0.5rem;
                            margin-bottom: 0.75rem;
                        }
                        .spinner {
                            display: inline-block;
                            width: 16px;
                            height: 16px;
                            border: 2px solid rgba(255,255,255,0.3);
                            border-radius: 50%;
                            border-top-color: #fff;
                            animation: spin 1s ease-in-out infinite;
                        }
                        @keyframes spin { to { transform: rotate(360deg); } }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <header>
                            <h1>Geração Assíncrona de Relatórios</h1>
                            <p>Processamento distribuído via RabbitMQ com Dead Letter Queue (DLQ) e rastreamento por protocolo</p>
                            <div class="badge-container">
                                <span class="badge badge-rabbitmq"><span class="badge-dot"></span> RabbitMQ Active</span>
                                <span class="badge"><span class="badge-dot" style="background:#38bdf8"></span> Exchange: relatorios.exchange</span>
                                <span class="badge"><span class="badge-dot" style="background:#818cf8"></span> Queue: relatorios.solicitacao.queue</span>
                                <span class="badge badge-dlq"><span class="badge-dot"></span> DLQ: relatorios.dlq</span>
                            </div>
                        </header>
                
                        <div class="card">
                            <h2 class="card-title">🚀 Nova Solicitação</h2>
                            <form id="formSolicitar" onsubmit="event.preventDefault(); solicitar();">
                                <div class="form-grid">
                                    <div>
                                        <label for="tipo">Formato do Relatório</label>
                                        <select id="tipo">
                                            <option value="PDF">Relatório Completo (PDF)</option>
                                            <option value="EXCEL">Planilha Financeira (Excel .xlsx)</option>
                                        </select>
                                    </div>
                                    <div>
                                        <label for="inicio">Data Inicial</label>
                                        <input type="date" id="inicio" required>
                                    </div>
                                    <div>
                                        <label for="fim">Data Final</label>
                                        <input type="date" id="fim" required>
                                    </div>
                                </div>
                                <button type="submit" class="btn-primary" id="btnSolicitar">
                                    <span>Solicitar Relatório Assíncrono</span>
                                </button>
                            </form>
                
                            <div class="status-box" id="statusBox">
                                <div class="status-header">
                                    <span style="font-size: 0.9rem; font-weight: 600;">Status do Processamento:</span>
                                    <span id="statusPill" class="status-pill status-PENDENTE">PENDENTE</span>
                                </div>
                                <div class="protocol-text" id="protocoloDisplay">Protocolo: -</div>
                                <p id="statusMsg" style="font-size: 0.85rem; color: var(--text-muted); margin-bottom: 0.5rem;">Aguardando na fila...</p>
                                <button id="btnDownload" class="btn-download" style="display: none;" onclick="baixarArquivo()">
                                    📥 Baixar Arquivo Gerado
                                </button>
                            </div>
                        </div>
                
                        <div class="card">
                            <h2 class="card-title">🔍 Consultar Protocolo Existente</h2>
                            <div style="display: flex; gap: 0.75rem; flex-wrap: wrap;">
                                <input type="text" id="inputProtocolo" placeholder="Informe o UUID do protocolo (ex: e3b89098-...) " style="flex: 1; min-width: 250px;">
                                <button type="button" class="btn-primary" onclick="consultarManual()">Consultar</button>
                            </div>
                        </div>
                    </div>
                
                    <script>
                        const today = new Date();
                        const firstDay = new Date(today.getFullYear(), today.getMonth(), 1);
                        document.getElementById('inicio').value = firstDay.toISOString().split('T')[0];
                        document.getElementById('fim').value = today.toISOString().split('T')[0];
                
                        let currentProtocolo = null;
                        let pollInterval = null;
                
                        async function solicitar() {
                            const btn = document.getElementById('btnSolicitar');
                            btn.disabled = true;
                            btn.innerHTML = '<span class="spinner"></span> Solicitando...';
                
                            const body = {
                                tipo: document.getElementById('tipo').value,
                                inicio: document.getElementById('inicio').value,
                                fim: document.getElementById('fim').value
                            };
                
                            try {
                                const res = await fetch('/api/relatorios/solicitar', {
                                    method: 'POST',
                                    headers: { 'Content-Type': 'application/json' },
                                    body: JSON.stringify(body)
                                });
                                if (!res.ok) throw new Error('Falha na solicitação HTTP ' + res.status);
                                const data = await res.json();
                                currentProtocolo = data.protocolo;
                                exibirStatus(data.protocolo, data.status, data.mensagem);
                                iniciarPolling(data.protocolo);
                            } catch (err) {
                                alert('Erro ao solicitar relatório: ' + err.message);
                            } finally {
                                btn.disabled = false;
                                btn.innerHTML = 'Solicitar Relatório Assíncrono';
                            }
                        }
                
                        function exibirStatus(protocolo, status, msg) {
                            const box = document.getElementById('statusBox');
                            box.style.display = 'block';
                            document.getElementById('protocoloDisplay').innerText = 'Protocolo: ' + protocolo;
                            const pill = document.getElementById('statusPill');
                            pill.className = 'status-pill status-' + status;
                            pill.innerText = status;
                            document.getElementById('statusMsg').innerText = msg || 'Processando na fila...';
                            const btnDown = document.getElementById('btnDownload');
                            btnDown.style.display = (status === 'CONCLUIDO') ? 'block' : 'none';
                        }
                
                        function iniciarPolling(protocolo) {
                            if (pollInterval) clearInterval(pollInterval);
                            pollInterval = setInterval(async () => {
                                try {
                                    const res = await fetch(`/api/relatorios/${protocolo}/status`);
                                    if (!res.ok) return;
                                    const data = await res.json();
                                    exibirStatus(protocolo, data.status, data.mensagemErro ? 'Erro: ' + data.mensagemErro : (data.status === 'CONCLUIDO' ? 'Relatório gerado com sucesso!' : 'Processando na fila RabbitMQ...'));
                                    if (data.status === 'CONCLUIDO' || data.status === 'ERRO') {
                                        clearInterval(pollInterval);
                                    }
                                } catch (e) {
                                    console.error(e);
                                }
                            }, 1500);
                        }
                
                        async function consultarManual() {
                            const proto = document.getElementById('inputProtocolo').value.trim();
                            if (!proto) return alert('Por favor, informe um protocolo.');
                            currentProtocolo = proto;
                            try {
                                const res = await fetch(`/api/relatorios/${proto}/status`);
                                if (!res.ok) {
                                    alert('Protocolo não encontrado ou erro na consulta.');
                                    return;
                                }
                                const data = await res.json();
                                exibirStatus(data.protocolo, data.status, data.mensagemErro || 'Status consultado com sucesso.');
                                if (data.status !== 'CONCLUIDO' && data.status !== 'ERRO') {
                                    iniciarPolling(data.protocolo);
                                }
                            } catch (err) {
                                alert('Erro ao consultar protocolo: ' + err.message);
                            }
                        }
                
                        function baixarArquivo() {
                            if (!currentProtocolo) return;
                            window.location.href = `/api/relatorios/${currentProtocolo}/download`;
                        }
                    </script>
                </body>
                </html>
                """;
        return ResponseEntity.ok(html);
    }
}