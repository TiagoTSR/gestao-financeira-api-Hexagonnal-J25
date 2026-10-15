package com.decodex.br.testesunitarios.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.domain.model.StatusLancamento;

@DisplayName("Testes unitários para StatusLancamento (enum rico)")
class StatusLancamentoTest {

    @Test
    @DisplayName("Deve verificar predicados de estado corretamente")
    void deveVerificarPredicadosDeEstado() {
        assertThat(StatusLancamento.PENDENTE.isPendente()).isTrue();
        assertThat(StatusLancamento.PENDENTE.isQuitado()).isFalse();
        assertThat(StatusLancamento.PENDENTE.isCancelado()).isFalse();
        assertThat(StatusLancamento.PENDENTE.permiteEdicao()).isTrue();

        assertThat(StatusLancamento.PAGO.isPendente()).isFalse();
        assertThat(StatusLancamento.PAGO.isQuitado()).isTrue();
        assertThat(StatusLancamento.PAGO.isCancelado()).isFalse();
        assertThat(StatusLancamento.PAGO.permiteEdicao()).isTrue();

        assertThat(StatusLancamento.RECEBIDO.isPendente()).isFalse();
        assertThat(StatusLancamento.RECEBIDO.isQuitado()).isTrue();
        assertThat(StatusLancamento.RECEBIDO.isCancelado()).isFalse();
        assertThat(StatusLancamento.RECEBIDO.permiteEdicao()).isTrue();

        assertThat(StatusLancamento.CANCELADO.isPendente()).isFalse();
        assertThat(StatusLancamento.CANCELADO.isQuitado()).isFalse();
        assertThat(StatusLancamento.CANCELADO.isCancelado()).isTrue();
        assertThat(StatusLancamento.CANCELADO.permiteEdicao()).isFalse();
    }

    @Test
    @DisplayName("Deve validar regras de transição de estado permitidas a partir de PENDENTE")
    void deveValidarTransicoesDePendente() {
        assertThat(StatusLancamento.PENDENTE.podeTransitarPara(StatusLancamento.PAGO)).isTrue();
        assertThat(StatusLancamento.PENDENTE.podeTransitarPara(StatusLancamento.RECEBIDO)).isTrue();
        assertThat(StatusLancamento.PENDENTE.podeTransitarPara(StatusLancamento.CANCELADO)).isTrue();
        assertThat(StatusLancamento.PENDENTE.podeTransitarPara(StatusLancamento.PENDENTE)).isTrue();
        assertThat(StatusLancamento.PENDENTE.podeTransitarPara(null)).isTrue();
    }

    @Test
    @DisplayName("Deve validar regras de transição de estado a partir de PAGO e RECEBIDO")
    void deveValidarTransicoesDeQuitado() {
        // De quitado para pendente (estorno) é permitido
        assertThat(StatusLancamento.PAGO.podeTransitarPara(StatusLancamento.PENDENTE)).isTrue();
        assertThat(StatusLancamento.RECEBIDO.podeTransitarPara(StatusLancamento.PENDENTE)).isTrue();

        // Direto para cancelado sem estorno não é permitido
        assertThat(StatusLancamento.PAGO.podeTransitarPara(StatusLancamento.CANCELADO)).isFalse();
        assertThat(StatusLancamento.RECEBIDO.podeTransitarPara(StatusLancamento.CANCELADO)).isFalse();
    }

    @Test
    @DisplayName("Deve validar regras de transição a partir de CANCELADO")
    void deveValidarTransicoesDeCancelado() {
        assertThat(StatusLancamento.CANCELADO.podeTransitarPara(StatusLancamento.PENDENTE)).isTrue();
        assertThat(StatusLancamento.CANCELADO.podeTransitarPara(StatusLancamento.PAGO)).isFalse();
        assertThat(StatusLancamento.CANCELADO.podeTransitarPara(StatusLancamento.RECEBIDO)).isFalse();
    }
}
