package com.decodex.br.testesunitarios.domain.validations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.domain.exeption.RegraDeNegocioException;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.validations.PessoaValidation;

@DisplayName("Testes unitários para PessoaValidation")
class PessoaValidationTest {

    @Test
    @DisplayName("Deve validar id com sucesso")
    void deveValidarIdComSucesso() {
        Long id = PessoaValidation.validarId(1L);
        assertThat(id).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando id for nulo")
    void deveLancarExcecaoQuandoIdNulo() {
        assertThatThrownBy(() -> PessoaValidation.validarId(null))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Id não pode ser nulo");
    }

    @Test
    @DisplayName("Deve validar nome com sucesso")
    void deveValidarNomeComSucesso() {
        String nome = PessoaValidation.validarNome("João Silva", "Nome");
        assertThat(nome).isEqualTo("João Silva");

        String nomePadrao = PessoaValidation.validarNome("Maria Santos");
        assertThat(nomePadrao).isEqualTo("Maria Santos");
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome for nulo")
    void deveLancarExcecaoQuandoNomeNulo() {
        assertThatThrownBy(() -> PessoaValidation.validarNome(null, "Nome"))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Nome não pode ser vazio");
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome for em branco")
    void deveLancarExcecaoQuandoNomeEmBranco() {
        assertThatThrownBy(() -> PessoaValidation.validarNome("   ", "Nome"))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Nome não pode ser vazio");
    }

    @Test
    @DisplayName("Deve validar endereço com sucesso")
    void deveValidarEnderecoComSucesso() {
        Endereco endereco = new Endereco("Rua A", "123", null, "Bairro", "12345-678", "Cidade", "SP");
        Endereco resultado = PessoaValidation.validarEndereco(endereco);
        assertThat(resultado).isEqualTo(endereco);
    }

    @Test
    @DisplayName("Deve lançar exceção quando endereço for nulo")
    void deveLancarExcecaoQuandoEnderecoNulo() {
        assertThatThrownBy(() -> PessoaValidation.validarEndereco(null))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Endereço não pode ser nulo");
    }

    @Test
    @DisplayName("Deve validar ativo com sucesso")
    void deveValidarAtivoComSucesso() {
        Boolean ativo = PessoaValidation.validarAtivo(true);
        assertThat(ativo).isTrue();

        Boolean inativo = PessoaValidation.validarAtivo(false);
        assertThat(inativo).isFalse();
    }

    @Test
    @DisplayName("Deve lançar exceção quando ativo for nulo")
    void deveLancarExcecaoQuandoAtivoNulo() {
        assertThatThrownBy(() -> PessoaValidation.validarAtivo(null))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Ativo não pode ser vazio (deve ser true ou false)");
    }
}
