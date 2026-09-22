package com.decodex.br.testesunitarios.domain.validations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.domain.validations.UsuarioValidation;

@DisplayName("Testes unitários para UsuarioValidation")
class UsuarioValidationTest {

    private UsuarioValidation validation;

    @BeforeEach
    void setUp() {
        validation = new UsuarioValidation();
    }

    @Test
    @DisplayName("Deve validar username com sucesso")
    void deveValidarUsernameComSucesso() {
        String username = validation.validarUsername("admin");
        assertThat(username).isEqualTo("admin");
    }

    @Test
    @DisplayName("Deve lançar exceção quando username for nulo ou em branco")
    void deveLancarExcecaoQuandoUsernameInvalido() {
        assertThatThrownBy(() -> validation.validarUsername(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O nome de usuário não pode ser vazio.");

        assertThatThrownBy(() -> validation.validarUsername("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O nome de usuário não pode ser vazio.");
    }

    @Test
    @DisplayName("Deve validar email válido com sucesso")
    void deveValidarEmailComSucesso() {
        String email = validation.validarEmail("usuario@empresa.com");
        assertThat(email).isEqualTo("usuario@empresa.com");
    }

    @Test
    @DisplayName("Deve lançar exceção quando email for nulo ou em branco")
    void deveLancarExcecaoQuandoEmailVazio() {
        assertThatThrownBy(() -> validation.validarEmail(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O e-mail não pode ser vazio.");

        assertThatThrownBy(() -> validation.validarEmail("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O e-mail não pode ser vazio.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando email não possuir arroba")
    void deveLancarExcecaoQuandoEmailSemArroba() {
        assertThatThrownBy(() -> validation.validarEmail("emailinvalido.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("E-mail inválido.");
    }
}
