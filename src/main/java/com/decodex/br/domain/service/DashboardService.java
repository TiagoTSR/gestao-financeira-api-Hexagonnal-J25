package com.decodex.br.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.decodex.br.domain.model.DashboardResumo;
import com.decodex.br.domain.model.DashboardResumo.CategoriaEstatistica;
import com.decodex.br.domain.model.DashboardResumo.MesFluxo;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.TipoLancamento;
import com.decodex.br.domain.port.in.DashboardInputPort;
import com.decodex.br.domain.port.out.LancamentoRepositoryPort;

@Service
public class DashboardService implements DashboardInputPort {

    private final LancamentoRepositoryPort lancamentoRepositoryPort;

    public DashboardService(LancamentoRepositoryPort lancamentoRepositoryPort) {
        this.lancamentoRepositoryPort = lancamentoRepositoryPort;
    }

    @Override
    public DashboardResumo obterDashboard(LocalDate mesReferencia) {
        LocalDate dataRef = mesReferencia != null ? mesReferencia : LocalDate.now();
        YearMonth anoMesRef = YearMonth.from(dataRef);
        LocalDate hoje = LocalDate.now();

        List<Lancamento> todos = lancamentoRepositoryPort.findAll();

        // 1. Lançamentos do mês de referência
        List<Lancamento> doMes = todos.stream()
                .filter(l -> l.getDataVencimento() != null && YearMonth.from(l.getDataVencimento()).equals(anoMesRef))
                .toList();

        BigDecimal totalReceitas = doMes.stream()
                .filter(l -> l.getTipo() == TipoLancamento.RECEITA)
                .map(Lancamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDespesas = doMes.stream()
                .filter(l -> l.getTipo() == TipoLancamento.DESPESA)
                .map(Lancamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoMes = totalReceitas.subtract(totalDespesas);

        // 2. Saldo Consolidado Histórico (efetivamente pago/recebido)
        BigDecimal totalReceitasPagasHistorico = todos.stream()
                .filter(l -> l.getTipo() == TipoLancamento.RECEITA && l.getDataPagamento() != null)
                .map(Lancamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDespesasPagasHistorico = todos.stream()
                .filter(l -> l.getTipo() == TipoLancamento.DESPESA && l.getDataPagamento() != null)
                .map(Lancamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoConsolidado = totalReceitasPagasHistorico.subtract(totalDespesasPagasHistorico);

        // 3. Pendentes no mês de referência
        BigDecimal totalPendente = doMes.stream()
                .filter(l -> l.getDataPagamento() == null)
                .map(Lancamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Vencidos em aberto (geral até a data de hoje)
        List<Lancamento> vencidosAbertos = todos.stream()
                .filter(l -> l.getDataPagamento() == null && l.getDataVencimento() != null && l.getDataVencimento().isBefore(hoje))
                .toList();

        BigDecimal totalVencido = vencidosAbertos.stream()
                .map(Lancamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long quantidadeVencidos = vencidosAbertos.size();

        // 5. Despesas por Categoria no mês
        List<Lancamento> despesasMes = doMes.stream()
                .filter(l -> l.getTipo() == TipoLancamento.DESPESA)
                .toList();

        Map<String, BigDecimal> totalPorCat = despesasMes.stream()
                .collect(Collectors.groupingBy(
                        l -> l.getCategoria() != null && l.getCategoria().getNome() != null ? l.getCategoria().getNome() : "Outros",
                        Collectors.reducing(BigDecimal.ZERO, Lancamento::getValor, BigDecimal::add)
                ));

        List<CategoriaEstatistica> categorias = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : totalPorCat.entrySet()) {
            BigDecimal valorCat = entry.getValue();
            BigDecimal percentual = BigDecimal.ZERO;
            if (totalDespesas.compareTo(BigDecimal.ZERO) > 0) {
                percentual = valorCat.multiply(BigDecimal.valueOf(100))
                        .divide(totalDespesas, 2, RoundingMode.HALF_UP);
            }
            categorias.add(new CategoriaEstatistica(entry.getKey(), valorCat, percentual));
        }
        categorias.sort((a, b) -> b.total().compareTo(a.total()));

        // 6. Fluxo Mensal (últimos 6 meses)
        List<MesFluxo> fluxo = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = anoMesRef.minusMonths(i);
            BigDecimal rec = todos.stream()
                    .filter(l -> l.getDataVencimento() != null && YearMonth.from(l.getDataVencimento()).equals(ym) && l.getTipo() == TipoLancamento.RECEITA)
                    .map(Lancamento::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal desp = todos.stream()
                    .filter(l -> l.getDataVencimento() != null && YearMonth.from(l.getDataVencimento()).equals(ym) && l.getTipo() == TipoLancamento.DESPESA)
                    .map(Lancamento::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            String mesNome = ym.getMonth().getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("pt-BR"));
            fluxo.add(new MesFluxo(ym.toString(), mesNome, rec, desp, rec.subtract(desp)));
        }

        // 7. Últimos 5 lançamentos
        List<Lancamento> ultimos = todos.stream()
                .sorted(Comparator.comparing(Lancamento::getDataVencimento, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .toList();

        return new DashboardResumo(
                totalReceitas,
                totalDespesas,
                saldoMes,
                saldoConsolidado,
                totalPendente,
                totalVencido,
                quantidadeVencidos,
                categorias,
                fluxo,
                ultimos
        );
    }
}
