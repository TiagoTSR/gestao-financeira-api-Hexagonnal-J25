package com.decodex.br.testesunitarios.domain.validations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.domain.validations.SenhaValidation;

@DisplayName("Testes unitários para SenhaValidation")
class SenhaValidationTest {

    private SenhaValidation validation;

    @BeforeEach
    void setUp() {
        validation = new SenhaValidation();
    }

    @Test
    @DisplayName("Deve validar hash de senha válido")
    void deveValidarHashValido() {
        String hash = "$2a$10$xyz123";
        String resultado = validation.validarHash(hash);
        assertThat(resultado).isEqualTo(hash);
    }

    @Test
    @DisplayName("Deve lançar exceção quando hash for nulo ou em branco")
    void deveLancarExcecaoQuandoHashInvalido() {
        assertThatThrownBy(() -> validation.validarHash(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A senha não pode ser vazia.");

        assertThatThrownBy(() -> validation.validarHash("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A senha não pode ser vazia.");
    }
}
