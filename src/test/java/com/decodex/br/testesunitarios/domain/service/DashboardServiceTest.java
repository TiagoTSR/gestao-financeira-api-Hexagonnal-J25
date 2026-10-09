package com.decodex.br.testesunitarios.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.model.DashboardResumo;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.model.TipoLancamento;
import com.decodex.br.domain.port.out.LancamentoRepositoryPort;
import com.decodex.br.domain.service.DashboardService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários para DashboardService")
class DashboardServiceTest {

    @Mock
    private LancamentoRepositoryPort lancamentoRepositoryPort;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    @DisplayName("Deve calcular corretamente os totais de receitas, despesas, saldo e pendências")
    void deveCalcularTotaisDoDashboard() {
        LocalDate mesRef = LocalDate.of(2026, 10, 15);
        Categoria catAlimentacao = new Categoria(UUID.randomUUID(), "Alimentação");
        Categoria catSalario = new Categoria(UUID.randomUUID(), "Salário");
        Pessoa pessoa = new Pessoa(UUID.randomUUID(), "Cliente Teste", new Endereco("Rua A", "1", null, "B", "00000", "C", "SP"), true);

        Lancamento receita1 = new Lancamento(
                UUID.randomUUID(), "Salário Mensal", LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 5),
                BigDecimal.valueOf(5000.00), null, TipoLancamento.RECEITA, catSalario, pessoa
        );

        Lancamento despesa1 = new Lancamento(
                UUID.randomUUID(), "Mercado", LocalDate.of(2026, 10, 10), LocalDate.of(2026, 10, 10),
                BigDecimal.valueOf(1200.00), null, TipoLancamento.DESPESA, catAlimentacao, pessoa
        );

        Lancamento despesaPendente = new Lancamento(
                UUID.randomUUID(), "Restaurante", LocalDate.of(2026, 10, 25), null,
                BigDecimal.valueOf(300.00), null, TipoLancamento.DESPESA, catAlimentacao, pessoa
        );

        when(lancamentoRepositoryPort.findAll()).thenReturn(List.of(receita1, despesa1, despesaPendente));

        DashboardResumo resumo = dashboardService.obterDashboard(mesRef);

        assertNotNull(resumo);
        assertEquals(BigDecimal.valueOf(5000.00), resumo.totalReceitas());
        assertEquals(BigDecimal.valueOf(1500.00), resumo.totalDespesas());
        assertEquals(BigDecimal.valueOf(3500.00), resumo.saldoMes());
        assertEquals(BigDecimal.valueOf(3800.00), resumo.saldoConsolidado()); // 5000 pago - 1200 pago
        assertEquals(BigDecimal.valueOf(300.00), resumo.totalPendente());
        assertFalse(resumo.despesasPorCategoria().isEmpty());
        assertEquals("Alimentação", resumo.despesasPorCategoria().get(0).categoria());
        assertEquals(BigDecimal.valueOf(1500.00), resumo.despesasPorCategoria().get(0).total());
        assertEquals(6, resumo.fluxoMensal().size());
    }
}
