package com.decodex.br.testesunitarios.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.domain.exception.RegraDeNegocioException;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.model.TipoLancamento;

@DisplayName("Testes unitários para Lancamento")
class LancamentoTest {

    private static final java.util.UUID ID = java.util.UUID.randomUUID();
    private static final String DESCRICAO = "Compra no supermercado";
    private static final LocalDate DATA_VENCIMENTO = LocalDate.of(2025, 5, 10);
    private static final LocalDate DATA_PAGAMENTO = LocalDate.of(2025, 5, 5);
    private static final BigDecimal VALOR = new BigDecimal("150.75");
    private static final String OBSERVACAO = "Pagamento com desconto";
    private static final TipoLancamento TIPO = TipoLancamento.DESPESA;
    private static final Categoria CATEGORIA = new Categoria(java.util.UUID.randomUUID(), "Alimentação");
    private static final Endereco ENDERECO_PESSOA = new Endereco(
    	    "Rua A", "10", null, "Centro", "00000-000", "São Paulo", "SP"
    	);
    private static final Pessoa PESSOA = new Pessoa(java.util.UUID.randomUUID(), "João Silva", ENDERECO_PESSOA, true);

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
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Descrição não pode ser nula ou vazia");
    }

    @Test
    @DisplayName("Deve lançar exceção quando descrição for vazia")
    void deveLancarExcecaoQuandoDescricaoVazia() {
        assertThatThrownBy(() -> new Lancamento(ID, "   ", DATA_VENCIMENTO, DATA_PAGAMENTO,
                VALOR, OBSERVACAO, TIPO, CATEGORIA, PESSOA))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Descrição não pode ser nula ou vazia");
    }

    @Test
    @DisplayName("Deve lançar exceção quando data de vencimento for nula")
    void deveLancarExcecaoQuandoDataVencimentoNula() {
        assertThatThrownBy(() -> new Lancamento(ID, DESCRICAO, null, DATA_PAGAMENTO,
                VALOR, OBSERVACAO, TIPO, CATEGORIA, PESSOA))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Data de vencimento não pode ser nula");
    }
    
    @Test
    @DisplayName("Deve lançar exceção quando tipo for nulo")
    void deveLancarExcecaoQuandoTipoNulo() {
        assertThatThrownBy(() -> new Lancamento(ID, DESCRICAO, DATA_VENCIMENTO, DATA_PAGAMENTO,
                VALOR, OBSERVACAO, null, CATEGORIA, PESSOA))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Tipo não pode ser nulo");
    }

    @Test
    @DisplayName("Deve lançar exceção quando categoria for nula")
    void deveLancarExcecaoQuandoCategoriaNula() {
        assertThatThrownBy(() -> new Lancamento(ID, DESCRICAO, DATA_VENCIMENTO, DATA_PAGAMENTO,
                VALOR, OBSERVACAO, TIPO, null, PESSOA))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("Categoria não pode ser nula");
    }

    @Test
    @DisplayName("Deve lançar exceção quando pessoa for nula")
    void deveLancarExcecaoQuandoPessoaNula() {
        assertThatThrownBy(() -> new Lancamento(ID, DESCRICAO, DATA_VENCIMENTO, DATA_PAGAMENTO,
                VALOR, OBSERVACAO, TIPO, CATEGORIA, null))
                .isInstanceOf(RegraDeNegocioException.class)
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
        Lancamento lancamento2 = new Lancamento(java.util.UUID.randomUUID(), DESCRICAO, DATA_VENCIMENTO, DATA_PAGAMENTO, VALOR, OBSERVACAO, TIPO, CATEGORIA, PESSOA); // ID diferente

        assertThat(lancamento1).isNotEqualTo(lancamento2);
        assertThat(lancamento1).isNotEqualTo(null);
        assertThat(lancamento1).isNotEqualTo(new Object());
    }

    @Test
    @DisplayName("Deve atualizar lançamento atomicamente com sucesso")
    void deveAtualizarLancamentoAtomicamente() {
        Lancamento lancamento = new Lancamento(ID, DESCRICAO, DATA_VENCIMENTO, DATA_PAGAMENTO, VALOR, OBSERVACAO, TIPO, CATEGORIA, PESSOA);
        Categoria novaCategoria = new Categoria(java.util.UUID.randomUUID(), "Transporte");
        Pessoa novaPessoa = new Pessoa(java.util.UUID.randomUUID(), "Maria Silva", ENDERECO_PESSOA, true);
        LocalDate novoVencimento = LocalDate.of(2025, 8, 1);
        LocalDate novoPagamento = LocalDate.of(2025, 8, 2);
        BigDecimal novoValor = new BigDecimal("350.00");

        lancamento.atualizar("Nova Descrição", novoVencimento, novoPagamento, novoValor, "Nova Obs", TipoLancamento.RECEITA, novaCategoria, novaPessoa);

        assertThat(lancamento.getDescricao()).isEqualTo("Nova Descrição");
        assertThat(lancamento.getDataVencimento()).isEqualTo(novoVencimento);
        assertThat(lancamento.getDataPagamento()).isEqualTo(novoPagamento);
        assertThat(lancamento.getValor()).isEqualByComparingTo(novoValor);
        assertThat(lancamento.getObservacao()).isEqualTo("Nova Obs");
        assertThat(lancamento.getTipo()).isEqualTo(TipoLancamento.RECEITA);
        assertThat(lancamento.getCategoria()).isEqualTo(novaCategoria);
        assertThat(lancamento.getPessoa()).isEqualTo(novaPessoa);
    }

    @Test
    @DisplayName("Deve manter estado original quando atualizar falhar em algum campo posterior (atomicidade)")
    void deveManterEstadoOriginalQuandoValidacaoFalhar() {
        Lancamento lancamento = new Lancamento(ID, DESCRICAO, DATA_VENCIMENTO, DATA_PAGAMENTO, VALOR, OBSERVACAO, TIPO, CATEGORIA, PESSOA);

        // Descrição válida, mas valor nulo deve falhar e não modificar nada
        assertThatThrownBy(() -> lancamento.atualizar("Tentativa", DATA_VENCIMENTO, null, null, null, TIPO, CATEGORIA, PESSOA))
            .isInstanceOf(RegraDeNegocioException.class);

        assertThat(lancamento.getDescricao()).isEqualTo(DESCRICAO);
        assertThat(lancamento.getValor()).isEqualTo(VALOR);
    }

    @Test
    @DisplayName("Deve atualizar campos via atualizarCampos e ignorar nulo")
    void deveAtualizarCamposEIgnorarNulo() {
        Lancamento lancamento = new Lancamento(ID, DESCRICAO, DATA_VENCIMENTO, DATA_PAGAMENTO, VALOR, OBSERVACAO, TIPO, CATEGORIA, PESSOA);
        Categoria novaCategoria = new Categoria(java.util.UUID.randomUUID(), "Transporte");
        Pessoa novaPessoa = new Pessoa(java.util.UUID.randomUUID(), "Maria Silva", ENDERECO_PESSOA, true);
        Lancamento novosDados = new Lancamento("Nova Descrição", DATA_VENCIMENTO, null, new BigDecimal("200.00"), null, TipoLancamento.RECEITA, novaCategoria, novaPessoa);

        lancamento.atualizarCampos(novosDados);
        assertThat(lancamento.getDescricao()).isEqualTo("Nova Descrição");

        lancamento.atualizarCampos(null);
        assertThat(lancamento.getDescricao()).isEqualTo("Nova Descrição");
    }

    @Test
    @DisplayName("Deve permitir alterações pontuais via métodos alterar")
    void devePermitirAlteracoesPontuais() {
        Lancamento lancamento = new Lancamento(ID, DESCRICAO, DATA_VENCIMENTO, DATA_PAGAMENTO, VALOR, OBSERVACAO, TIPO, CATEGORIA, PESSOA);
        Categoria novaCategoria = new Categoria(java.util.UUID.randomUUID(), "Transporte");
        Pessoa novaPessoa = new Pessoa(java.util.UUID.randomUUID(), "Maria Silva", ENDERECO_PESSOA, true);
        LocalDate novoVencimento = LocalDate.of(2025, 9, 1);
        LocalDate novoPagamento = LocalDate.of(2025, 9, 2);

        lancamento.alterarDescricao("Novo Texto");
        lancamento.alterarDataVencimento(novoVencimento);
        lancamento.alterarDataPagamento(novoPagamento);
        lancamento.alterarValor(new BigDecimal("999.00"));
        lancamento.alterarObservacao("Nota");
        lancamento.alterarTipo(TipoLancamento.RECEITA);
        lancamento.alterarCategoria(novaCategoria);
        lancamento.alterarPessoa(novaPessoa);

        assertThat(lancamento.getDescricao()).isEqualTo("Novo Texto");
        assertThat(lancamento.getDataVencimento()).isEqualTo(novoVencimento);
        assertThat(lancamento.getDataPagamento()).isEqualTo(novoPagamento);
        assertThat(lancamento.getValor()).isEqualByComparingTo("999.00");
        assertThat(lancamento.getObservacao()).isEqualTo("Nota");
        assertThat(lancamento.getTipo()).isEqualTo(TipoLancamento.RECEITA);
        assertThat(lancamento.getCategoria()).isEqualTo(novaCategoria);
        assertThat(lancamento.getPessoa()).isEqualTo(novaPessoa);
    }
}