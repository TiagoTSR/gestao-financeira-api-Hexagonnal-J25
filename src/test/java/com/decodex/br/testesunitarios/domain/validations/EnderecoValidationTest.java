package com.decodex.br.testesunitarios.domain.validations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.domain.validations.EnderecoValidation;

@DisplayName("Testes unitários para EnderecoValidation")
class EnderecoValidationTest {

    private EnderecoValidation validation;

    @BeforeEach
    void setUp() {
        validation = new EnderecoValidation();
    }

    @Test
    @DisplayName("Deve validar campo não nulo com sucesso")
    void deveValidarCampoNaoNuloComSucesso() {
        String resultado = validation.validarCampoNaoNulo("Av Brasil", "logradouro");
        assertThat(resultado).isEqualTo("Av Brasil");
    }

    @Test
    @DisplayName("Deve lançar exceção quando campo for nulo")
    void deveLancarExcecaoQuandoCampoNulo() {
        assertThatThrownBy(() -> validation.validarCampoNaoNulo(null, "logradouro"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("logradouro não pode ser nulo");
    }
}
