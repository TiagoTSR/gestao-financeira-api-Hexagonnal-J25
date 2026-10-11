import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

// Métricas customizadas para análise refinada
const errorRate = new Rate('custom_error_rate');
const loginTrend = new Trend('tempo_login', true);
const lancamentoReadTrend = new Trend('tempo_leitura_lancamentos', true);
const lancamentoWriteTrend = new Trend('tempo_escrita_lancamento', true);
const relatorioEnqueueTrend = new Trend('tempo_enfileiramento_relatorio', true);
const relatoriosSolicitados = new Counter('total_relatorios_enfileirados');

export const options = {
    stages: [
        { duration: '30s', target: 200 },  // Aquecimento gradual até 200 VUs
        { duration: '1m',  target: 1000 }, // Subida de carga até 1.000 VUs simultâneos
        { duration: '2m',  target: 1000 }, // Sustentação em 1.000 VUs sob alta concorrência
        { duration: '30s', target: 0 },    // Desaceleração controlada (Ramp-down)
    ],
    thresholds: {
        http_req_failed: ['rate<0.02'],       // Menos de 2% de erros aceitáveis sob estresse extremo
        http_req_duration: ['p(95)<2000'],    // 95% das requisições abaixo de 2 segundos
        'tempo_leitura_lancamentos': ['p(95)<500'], // Leituras rápidas p(95) < 500ms
        'tempo_enfileiramento_relatorio': ['p(95)<600'], // RabbitMQ assíncrono rápido p(95) < 600ms
    },
};

const BASE_URL = __ENV.API_URL || 'http://localhost:8080';

// Setup inicial antes dos 1000 usuários
export function setup() {
    console.log(`[k6] Iniciando teste de estresse contra: ${BASE_URL}`);
    const healthCheck = http.get(`${BASE_URL}/v3/api-docs`);
    if (healthCheck.status !== 200 && healthCheck.status !== 401 && healthCheck.status !== 403) {
        console.warn(`[k6] Alerta: Endpoint base retornou status ${healthCheck.status}. Certifique-se de que a API está rodando.`);
    }
    return { baseUrl: BASE_URL };
}

export default function (data) {
    const url = data.baseUrl;
    let token = null;

    // ─────────────────────────────────────────────────────────────
    // 1. ETAPA DE AUTENTICAÇÃO (Login JWT)
    // ─────────────────────────────────────────────────────────────
    group('01_Autenticacao_JWT', function () {
        const payloadLogin = JSON.stringify({
            username: 'admin',
            password: '123456'
        });

        const params = {
            headers: { 'Content-Type': 'application/json' },
            tags: { name: 'POST /auth/login' }
        };

        const resLogin = http.post(`${url}/auth/login`, payloadLogin, params);
        loginTrend.add(resLogin.timings.duration);

        const loginOk = check(resLogin, {
            'login efetuado com sucesso (200)': (r) => r.status === 200,
            'token jwt retornado': (r) => r.json('accessToken') !== undefined || r.json('token') !== undefined
        });

        errorRate.add(!loginOk);

        if (loginOk) {
            token = resLogin.json('accessToken') || resLogin.json('token');
        }
    });

    if (!token) {
        // Se a autenticação falhar, aguarda e tenta novamente no próximo ciclo
        sleep(1);
        return;
    }

    const authHeaders = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    };

    // ─────────────────────────────────────────────────────────────
    // 2. ETAPA DE LEITURA (Consultas Rápidas no Pool HikariCP)
    // ─────────────────────────────────────────────────────────────
    group('02_Consulta_Lancamentos', function () {
        const resList = http.get(`${url}/lancamentos?size=10&page=0`, {
            ...authHeaders,
            tags: { name: 'GET /lancamentos' }
        });
        lancamentoReadTrend.add(resList.timings.duration);

        const listOk = check(resList, {
            'listagem de lançamentos retornou 200': (r) => r.status === 200,
        });
        errorRate.add(!listOk);
    });

    sleep(Math.random() * 0.5 + 0.2); // Think time curto

    // ─────────────────────────────────────────────────────────────
    // 3. ETAPA DE ESCRITA CONCORRENTE COM AUDITORIA (Envers)
    // ─────────────────────────────────────────────────────────────
    group('03_Criacao_Lancamento', function () {
        const payloadLancamento = JSON.stringify({
            descricao: `Despesa Teste Estresse VU-${__VU}-${Date.now()}`,
            dataVencimento: '2026-10-25',
            valor: parseFloat((Math.random() * 500 + 10).toFixed(2)),
            tipo: 'DESPESA',
            categoria: { id: '00000000-0000-0000-0000-000000000001' },
            pessoa: { id: '00000000-0000-0000-0000-000000000001' }
        });

        const resPost = http.post(`${url}/lancamentos`, payloadLancamento, {
            ...authHeaders,
            tags: { name: 'POST /lancamentos' }
        });
        lancamentoWriteTrend.add(resPost.timings.duration);

        const writeOk = check(resPost, {
            'lancamento criado ou validado com sucesso': (r) => r.status === 201 || r.status === 200 || r.status === 400
        });
        errorRate.add(!writeOk);
    });

    sleep(Math.random() * 0.5 + 0.2);

    // ─────────────────────────────────────────────────────────────
    // 4. ETAPA DE ENFILEIRAMENTO ASSÍNCRONO NO RABBITMQ (com Protocolo)
    // ─────────────────────────────────────────────────────────────
    group('04_Enfileiramento_Relatorio_RabbitMQ', function () {
        const formato = (__ITER % 2 === 0) ? 'EXCEL' : 'PDF';
        const payloadRelatorio = JSON.stringify({
            tipo: formato,
            inicio: '2026-10-01',
            fim: '2026-10-31'
        });

        const resSolicitar = http.post(`${url}/api/relatorios/solicitar`, payloadRelatorio, {
            ...authHeaders,
            tags: { name: 'POST /api/relatorios/solicitar' }
        });
        relatorioEnqueueTrend.add(resSolicitar.timings.duration);

        const solicitacaoOk = check(resSolicitar, {
            'solicitação enfileirada no RabbitMQ com 202': (r) => r.status === 202,
            'protocolo retornado': (r) => r.json('protocolo') !== undefined
        });
        errorRate.add(!solicitacaoOk);

        if (solicitacaoOk) {
            relatoriosSolicitados.add(1);
            const protocolo = resSolicitar.json('protocolo');

            // 5. Consulta não-bloqueante de status
            const resStatus = http.get(`${url}/api/relatorios/${protocolo}/status`, {
                ...authHeaders,
                tags: { name: 'GET /api/relatorios/{protocolo}/status' }
            });

            check(resStatus, {
                'status do protocolo consultado com 200': (r) => r.status === 200
            });
        }
    });

    // Think time realista entre iterações da VU (1 a 2 segundos)
    sleep(Math.random() * 1.5 + 0.5);
}

// Geração de relatório visual HTML e resumo de console após conclusão
export function handleSummary(data) {
    const summaryHtml = `
    <!DOCTYPE html>
    <html lang="pt-BR">
    <head>
        <meta charset="UTF-8">
        <title>Relatório do Teste de Sobrecarga - 1.000 Usuários</title>
        <style>
            body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #0f172a; color: #f8fafc; padding: 2rem; }
            .container { max-width: 900px; margin: 0 auto; background: #1e293b; border-radius: 12px; padding: 2rem; box-shadow: 0 10px 25px rgba(0,0,0,0.5); }
            h1 { color: #38bdf8; margin-bottom: 0.5rem; }
            .badge { display: inline-block; padding: 0.35rem 0.75rem; border-radius: 9999px; font-weight: bold; background: #334155; color: #38bdf8; margin-bottom: 1.5rem; }
            table { width: 100%; border-collapse: collapse; margin-top: 1rem; }
            th, td { text-align: left; padding: 0.75rem 1rem; border-bottom: 1px solid #334155; }
            th { background: #0b1120; color: #94a3b8; }
            tr:hover { background: #334155; }
            .metric-val { font-family: monospace; font-weight: bold; color: #34d399; }
        </style>
    </head>
    <body>
        <div class="container">
            <h1>🚀 Relatório de Teste de Sobrecarga (k6)</h1>
            <div class="badge">Simulação de 1.000 Usuários Simultâneos</div>
            <p>API: <strong>${BASE_URL}</strong> | Cenário: Autenticação JWT + Leitura + Escrita/Auditoria + RabbitMQ Assíncrono</p>
            <table>
                <thead>
                    <tr>
                        <th>Métrica</th>
                        <th>Valor Registrado</th>
                    </tr>
                </thead>
                <tbody>
                    <tr><td>Total de Requisições HTTP</td><td class="metric-val">${data.metrics.http_reqs ? data.metrics.http_reqs.values.count : 'N/A'}</td></tr>
                    <tr><td>Throughput Médio (RPS)</td><td class="metric-val">${data.metrics.http_reqs ? data.metrics.http_reqs.values.rate.toFixed(2) + ' req/s' : 'N/A'}</td></tr>
                    <tr><td>Latência Média Geral</td><td class="metric-val">${data.metrics.http_req_duration ? data.metrics.http_req_duration.values.avg.toFixed(2) + ' ms' : 'N/A'}</td></tr>
                    <tr><td>Latência p(95)</td><td class="metric-val">${data.metrics.http_req_duration ? data.metrics.http_req_duration.values['p(95)'].toFixed(2) + ' ms' : 'N/A'}</td></tr>
                    <tr><td>Latência p(99)</td><td class="metric-val">${data.metrics.http_req_duration ? data.metrics.http_req_duration.values['p(99)'].toFixed(2) + ' ms' : 'N/A'}</td></tr>
                    <tr><td>Taxa de Falha HTTP</td><td class="metric-val">${data.metrics.http_req_failed ? (data.metrics.http_req_failed.values.rate * 100).toFixed(2) + '%' : 'N/A'}</td></tr>
                </tbody>
            </table>
        </div>
    </body>
    </html>
    `;

    return {
        'stress-test-report.html': summaryHtml,
        stdout: textSummary(data, { indent: ' ', enableColors: true })
    };
}

function textSummary(data, options) {
    return `\n=======================================================\n` +
           `   TESTE DE SOBRECARGA (1.000 USUÁRIOS) CONCLUÍDO      \n` +
           `=======================================================\n` +
           `Verifique o relatório HTML detalhado em: stress-test-report.html\n`;
}
