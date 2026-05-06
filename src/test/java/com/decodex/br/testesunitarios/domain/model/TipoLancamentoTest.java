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
}