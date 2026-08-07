package com.decodex.br.testesunitarios.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.decodex.br.domain.model.Senha;

@DisplayName("Testes unitários para Senha (Value Object)")
class SenhaTest {

    @Test
    @DisplayName("Deve criar senha válida")
    void deveCriarSenhaValida() {
        Senha senha = new Senha("$2a$10$abc");

        assertThat(senha.getHash()).isEqualTo("$2a$10$abc");
    }

    @Test
    @DisplayName("Deve lançar exceção quando a senha for nula")
    void deveLancarExcecaoQuandoSenhaNula() {
        assertThatThrownBy(() -> new Senha(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A senha não pode ser vazia.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando a senha for vazia")
    void deveLancarExcecaoQuandoSenhaVazia() {
        assertThatThrownBy(() -> new Senha("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A senha não pode ser vazia.");
    }
}
