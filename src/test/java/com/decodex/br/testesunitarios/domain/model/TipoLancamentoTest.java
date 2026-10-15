package com.decodex.br.testesunitarios.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.domain.model.TipoLancamento;

@DisplayName("Testes unitários para TipoLancamento (enum)")
class TipoLancamentoTest {

    @Test
    @DisplayName("Deve conter os valores esperados")
    void deveConterValoresEsperados() {
        assertThat(TipoLancamento.values()).containsExactly(TipoLancamento.RECEITA, TipoLancamento.DESPESA);
    }

    @Test
    @DisplayName("RECEITA deve ter nome correto")
    void testReceita() {
        assertThat(TipoLancamento.RECEITA.name()).isEqualTo("RECEITA");
    }

    @Test
    @DisplayName("DESPESA deve ter nome correto")
    void testDespesa() {
        assertThat(TipoLancamento.DESPESA.name()).isEqualTo("DESPESA");
    }

    @Test
    @DisplayName("Deve retornar status de liquidação correto")
    void deveRetornarStatusQuitadoCorreto() {
        assertThat(TipoLancamento.RECEITA.getStatusQuitado()).isEqualTo(com.decodex.br.domain.model.StatusLancamento.RECEBIDO);
        assertThat(TipoLancamento.DESPESA.getStatusQuitado()).isEqualTo(com.decodex.br.domain.model.StatusLancamento.PAGO);
    }

    @Test
    @DisplayName("Deve validar compatibilidade com status de lançamento")
    void deveValidarCompatibilidadeComStatus() {
        assertThat(TipoLancamento.RECEITA.isCompativelCom(com.decodex.br.domain.model.StatusLancamento.RECEBIDO)).isTrue();
        assertThat(TipoLancamento.RECEITA.isCompativelCom(com.decodex.br.domain.model.StatusLancamento.PENDENTE)).isTrue();
        assertThat(TipoLancamento.RECEITA.isCompativelCom(com.decodex.br.domain.model.StatusLancamento.CANCELADO)).isTrue();
        assertThat(TipoLancamento.RECEITA.isCompativelCom(com.decodex.br.domain.model.StatusLancamento.PAGO)).isFalse();

        assertThat(TipoLancamento.DESPESA.isCompativelCom(com.decodex.br.domain.model.StatusLancamento.PAGO)).isTrue();
        assertThat(TipoLancamento.DESPESA.isCompativelCom(com.decodex.br.domain.model.StatusLancamento.PENDENTE)).isTrue();
        assertThat(TipoLancamento.DESPESA.isCompativelCom(com.decodex.br.domain.model.StatusLancamento.CANCELADO)).isTrue();
        assertThat(TipoLancamento.DESPESA.isCompativelCom(com.decodex.br.domain.model.StatusLancamento.RECEBIDO)).isFalse();
    }

    @Test
    @DisplayName("Deve calcular impacto financeiro com sinal correto")
    void deveCalcularImpactoFinanceiro() {
        java.math.BigDecimal valor = new java.math.BigDecimal("150.00");
        assertThat(TipoLancamento.RECEITA.calcularImpacto(valor)).isEqualByComparingTo(new java.math.BigDecimal("150.00"));
        assertThat(TipoLancamento.DESPESA.calcularImpacto(valor)).isEqualByComparingTo(new java.math.BigDecimal("-150.00"));
        assertThat(TipoLancamento.RECEITA.calcularImpacto(null)).isEqualTo(java.math.BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve identificar corretamente tipo receita e despesa")
    void deveIdentificarTipo() {
        assertThat(TipoLancamento.RECEITA.isReceita()).isTrue();
        assertThat(TipoLancamento.RECEITA.isDespesa()).isFalse();
        assertThat(TipoLancamento.DESPESA.isReceita()).isFalse();
        assertThat(TipoLancamento.DESPESA.isDespesa()).isTrue();
    }
}