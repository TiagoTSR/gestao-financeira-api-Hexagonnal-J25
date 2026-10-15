package com.decodex.br.testesunitarios.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.domain.model.StatusRelatorio;

@DisplayName("Testes unitários para StatusRelatorio (enum rico)")
class StatusRelatorioTest {

    @Test
    @DisplayName("Deve verificar estados terminais, em andamento e sucesso")
    void deveVerificarEstados() {
        assertThat(StatusRelatorio.PENDENTE.isEmAndamento()).isTrue();
        assertThat(StatusRelatorio.PENDENTE.isTerminal()).isFalse();
        assertThat(StatusRelatorio.PENDENTE.isSucesso()).isFalse();

        assertThat(StatusRelatorio.PROCESSANDO.isEmAndamento()).isTrue();
        assertThat(StatusRelatorio.PROCESSANDO.isTerminal()).isFalse();
        assertThat(StatusRelatorio.PROCESSANDO.isSucesso()).isFalse();

        assertThat(StatusRelatorio.CONCLUIDO.isEmAndamento()).isFalse();
        assertThat(StatusRelatorio.CONCLUIDO.isTerminal()).isTrue();
        assertThat(StatusRelatorio.CONCLUIDO.isSucesso()).isTrue();

        assertThat(StatusRelatorio.ERRO.isEmAndamento()).isFalse();
        assertThat(StatusRelatorio.ERRO.isTerminal()).isTrue();
        assertThat(StatusRelatorio.ERRO.isSucesso()).isFalse();
    }
}
