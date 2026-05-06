package com.decodex.br.testesunitarios.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.domain.model.Categoria;

@DisplayName("Testes unitários para Categoria")
class CategoriaTest {

    @Test
    @DisplayName("Deve criar categoria com dados válidos")
    void deveCriarCategoriaValida() {
        Categoria categoria = new Categoria(1L, "Alimentação");

        assertThat(categoria.getId()).isEqualTo(1L);
        assertThat(categoria.getNome()).isEqualTo("Alimentação");
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome for nulo")
    void deveLancarExcecaoQuandoNomeNulo() {
        assertThatThrownBy(() -> new Categoria(1L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nome não pode ser vazio");
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome for vazio")
    void deveLancarExcecaoQuandoNomeVazio() {
        assertThatThrownBy(() -> new Categoria(1L, "   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nome não pode ser vazio");
    }

    @Test
    @DisplayName("Deve considerar duas categorias iguais quando id e nome forem iguais")
    void testEqualsAndHashCode() {
        Categoria cat1 = new Categoria(1L, "Lazer");
        Categoria cat2 = new Categoria(1L, "Lazer");

        assertThat(cat1).isEqualTo(cat2);
        assertThat(cat1.hashCode()).isEqualTo(cat2.hashCode());
    }

    @Test
    @DisplayName("Deve considerar categorias diferentes quando id ou nome mudar")
    void testEqualsDiferentes() {
        Categoria cat1 = new Categoria(1L, "Lazer");
        Categoria cat2 = new Categoria(2L, "Lazer");
        Categoria cat3 = new Categoria(1L, "Saúde");

        assertThat(cat1).isNotEqualTo(cat2);
        assertThat(cat1).isNotEqualTo(cat3);
    }
}
