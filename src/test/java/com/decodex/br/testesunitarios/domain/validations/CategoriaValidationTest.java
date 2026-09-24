package com.decodex.br.testesunitarios.domain.validations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.domain.exeption.RegraDeNegocioException;
import com.decodex.br.domain.validations.CategoriaValidation;

@DisplayName("Testes unitários para CategoriaValidation")
class CategoriaValidationTest {

    @Test
    @DisplayName("Deve validar nome com sucesso")
    void deveValidarNomeComSucesso() {
        String resultado = CategoriaValidation.validarNome("Alimentação", "Nome");
        assertThat(resultado).isEqualTo("Alimentação");

        String resultadoPadrao = CategoriaValidation.validarNome("Lazer");
        assertThat(resultadoPadrao).isEqualTo("Lazer");
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome for nulo")
    void deveLancarExcecaoQuandoNomeNulo() {
        assertThatThrownBy(() -> CategoriaValidation.validarNome(null, "Nome"))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Nome não pode ser vazio");
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome for em branco")
    void deveLancarExcecaoQuandoNomeEmBranco() {
        assertThatThrownBy(() -> CategoriaValidation.validarNome("   ", "Nome"))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Nome não pode ser vazio");
    }
}
