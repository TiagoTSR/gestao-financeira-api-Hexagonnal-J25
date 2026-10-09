package com.decodex.br.domain.model;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResumo(
    BigDecimal totalReceitas,
    BigDecimal totalDespesas,
    BigDecimal saldoMes,
    BigDecimal saldoConsolidado,
    BigDecimal totalPendente,
    BigDecimal totalVencido,
    long quantidadeVencidos,
    List<CategoriaEstatistica> despesasPorCategoria,
    List<MesFluxo> fluxoMensal,
    List<Lancamento> ultimosLancamentos
) {
    public record CategoriaEstatistica(
        String categoria,
        BigDecimal total,
        BigDecimal percentual
    ) {}

    public record MesFluxo(
        String mesAno,
        String mesNome,
        BigDecimal receitas,
        BigDecimal despesas,
        BigDecimal saldo
    ) {}
}
