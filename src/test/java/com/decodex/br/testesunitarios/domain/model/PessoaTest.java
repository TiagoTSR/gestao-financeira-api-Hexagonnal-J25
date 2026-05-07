package com.decodex.br.testesunitarios.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Pessoa;

@DisplayName("Testes unitários para Pessoa")
class PessoaTest {

    private static final Long ID = 1L;
    private static final String NOME = "Maria Oliveira";
    private static final Endereco ENDERECO = new Endereco("Rua B", "456", null, "Jardim", "98765-432", "Rio de Janeiro", "RJ");
    private static final Boolean ATIVO = true;

    @Test
    @DisplayName("Deve criar pessoa com dados válidos")
    void deveCriarPessoaValida() {
        Pessoa pessoa = new Pessoa(ID, NOME, ENDERECO, ATIVO);

        assertThat(pessoa.getId()).isEqualTo(ID);
        assertThat(pessoa.getNome()).isEqualTo(NOME);
        assertThat(pessoa.getEndereco()).isEqualTo(ENDERECO);
        assertThat(pessoa.getAtivo()).isTrue();
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome for nulo")
    void deveLancarExcecaoQuandoNomeNulo() {
        assertThatThrownBy(() -> new Pessoa(ID, null, ENDERECO, ATIVO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nome não pode ser vazio");
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome for vazio")
    void deveLancarExcecaoQuandoNomeVazio() {
        assertThatThrownBy(() -> new Pessoa(ID, "  ", ENDERECO, ATIVO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nome não pode ser vazio");
    }

    @Test
    @DisplayName("Deve lançar exceção quando ativo for nulo")
    void deveLancarExcecaoQuandoAtivoNulo() {
        assertThatThrownBy(() -> new Pessoa(ID, NOME, ENDERECO, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ativo não pode ser vazio (deve ser true ou false)");
    }

    @Test
    @DisplayName("Deve aceitar endereco nulo (opcional)")
    void deveAceitarEnderecoNulo() {
        Pessoa pessoa = new Pessoa(ID, NOME, null, ATIVO);

        assertThat(pessoa.getEndereco()).isNull();
        assertThat(pessoa.getNome()).isEqualTo(NOME);
    }
    
    @Test
    @DisplayName("Deve considerar duas pessoas iguais quando possuírem o mesmo ID")
    void testEqualsAndHashCode() {
        // Pessoas com o mesmo ID, mesmo que os outros dados sejam diferentes
        Pessoa pessoa1 = new Pessoa(ID, "Maria Oliveira", ENDERECO, true);
        Pessoa pessoa2 = new Pessoa(ID, "Outro Nome", null, false);

        assertThat(pessoa1).isEqualTo(pessoa2);
        assertThat(pessoa1.hashCode()).isEqualTo(pessoa2.hashCode());
    }

    @Test
    @DisplayName("Deve considerar pessoas diferentes quando IDs mudarem ou comparadas com null/outros tipos")
    void testEqualsDiferentes() {
        Pessoa pessoa1 = new Pessoa(ID, NOME, ENDERECO, ATIVO);
        Pessoa pessoa2 = new Pessoa(2L, NOME, ENDERECO, ATIVO); // ID diferente

        assertThat(pessoa1).isNotEqualTo(pessoa2);
        assertThat(pessoa1).isNotEqualTo(null);
        assertThat(pessoa1).isNotEqualTo(new Object()); // Garantindo que false retorne para outras classes
    }
}