package com.decodex.br.testesunitarios.domain.validations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.domain.exeption.RegraDeNegocioException;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.model.TipoLancamento;
import com.decodex.br.domain.validations.LancamentoValidation;

@DisplayName("Testes unitários para LancamentoValidation")
class LancamentoValidationTest {

    @Test
    @DisplayName("Deve validar descrição com sucesso")
    void deveValidarDescricaoComSucesso() {
        String descricao = LancamentoValidation.validarDescricao("Pagamento de conta");
        assertThat(descricao).isEqualTo("Pagamento de conta");
    }

    @Test
    @DisplayName("Deve lançar exceção quando descrição for nula ou em branco")
    void deveLancarExcecaoQuandoDescricaoInvalida() {
        assertThatThrownBy(() -> LancamentoValidation.validarDescricao(null))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Descrição não pode ser nula ou vazia");

        assertThatThrownBy(() -> LancamentoValidation.validarDescricao("   "))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Descrição não pode ser nula ou vazia");
    }

    @Test
    @DisplayName("Deve validar data de vencimento com sucesso")
    void deveValidarDataVencimentoComSucesso() {
        LocalDate data = LocalDate.now();
        LocalDate resultado = LancamentoValidation.validarDataVencimento(data);
        assertThat(resultado).isEqualTo(data);
    }

    @Test
    @DisplayName("Deve lançar exceção quando data de vencimento for nula")
    void deveLancarExcecaoQuandoDataVencimentoNula() {
        assertThatThrownBy(() -> LancamentoValidation.validarDataVencimento(null))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Data de vencimento não pode ser nula");
    }

    @Test
    @DisplayName("Deve validar valor com sucesso")
    void deveValidarValorComSucesso() {
        BigDecimal valor = new BigDecimal("100.00");
        BigDecimal resultado = LancamentoValidation.validarValor(valor);
        assertThat(resultado).isEqualTo(valor);
    }

    @Test
    @DisplayName("Deve lançar exceção quando valor for nulo")
    void deveLancarExcecaoQuandoValorNulo() {
        assertThatThrownBy(() -> LancamentoValidation.validarValor(null))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Valor não pode ser nulo");
    }

    @Test
    @DisplayName("Deve validar tipo com sucesso")
    void deveValidarTipoComSucesso() {
        TipoLancamento tipo = LancamentoValidation.validarTipo(TipoLancamento.RECEITA);
        assertThat(tipo).isEqualTo(TipoLancamento.RECEITA);
    }

    @Test
    @DisplayName("Deve lançar exceção quando tipo for nulo")
    void deveLancarExcecaoQuandoTipoNulo() {
        assertThatThrownBy(() -> LancamentoValidation.validarTipo(null))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Tipo não pode ser nulo");
    }

    @Test
    @DisplayName("Deve validar categoria com sucesso")
    void deveValidarCategoriaComSucesso() {
        Categoria categoria = new Categoria(1L, "Alimentação");
        Categoria resultado = LancamentoValidation.validarCategoria(categoria);
        assertThat(resultado).isEqualTo(categoria);
    }

    @Test
    @DisplayName("Deve lançar exceção quando categoria for nula")
    void deveLancarExcecaoQuandoCategoriaNula() {
        assertThatThrownBy(() -> LancamentoValidation.validarCategoria(null))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Categoria não pode ser nula");
    }

    @Test
    @DisplayName("Deve validar pessoa com sucesso")
    void deveValidarPessoaComSucesso() {
        Endereco endereco = new Endereco("Rua 1", "10", null, "Bairro", "12345-000", "Cidade", "SP");
        Pessoa pessoa = new Pessoa(1L, "José", endereco, true);
        Pessoa resultado = LancamentoValidation.validarPessoa(pessoa);
        assertThat(resultado).isEqualTo(pessoa);
    }

    @Test
    @DisplayName("Deve lançar exceção quando pessoa for nula")
    void deveLancarExcecaoQuandoPessoaNula() {
        assertThatThrownBy(() -> LancamentoValidation.validarPessoa(null))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Pessoa não pode ser nula");
    }
}
