package com.decodex.br.testesunitarios.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.domain.exception.RegraDeNegocioException;
import com.decodex.br.domain.model.Categoria;

@DisplayName("Testes unitários para Categoria")
class CategoriaTest {

    private final UUID id1 = UUID.randomUUID();
    private final UUID id2 = UUID.randomUUID();

    @Test
    @DisplayName("Deve criar categoria com dados válidos")
    void deveCriarCategoriaValida() {
        Categoria categoria = new Categoria(id1, "Alimentação");

        assertThat(categoria.getId()).isEqualTo(id1);
        assertThat(categoria.getNome()).isEqualTo("Alimentação");
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome for nulo")
    void deveLancarExcecaoQuandoNomeNulo() {
        assertThatThrownBy(() -> new Categoria(id1, null))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Nome não pode ser vazio");
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome for vazio")
    void deveLancarExcecaoQuandoNomeVazio() {
        assertThatThrownBy(() -> new Categoria(id1, "   "))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Nome não pode ser vazio");
    }

    @Test
    @DisplayName("Deve considerar duas categorias iguais quando id e nome forem iguais")
    void testEqualsAndHashCode() {
        Categoria cat1 = new Categoria(id1, "Lazer");
        Categoria cat2 = new Categoria(id1, "Lazer");

        assertThat(cat1).isEqualTo(cat2);
        assertThat(cat1.hashCode()).isEqualTo(cat2.hashCode());
    }

    @Test
    @DisplayName("Deve considerar categorias diferentes quando IDs mudarem ou comparadas com null/outros tipos")
    void testEqualsDiferentes() {
        Categoria cat1 = new Categoria(id1, "Lazer");
        Categoria cat2 = new Categoria(id2, "Lazer"); // ID diferente

        // Testa IDs diferentes
        assertThat(cat1).isNotEqualTo(cat2);
        
        // Testa cobertura do: if (o == null)
        assertThat(cat1).isNotEqualTo(null);
        
        // Testa cobertura do: if (getClass() != o.getClass())
        assertThat(cat1).isNotEqualTo(new Object()); 
    }

    @Test
    @DisplayName("Deve alterar nome com sucesso")
    void deveAlterarNome() {
        Categoria cat = new Categoria(id1, "Lazer");
        cat.alterarNome("Cinema");
        assertThat(cat.getNome()).isEqualTo("Cinema");
    }

    @Test
    @DisplayName("Deve atualizar nome com sucesso via atualizar")
    void deveAtualizar() {
        Categoria cat = new Categoria(id1, "Lazer");
        cat.atualizar("Viagens");
        assertThat(cat.getNome()).isEqualTo("Viagens");
    }

    @Test
    @DisplayName("Deve atualizar campos via atualizarCampos")
    void deveAtualizarCampos() {
        Categoria cat = new Categoria(id1, "Lazer");
        cat.atualizarCampos(new Categoria(null, "Educação"));
        assertThat(cat.getNome()).isEqualTo("Educação");
    }

    @Test
    @DisplayName("Não deve atualizar campos quando novosDados for nulo")
    void naoDeveAtualizarCamposQuandoNulo() {
        Categoria cat = new Categoria(id1, "Lazer");
        cat.atualizarCampos(null);
        assertThat(cat.getNome()).isEqualTo("Lazer");
    }
}
