package com.decodex.br.testesunitarios.domain.validations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.domain.exeption.RegraDeNegocioException;
import com.decodex.br.domain.validations.EnderecoValidation;

@DisplayName("Testes unitários para EnderecoValidation")
class EnderecoValidationTest {

    @Test
    @DisplayName("Deve validar campo não nulo com sucesso")
    void deveValidarCampoNaoNuloComSucesso() {
        String resultado = EnderecoValidation.validarCampoNaoNulo("Av Brasil", "logradouro");
        assertThat(resultado).isEqualTo("Av Brasil");
    }

    @Test
    @DisplayName("Deve lançar exceção quando campo for nulo")
    void deveLancarExcecaoQuandoCampoNulo() {
        assertThatThrownBy(() -> EnderecoValidation.validarCampoNaoNulo(null, "logradouro"))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("logradouro não pode ser nulo");
    }
}
