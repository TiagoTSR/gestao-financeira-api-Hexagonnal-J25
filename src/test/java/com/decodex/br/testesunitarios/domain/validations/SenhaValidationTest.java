package com.decodex.br.testesunitarios.domain.validations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.domain.exeption.RegraDeNegocioException;
import com.decodex.br.domain.validations.SenhaValidation;

@DisplayName("Testes unitários para SenhaValidation")
class SenhaValidationTest {

    @Test
    @DisplayName("Deve validar hash de senha válido")
    void deveValidarHashValido() {
        String hash = "$2a$10$xyz123";
        String resultado = SenhaValidation.validarHash(hash);
        assertThat(resultado).isEqualTo(hash);
    }

    @Test
    @DisplayName("Deve lançar exceção quando hash for nulo ou em branco")
    void deveLancarExcecaoQuandoHashInvalido() {
        assertThatThrownBy(() -> SenhaValidation.validarHash(null))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("A senha não pode ser vazia.");

        assertThatThrownBy(() -> SenhaValidation.validarHash("   "))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("A senha não pode ser vazia.");
    }
}
