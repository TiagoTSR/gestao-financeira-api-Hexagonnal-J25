import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

// Métricas customizadas
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
        http_req_failed: ['rate<0.05'],       // Menos de 5% de erros sob carga máxima
        http_req_duration: ['p(95)<3000'],    // 95% das requisições abaixo de 3s em máquina local
        'tempo_leitura_lancamentos': ['p(95)<1000'],
        'tempo_escrita_lancamento': ['p(95)<1500'],
        'tempo_enfileiramento_relatorio': ['p(95)<1000'],
    },
};

const BASE_URL = __ENV.API_URL || 'http://localhost:8080';

// Setup inicial antes dos 1000 usuários
export function setup() {
    console.log(`[k6] Iniciando setup contra: ${BASE_URL}`);

    const resLogin = http.post(`${BASE_URL}/auth/login`, JSON.stringify({
        username: 'admin',
        password: '123456'
    }), { headers: { 'Content-Type': 'application/json' } });

    let categoriaId = null;
    let pessoaId = null;

    if (resLogin.status === 200) {
        const token = resLogin.json('accessToken') || resLogin.json('token');
        const headers = { Authorization: `Bearer ${token}` };

        // Buscar categoria existente no banco
        const resCat = http.get(`${BASE_URL}/categorias`, { headers });
        if (resCat.status === 200) {
            const dataCat = resCat.json();
            const list = Array.isArray(dataCat) ? dataCat : (dataCat.content || []);
            if (list.length > 0) categoriaId = list[0].id;
        }

        // Buscar pessoa existente no banco
        const resPes = http.get(`${BASE_URL}/pessoas?size=5`, { headers });
        if (resPes.status === 200) {
            const dataPes = resPes.json();
            const list = Array.isArray(dataPes) ? dataPes : (dataPes.content || []);
            if (list.length > 0) pessoaId = list[0].id;
        }
    }

    console.log(`[k6] Dados carregados - Categoria: ${categoriaId}, Pessoa: ${pessoaId}`);
    return { baseUrl: BASE_URL, categoriaId, pessoaId };
}

export default function (data) {
    const url = data.baseUrl;
    let token = null;

    // ─────────────────────────────────────────────────────────────
    // 1. ETAPA DE AUTENTICAÇÃO (Login JWT)
    // ─────────────────────────────────────────────────────────────
    group('01_Autenticacao_JWT', function () {
        const resLogin = http.post(`${url}/auth/login`, JSON.stringify({
            username: 'admin',
            password: '123456'
        }), {
            headers: { 'Content-Type': 'application/json' },
            tags: { name: 'POST /auth/login' }
        });

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

    sleep(Math.random() * 0.8 + 0.4);

    // ─────────────────────────────────────────────────────────────
    // 3. ETAPA DE ESCRITA CONCORRENTE COM AUDITORIA (Envers)
    // ─────────────────────────────────────────────────────────────
    if (data.categoriaId && data.pessoaId) {
        group('03_Criacao_Lancamento', function () {
            const payloadLancamento = JSON.stringify({
                descricao: `Despesa Teste Estresse VU-${__VU}-${Date.now()}`,
                dataVencimento: '2026-10-25',
                valor: parseFloat((Math.random() * 500 + 10).toFixed(2)),
                tipo: 'DESPESA',
                categoria: { id: data.categoriaId },
                pessoa: { id: data.pessoaId }
            });

            const resPost = http.post(`${url}/lancamentos`, payloadLancamento, {
                ...authHeaders,
                tags: { name: 'POST /lancamentos' }
            });
            lancamentoWriteTrend.add(resPost.timings.duration);

            const writeOk = check(resPost, {
                'lancamento criado com 201': (r) => r.status === 201
            });
            errorRate.add(!writeOk);
        });

        sleep(Math.random() * 0.8 + 0.4);
    }

    // ─────────────────────────────────────────────────────────────
    // 4. ETAPA DE ENFILEIRAMENTO ASSÍNCRONO NO RABBITMQ (com Protocolo)
    // ─────────────────────────────────────────────────────────────
    // 20% das iterações solicitam relatório para representar fluxo real
    if (__ITER % 5 === 0) {
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

                // Consulta não-bloqueante de status
                const resStatus = http.get(`${url}/api/relatorios/${protocolo}/status`, {
                    ...authHeaders,
                    tags: { name: 'GET /api/relatorios/{protocolo}/status' }
                });

                check(resStatus, {
                    'status do protocolo consultado com 200': (r) => r.status === 200
                });
            }
        });
    }

    // Think time realista entre ciclos do usuário (1 a 2.5 segundos)
    sleep(Math.random() * 1.5 + 1.0);
}

// Geração de relatório visual HTML
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
                    <tr><td>Total de Requisições HTTP</td><td class="metric-val">${data.metrics?.http_reqs?.values?.count ?? 'N/A'}</td></tr>
                    <tr><td>Throughput Médio (RPS)</td><td class="metric-val">${data.metrics?.http_reqs?.values?.rate != null ? Number(data.metrics.http_reqs.values.rate).toFixed(2) + ' req/s' : 'N/A'}</td></tr>
                    <tr><td>Latência Média Geral</td><td class="metric-val">${data.metrics?.http_req_duration?.values?.avg != null ? Number(data.metrics.http_req_duration.values.avg).toFixed(2) + ' ms' : 'N/A'}</td></tr>
                    <tr><td>Latência p(95)</td><td class="metric-val">${data.metrics?.http_req_duration?.values?.['p(95)'] != null ? Number(data.metrics.http_req_duration.values['p(95)']).toFixed(2) + ' ms' : 'N/A'}</td></tr>
                    <tr><td>Taxa de Falha HTTP</td><td class="metric-val">${data.metrics?.http_req_failed?.values?.rate != null ? (Number(data.metrics.http_req_failed.values.rate) * 100).toFixed(2) + '%' : 'N/A'}</td></tr>
                </tbody>
            </table>
        </div>
    </body>
    </html>
    `;

    return {
        'stress-test-report.html': summaryHtml
    };
}
