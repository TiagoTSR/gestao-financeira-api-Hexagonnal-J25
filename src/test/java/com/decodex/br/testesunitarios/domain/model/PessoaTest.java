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
    private static final Endereco ENDERECO = new Endereco(
        "Rua B", "456", null, "Jardim", "98765-432", "Rio de Janeiro", "RJ"
    );
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
    @DisplayName("Deve lançar exceção quando endereco for nulo")
    void deveLancarExcecaoQuandoEnderecoNulo() {
        assertThatThrownBy(() -> new Pessoa(ID, NOME, null, ATIVO))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Endereço não pode ser nulo");
    }

    @Test
    @DisplayName("Deve considerar duas pessoas iguais quando possuírem o mesmo ID")
    void testEqualsAndHashCode() {
        Endereco outroEndereco = new Endereco(
            "Rua C", "789", null, "Centro", "11111-111", "São Paulo", "SP"
        );
        Pessoa pessoa1 = new Pessoa(ID, "Maria Oliveira", ENDERECO, true);
        Pessoa pessoa2 = new Pessoa(ID, "Outro Nome", outroEndereco, false);

        assertThat(pessoa1).isEqualTo(pessoa2);
        assertThat(pessoa1.hashCode()).isEqualTo(pessoa2.hashCode());
    }

    @Test
    @DisplayName("Deve considerar pessoas diferentes quando IDs mudarem ou comparadas com null/outros tipos")
    void testEqualsDiferentes() {
        Pessoa pessoa1 = new Pessoa(ID, NOME, ENDERECO, ATIVO);
        Pessoa pessoa2 = new Pessoa(2L, NOME, ENDERECO, ATIVO);

        assertThat(pessoa1).isNotEqualTo(pessoa2);
        assertThat(pessoa1).isNotEqualTo(null);
        assertThat(pessoa1).isNotEqualTo(new Object());
    }
}