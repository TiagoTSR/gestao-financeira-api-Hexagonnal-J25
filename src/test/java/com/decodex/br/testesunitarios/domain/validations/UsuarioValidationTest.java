package com.decodex.br.testesunitarios.domain.validations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.domain.exeption.RegraDeNegocioException;
import com.decodex.br.domain.validations.UsuarioValidation;

@DisplayName("Testes unitários para UsuarioValidation")
class UsuarioValidationTest {

    @Test
    @DisplayName("Deve validar username com sucesso")
    void deveValidarUsernameComSucesso() {
        String username = UsuarioValidation.validarUsername("admin");
        assertThat(username).isEqualTo("admin");
    }

    @Test
    @DisplayName("Deve lançar exceção quando username for nulo ou em branco")
    void deveLancarExcecaoQuandoUsernameInvalido() {
        assertThatThrownBy(() -> UsuarioValidation.validarUsername(null))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("O nome de usuário não pode ser vazio.");

        assertThatThrownBy(() -> UsuarioValidation.validarUsername("   "))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("O nome de usuário não pode ser vazio.");
    }

    @Test
    @DisplayName("Deve validar email válido com sucesso")
    void deveValidarEmailComSucesso() {
        String email = UsuarioValidation.validarEmail("usuario@empresa.com");
        assertThat(email).isEqualTo("usuario@empresa.com");
    }

    @Test
    @DisplayName("Deve lançar exceção quando email for nulo ou em branco")
    void deveLancarExcecaoQuandoEmailVazio() {
        assertThatThrownBy(() -> UsuarioValidation.validarEmail(null))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("O e-mail não pode ser vazio.");

        assertThatThrownBy(() -> UsuarioValidation.validarEmail("   "))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("O e-mail não pode ser vazio.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando email não possuir arroba")
    void deveLancarExcecaoQuandoEmailSemArroba() {
        assertThatThrownBy(() -> UsuarioValidation.validarEmail("emailinvalido.com"))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("E-mail inválido.");
    }
}
