package com.decodex.br.testesunitarios.domain.validations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.model.TipoLancamento;
import com.decodex.br.domain.validations.LancamentoValidation;

@DisplayName("Testes unitários para LancamentoValidation")
class LancamentoValidationTest {

    private LancamentoValidation validation;

    @BeforeEach
    void setUp() {
        validation = new LancamentoValidation();
    }

    @Test
    @DisplayName("Deve validar descrição com sucesso")
    void deveValidarDescricaoComSucesso() {
        String descricao = validation.validarDescricao("Pagamento de conta");
        assertThat(descricao).isEqualTo("Pagamento de conta");
    }

    @Test
    @DisplayName("Deve lançar exceção quando descrição for nula ou em branco")
    void deveLancarExcecaoQuandoDescricaoInvalida() {
        assertThatThrownBy(() -> validation.validarDescricao(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Descrição não pode ser nula ou vazia");

        assertThatThrownBy(() -> validation.validarDescricao("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Descrição não pode ser nula ou vazia");
    }

    @Test
    @DisplayName("Deve validar data de vencimento com sucesso")
    void deveValidarDataVencimentoComSucesso() {
        LocalDate data = LocalDate.now();
        LocalDate resultado = validation.validarDataVencimento(data);
        assertThat(resultado).isEqualTo(data);
    }

    @Test
    @DisplayName("Deve lançar exceção quando data de vencimento for nula")
    void deveLancarExcecaoQuandoDataVencimentoNula() {
        assertThatThrownBy(() -> validation.validarDataVencimento(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Data de vencimento não pode ser nula");
    }

    @Test
    @DisplayName("Deve validar valor com sucesso")
    void deveValidarValorComSucesso() {
        BigDecimal valor = new BigDecimal("100.00");
        BigDecimal resultado = validation.validarValor(valor);
        assertThat(resultado).isEqualTo(valor);
    }

    @Test
    @DisplayName("Deve lançar exceção quando valor for nulo")
    void deveLancarExcecaoQuandoValorNulo() {
        assertThatThrownBy(() -> validation.validarValor(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Valor não pode ser nulo");
    }

    @Test
    @DisplayName("Deve validar tipo com sucesso")
    void deveValidarTipoComSucesso() {
        TipoLancamento tipo = validation.validarTipo(TipoLancamento.RECEITA);
        assertThat(tipo).isEqualTo(TipoLancamento.RECEITA);
    }

    @Test
    @DisplayName("Deve lançar exceção quando tipo for nulo")
    void deveLancarExcecaoQuandoTipoNulo() {
        assertThatThrownBy(() -> validation.validarTipo(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tipo não pode ser nulo");
    }

    @Test
    @DisplayName("Deve validar categoria com sucesso")
    void deveValidarCategoriaComSucesso() {
        Categoria categoria = new Categoria(1L, "Alimentação");
        Categoria resultado = validation.validarCategoria(categoria);
        assertThat(resultado).isEqualTo(categoria);
    }

    @Test
    @DisplayName("Deve lançar exceção quando categoria for nula")
    void deveLancarExcecaoQuandoCategoriaNula() {
        assertThatThrownBy(() -> validation.validarCategoria(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Categoria não pode ser nula");
    }

    @Test
    @DisplayName("Deve validar pessoa com sucesso")
    void deveValidarPessoaComSucesso() {
        Endereco endereco = new Endereco("Rua 1", "10", null, "Bairro", "12345-000", "Cidade", "SP");
        Pessoa pessoa = new Pessoa(1L, "José", endereco, true);
        Pessoa resultado = validation.validarPessoa(pessoa);
        assertThat(resultado).isEqualTo(pessoa);
    }

    @Test
    @DisplayName("Deve lançar exceção quando pessoa for nula")
    void deveLancarExcecaoQuandoPessoaNula() {
        assertThatThrownBy(() -> validation.validarPessoa(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Pessoa não pode ser nula");
    }
}
