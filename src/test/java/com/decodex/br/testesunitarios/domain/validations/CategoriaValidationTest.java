package com.decodex.br.testesunitarios.domain.validations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.domain.validations.CategoriaValidation;

@DisplayName("Testes unitários para CategoriaValidation")
class CategoriaValidationTest {

    private CategoriaValidation validation;

    @BeforeEach
    void setUp() {
        validation = new CategoriaValidation();
    }

    @Test
    @DisplayName("Deve validar nome com sucesso")
    void deveValidarNomeComSucesso() {
        String resultado = validation.validarNome("Alimentação", "Nome");
        assertThat(resultado).isEqualTo("Alimentação");

        String resultadoPadrao = validation.validarNome("Lazer");
        assertThat(resultadoPadrao).isEqualTo("Lazer");
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome for nulo")
    void deveLancarExcecaoQuandoNomeNulo() {
        assertThatThrownBy(() -> validation.validarNome(null, "Nome"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nome não pode ser vazio");
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome for em branco")
    void deveLancarExcecaoQuandoNomeEmBranco() {
        assertThatThrownBy(() -> validation.validarNome("   ", "Nome"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nome não pode ser vazio");
    }
}
