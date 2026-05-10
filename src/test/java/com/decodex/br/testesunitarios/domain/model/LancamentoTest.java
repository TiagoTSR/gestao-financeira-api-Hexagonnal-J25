package com.decodex.br.testesunitarios.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.model.TipoLancamento;

@DisplayName("Testes unitários para Lancamento")
class LancamentoTest {

    private static final Long ID = 1L;
    private static final String DESCRICAO = "Compra no supermercado";
    private static final LocalDate DATA_VENCIMENTO = LocalDate.of(2025, 5, 10);
    private static final LocalDate DATA_PAGAMENTO = LocalDate.of(2025, 5, 5);
    private static final BigDecimal VALOR = new BigDecimal("150.75");
    private static final String OBSERVACAO = "Pagamento com desconto";
    private static final TipoLancamento TIPO = TipoLancamento.DESPESA;
    private static final Categoria CATEGORIA = new Categoria(1L, "Alimentação");
    private static final Pessoa PESSOA = new Pessoa(1L, "João Silva", null, true);

    @Test
    @DisplayName("Deve criar lançamento válido com todos os campos")
    void deveCriarLancamentoValido() {
        Lancamento lancamento = new Lancamento(ID, DESCRICAO, DATA_VENCIMENTO, DATA_PAGAMENTO,
                VALOR, OBSERVACAO, TIPO, CATEGORIA, PESSOA);

        assertThat(lancamento.getId()).isEqualTo(ID);
        assertThat(lancamento.getDescricao()).isEqualTo(DESCRICAO);
        assertThat(lancamento.getDataVencimento()).isEqualTo(DATA_VENCIMENTO);
        assertThat(lancamento.getDataPagamento()).isEqualTo(DATA_PAGAMENTO);
        assertThat(lancamento.getValor()).isEqualTo(VALOR);
        assertThat(lancamento.getObservacao()).isEqualTo(OBSERVACAO);
        assertThat(lancamento.getTipo()).isEqualTo(TIPO);
        assertThat(lancamento.getCategoria()).isEqualTo(CATEGORIA);
        assertThat(lancamento.getPessoa()).isEqualTo(PESSOA);
    }

    @Test
    @DisplayName("Deve lançar exceção quando descrição for nula")
    void deveLancarExcecaoQuandoDescricaoNula() {
        assertThatThrownBy(() -> new Lancamento(ID, null, DATA_VENCIMENTO, DATA_PAGAMENTO,
                VALOR, OBSERVACAO, TIPO, CATEGORIA, PESSOA))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Descrição não pode ser nula ou vazia");
    }

    @Test
    @DisplayName("Deve lançar exceção quando descrição for vazia")
    void deveLancarExcecaoQuandoDescricaoVazia() {
        assertThatThrownBy(() -> new Lancamento(ID, "   ", DATA_VENCIMENTO, DATA_PAGAMENTO,
                VALOR, OBSERVACAO, TIPO, CATEGORIA, PESSOA))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Descrição não pode ser nula ou vazia");
    }

    @Test
    @DisplayName("Deve lançar exceção quando data de vencimento for nula")
    void deveLancarExcecaoQuandoDataVencimentoNula() {
        assertThatThrownBy(() -> new Lancamento(ID, DESCRICAO, null, DATA_PAGAMENTO,
                VALOR, OBSERVACAO, TIPO, CATEGORIA, PESSOA))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Data de vencimento não pode ser nula");
    }
    
    @Test
    @DisplayName("Deve lançar exceção quando tipo for nulo")
    void deveLancarExcecaoQuandoTipoNulo() {
        assertThatThrownBy(() -> new Lancamento(ID, DESCRICAO, DATA_VENCIMENTO, DATA_PAGAMENTO,
                VALOR, OBSERVACAO, null, CATEGORIA, PESSOA))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tipo não pode ser nulo");
    }

    @Test
    @DisplayName("Deve lançar exceção quando categoria for nula")
    void deveLancarExcecaoQuandoCategoriaNula() {
        assertThatThrownBy(() -> new Lancamento(ID, DESCRICAO, DATA_VENCIMENTO, DATA_PAGAMENTO,
                VALOR, OBSERVACAO, TIPO, null, PESSOA))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Categoria não pode ser nula");
    }

    @Test
    @DisplayName("Deve lançar exceção quando pessoa for nula")
    void deveLancarExcecaoQuandoPessoaNula() {
        assertThatThrownBy(() -> new Lancamento(ID, DESCRICAO, DATA_VENCIMENTO, DATA_PAGAMENTO,
                VALOR, OBSERVACAO, TIPO, CATEGORIA, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Pessoa não pode ser nula");
    }

    @Test
    @DisplayName("Deve aceitar dataPagamento e observacao nulos (opcionais)")
    void deveAceitarDataPagamentoEObservacaoNulos() {
        Lancamento lancamento = new Lancamento(ID, DESCRICAO, DATA_VENCIMENTO, null,
                VALOR, null, TIPO, CATEGORIA, PESSOA);

        assertThat(lancamento.getDataPagamento()).isNull();
        assertThat(lancamento.getObservacao()).isNull();
    }
    
    @Test
    @DisplayName("Deve considerar dois lançamentos iguais quando possuírem o mesmo ID")
    void testEqualsAndHashCode() {
        // Lançamentos com o mesmo ID
        Lancamento lancamento1 = new Lancamento(ID, DESCRICAO, DATA_VENCIMENTO, DATA_PAGAMENTO, VALOR, OBSERVACAO, TIPO, CATEGORIA, PESSOA);
        Lancamento lancamento2 = new Lancamento(ID, "Outra Descrição", LocalDate.now(), null, BigDecimal.TEN, null, TipoLancamento.RECEITA, CATEGORIA, PESSOA);

        assertThat(lancamento1).isEqualTo(lancamento2);
        assertThat(lancamento1.hashCode()).isEqualTo(lancamento2.hashCode());
    }

    @Test
    @DisplayName("Deve considerar lançamentos diferentes quando IDs mudarem ou comparados com null/outros tipos")
    void testEqualsDiferentes() {
        Lancamento lancamento1 = new Lancamento(ID, DESCRICAO, DATA_VENCIMENTO, DATA_PAGAMENTO, VALOR, OBSERVACAO, TIPO, CATEGORIA, PESSOA);
        Lancamento lancamento2 = new Lancamento(2L, DESCRICAO, DATA_VENCIMENTO, DATA_PAGAMENTO, VALOR, OBSERVACAO, TIPO, CATEGORIA, PESSOA); // ID diferente

        assertThat(lancamento1).isNotEqualTo(lancamento2);
        assertThat(lancamento1).isNotEqualTo(null);
        assertThat(lancamento1).isNotEqualTo(new Object());
    }
}