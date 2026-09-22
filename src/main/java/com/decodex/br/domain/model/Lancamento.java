package com.decodex.br.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.decodex.br.domain.validations.LancamentoValidation;

public class Lancamento {

    private Long id;

    private String descricao;

    private LocalDate dataVencimento;

    private LocalDate dataPagamento;

    private BigDecimal valor;

    private String observacao;

    private TipoLancamento tipo;

    private Categoria categoria;

    private Pessoa pessoa;

    private final LancamentoValidation validation = new LancamentoValidation();

    public Lancamento(Long id, String descricao, LocalDate dataVencimento, LocalDate dataPagamento, BigDecimal valor,
            String observacao, TipoLancamento tipo, Categoria categoria, Pessoa pessoa) {
        this.id = id;
        this.descricao = validation.validarDescricao(descricao);
        this.dataVencimento = validation.validarDataVencimento(dataVencimento);
        this.dataPagamento = dataPagamento;
        this.valor = validation.validarValor(valor);
        this.observacao = observacao;
        this.tipo = validation.validarTipo(tipo);
        this.categoria = validation.validarCategoria(categoria);
        this.pessoa = validation.validarPessoa(pessoa);
    }

    public Lancamento(String descricao, LocalDate dataVencimento, LocalDate dataPagamento, BigDecimal valor,
            String observacao, TipoLancamento tipo, Categoria categoria, Pessoa pessoa) {
        this(null, descricao, dataVencimento, dataPagamento, valor, observacao, tipo, categoria, pessoa);
    }

    public void alterarDescricao(String novaDescricao) {
        this.descricao = validation.validarDescricao(novaDescricao);
    }

    public void alterarDataVencimento(LocalDate novaDataVencimento) {
        this.dataVencimento = validation.validarDataVencimento(novaDataVencimento);
    }

    public void alterarDataPagamento(LocalDate novaDataPagamento) {
        this.dataPagamento = novaDataPagamento;
    }

    public void alterarValor(BigDecimal novoValor) {
        this.valor = validation.validarValor(novoValor);
    }

    public void alterarObservacao(String novaObservacao) {
        this.observacao = novaObservacao;
    }

    public void alterarTipo(TipoLancamento novoTipo) {
        this.tipo = validation.validarTipo(novoTipo);
    }

    public void alterarCategoria(Categoria novaCategoria) {
        this.categoria = validation.validarCategoria(novaCategoria);
    }

    public void alterarPessoa(Pessoa novaPessoa) {
        this.pessoa = validation.validarPessoa(novaPessoa);
    }

    public void atualizarCampos(Lancamento novosDados) {
        alterarDescricao(novosDados.descricao);
        alterarDataVencimento(novosDados.dataVencimento);
        alterarDataPagamento(novosDados.dataPagamento);
        alterarValor(novosDados.valor);
        alterarObservacao(novosDados.observacao);
        alterarTipo(novosDados.tipo);
        alterarCategoria(novosDados.categoria);
        alterarPessoa(novosDados.pessoa);
    }

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getObservacao() {
        return observacao;
    }

    public TipoLancamento getTipo() {
        return tipo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Lancamento lancamento = (Lancamento) o;

        return id != null && id.equals(lancamento.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
