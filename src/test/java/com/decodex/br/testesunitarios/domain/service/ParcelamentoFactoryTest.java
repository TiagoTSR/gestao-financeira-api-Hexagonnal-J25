package com.decodex.br.testesunitarios.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.application.dto.lancamento.LancamentoCreateDTO;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.model.StatusLancamento;
import com.decodex.br.domain.model.TipoLancamento;
import com.decodex.br.domain.service.ParcelamentoFactory;

import com.decodex.br.domain.model.Endereco;

@DisplayName("Testes unitários para ParcelamentoFactory")
class ParcelamentoFactoryTest {

    private final Categoria categoria = new Categoria(UUID.randomUUID(), "Alimentação");
    private final Endereco endereco = new Endereco("Rua A", "10", null, "Centro", "00000-000", "São Paulo", "SP");
    private final Pessoa pessoa = new Pessoa(UUID.randomUUID(), "João Silva", endereco, true);

    @Test
    @DisplayName("Deve gerar lançamento único quando totalParcelas for nulo ou menor igual a 1")
    void deveGerarLancamentoUnicoQuandoTotalParcelasForNuloOuMenorIgualAUm() {
        LancamentoCreateDTO dtoNulo = new LancamentoCreateDTO(
                "Supermercado",
                LocalDate.of(2026, 10, 15),
                null,
                BigDecimal.valueOf(150.00),
                "Compras do mês",
                TipoLancamento.DESPESA,
                categoria.getId(),
                pessoa.getId(),
                StatusLancamento.PENDENTE,
                null,
                null,
                null
        );

        List<Lancamento> resultado = ParcelamentoFactory.gerarLancamentos(dtoNulo, categoria, pessoa);

        assertThat(resultado).hasSize(1);
        Lancamento unico = resultado.get(0);
        assertThat(unico.getDescricao()).isEqualTo("Supermercado");
        assertThat(unico.getValor()).isEqualByComparingTo("150.00");
        assertThat(unico.getNumeroParcela()).isEqualTo(1);
        assertThat(unico.getTotalParcelas()).isEqualTo(1);
        assertThat(unico.getValorPago()).isNull();
    }

    @Test
    @DisplayName("Deve inferir valorPago no lançamento único quando dataPagamento for informada mas valorPago for nulo")
    void deveInferirValorPagoNoLancamentoUnicoQuandoDataPagamentoInformada() {
        LocalDate hoje = LocalDate.of(2026, 10, 11);
        LancamentoCreateDTO dto = new LancamentoCreateDTO(
                "Conta de Luz",
                hoje,
                hoje,
                BigDecimal.valueOf(80.50),
                null,
                TipoLancamento.DESPESA,
                categoria.getId(),
                pessoa.getId(),
                StatusLancamento.PAGO,
                null,
                null,
                1
        );

        List<Lancamento> resultado = ParcelamentoFactory.gerarLancamentos(dto, categoria, pessoa);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getValorPago()).isEqualByComparingTo("80.50");
    }

    @Test
    @DisplayName("Deve dividir parcelas e alocar diferença de centavos na primeira parcela")
    void deveDividirParcelasEAlocarDiferencaCentavosNaPrimeiraParcela() {
        // R$ 100,00 dividido em 3 parcelas: 33.33 * 3 = 99.99 -> Primeira parcela deve ser 33.34
        LocalDate vencimento = LocalDate.of(2026, 1, 10);
        LancamentoCreateDTO dto = new LancamentoCreateDTO(
                "Curso Online",
                vencimento,
                vencimento, // pago hoje
                BigDecimal.valueOf(100.00),
                "Inscrição",
                TipoLancamento.DESPESA,
                categoria.getId(),
                pessoa.getId(),
                StatusLancamento.PAGO,
                null,
                null,
                3
        );

        List<Lancamento> parcelas = ParcelamentoFactory.gerarLancamentos(dto, categoria, pessoa);

        assertThat(parcelas).hasSize(3);

        // Parcela 1
        Lancamento p1 = parcelas.get(0);
        assertThat(p1.getDescricao()).isEqualTo("Curso Online (1/3)");
        assertThat(p1.getDataVencimento()).isEqualTo(LocalDate.of(2026, 1, 10));
        assertThat(p1.getDataPagamento()).isEqualTo(vencimento);
        assertThat(p1.getValor()).isEqualByComparingTo("33.34");
        assertThat(p1.getValorPago()).isEqualByComparingTo("33.34");
        assertThat(p1.getStatus()).isEqualTo(StatusLancamento.PAGO);
        assertThat(p1.getNumeroParcela()).isEqualTo(1);
        assertThat(p1.getTotalParcelas()).isEqualTo(3);

        // Parcela 2
        Lancamento p2 = parcelas.get(1);
        assertThat(p2.getDescricao()).isEqualTo("Curso Online (2/3)");
        assertThat(p2.getDataVencimento()).isEqualTo(LocalDate.of(2026, 2, 10));
        assertThat(p2.getDataPagamento()).isNull();
        assertThat(p2.getValor()).isEqualByComparingTo("33.33");
        assertThat(p2.getValorPago()).isNull();
        assertThat(p2.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
        assertThat(p2.getNumeroParcela()).isEqualTo(2);
        assertThat(p2.getTotalParcelas()).isEqualTo(3);

        // Parcela 3
        Lancamento p3 = parcelas.get(2);
        assertThat(p3.getDescricao()).isEqualTo("Curso Online (3/3)");
        assertThat(p3.getDataVencimento()).isEqualTo(LocalDate.of(2026, 3, 10));
        assertThat(p3.getDataPagamento()).isNull();
        assertThat(p3.getValor()).isEqualByComparingTo("33.33");
        assertThat(p3.getValorPago()).isNull();
        assertThat(p3.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
        assertThat(p3.getNumeroParcela()).isEqualTo(3);
        assertThat(p3.getTotalParcelas()).isEqualTo(3);

        // Soma total
        BigDecimal soma = p1.getValor().add(p2.getValor()).add(p3.getValor());
        assertThat(soma).isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("Deve cobrir construtor privado por reflexão")
    void deveCobrirConstrutorPrivado() throws Exception {
        Constructor<ParcelamentoFactory> constructor = ParcelamentoFactory.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        ParcelamentoFactory instancia = constructor.newInstance();
        assertThat(instancia).isNotNull();
    }
}
