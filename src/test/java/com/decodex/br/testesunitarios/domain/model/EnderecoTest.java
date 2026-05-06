package com.decodex.br.testesunitarios.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.domain.model.Endereco;

@DisplayName("Testes unitários para Endereco (Embeddable)")
class EnderecoTest {

    private static final String LOGRADOURO = "Rua A";
    private static final String NUMERO = "123";
    private static final String COMPLEMENTO = "Apto 45";
    private static final String BAIRRO = "Centro";
    private static final String CEP = "12345-678";
    private static final String CIDADE = "São Paulo";
    private static final String ESTADO = "SP";

    @Test
    @DisplayName("Deve criar endereço com todos os campos válidos")
    void deveCriarEnderecoValido() {
        Endereco endereco = new Endereco(LOGRADOURO, NUMERO, COMPLEMENTO, BAIRRO, CEP, CIDADE, ESTADO);

        assertThat(endereco.getLogradouro()).isEqualTo(LOGRADOURO);
        assertThat(endereco.getNumero()).isEqualTo(NUMERO);
        assertThat(endereco.getComplemento()).isEqualTo(COMPLEMENTO);
        assertThat(endereco.getBairro()).isEqualTo(BAIRRO);
        assertThat(endereco.getCep()).isEqualTo(CEP);
        assertThat(endereco.getCidade()).isEqualTo(CIDADE);
        assertThat(endereco.getEstado()).isEqualTo(ESTADO);
    }

    @Test
    @DisplayName("Deve lançar exceção quando logradouro for nulo")
    void deveLancarExcecaoQuandoLogradouroNulo() {
        assertThatThrownBy(() -> new Endereco(null, NUMERO, COMPLEMENTO, BAIRRO, CEP, CIDADE, ESTADO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("logradouro não pode ser nulo");
    }

    @Test
    @DisplayName("Deve lançar exceção quando bairro for nulo")
    void deveLancarExcecaoQuandoBairroNulo() {
        assertThatThrownBy(() -> new Endereco(LOGRADOURO, NUMERO, COMPLEMENTO, null, CEP, CIDADE, ESTADO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("bairro não pode ser nulo");
    }

    @Test
    @DisplayName("Deve lançar exceção quando CEP for nulo")
    void deveLancarExcecaoQuandoCepNulo() {
        assertThatThrownBy(() -> new Endereco(LOGRADOURO, NUMERO, COMPLEMENTO, BAIRRO, null, CIDADE, ESTADO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cep não pode ser nulo");
    }

    @Test
    @DisplayName("Deve lançar exceção quando cidade for nula")
    void deveLancarExcecaoQuandoCidadeNula() {
        assertThatThrownBy(() -> new Endereco(LOGRADOURO, NUMERO, COMPLEMENTO, BAIRRO, CEP, null, ESTADO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cidade não pode ser nulo");
    }

    @Test
    @DisplayName("Deve lançar exceção quando estado for nulo")
    void deveLancarExcecaoQuandoEstadoNulo() {
        assertThatThrownBy(() -> new Endereco(LOGRADOURO, NUMERO, COMPLEMENTO, BAIRRO, CEP, CIDADE, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("estado não pode ser nulo");
    }

    @Test
    @DisplayName("Deve aceitar numero e complemento nulos (campos opcionais)")
    void deveAceitarNumeroEComplementoNulos() {
        Endereco endereco = new Endereco(LOGRADOURO, null, null, BAIRRO, CEP, CIDADE, ESTADO);

        assertThat(endereco.getNumero()).isNull();
        assertThat(endereco.getComplemento()).isNull();
        assertThat(endereco.getLogradouro()).isEqualTo(LOGRADOURO);
    }
}