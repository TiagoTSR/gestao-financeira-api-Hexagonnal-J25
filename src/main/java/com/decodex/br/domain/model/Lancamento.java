package com.decodex.br.domain.model;

import static com.decodex.br.domain.validations.LancamentoValidation.validarCategoria;
import static com.decodex.br.domain.validations.LancamentoValidation.validarDataVencimento;
import static com.decodex.br.domain.validations.LancamentoValidation.validarDescricao;
import static com.decodex.br.domain.validations.LancamentoValidation.validarPessoa;
import static com.decodex.br.domain.validations.LancamentoValidation.validarTipo;
import static com.decodex.br.domain.validations.LancamentoValidation.validarValor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.decodex.br.domain.annotation.Default;

public class Lancamento {

    private UUID id;

    private String descricao;

    private LocalDate dataVencimento;

    private LocalDate dataPagamento;

    private BigDecimal valor;

    private String observacao;

    private TipoLancamento tipo;

    private Categoria categoria;

    private Pessoa pessoa;

    @Default
    public Lancamento(UUID id, String descricao, LocalDate dataVencimento, LocalDate dataPagamento, BigDecimal valor,
            String observacao, TipoLancamento tipo, Categoria categoria, Pessoa pessoa) {
        this.id = id;
        this.descricao = validarDescricao(descricao);
        this.dataVencimento = validarDataVencimento(dataVencimento);
        this.dataPagamento = dataPagamento;
        this.valor = validarValor(valor);
        this.observacao = observacao;
        this.tipo = validarTipo(tipo);
        this.categoria = validarCategoria(categoria);
        this.pessoa = validarPessoa(pessoa);
    }

    public Lancamento(String descricao, LocalDate dataVencimento, LocalDate dataPagamento, BigDecimal valor,
            String observacao, TipoLancamento tipo, Categoria categoria, Pessoa pessoa) {
        this(null, descricao, dataVencimento, dataPagamento, valor, observacao, tipo, categoria, pessoa);
    }

    public void alterarDescricao(String novaDescricao) {
        this.descricao = validarDescricao(novaDescricao);
    }

    public void alterarDataVencimento(LocalDate novaDataVencimento) {
        this.dataVencimento = validarDataVencimento(novaDataVencimento);
    }

    public void alterarDataPagamento(LocalDate novaDataPagamento) {
        this.dataPagamento = novaDataPagamento;
    }

    public void alterarValor(BigDecimal novoValor) {
        this.valor = validarValor(novoValor);
    }

    public void alterarObservacao(String novaObservacao) {
        this.observacao = novaObservacao;
    }

    public void alterarTipo(TipoLancamento novoTipo) {
        this.tipo = validarTipo(novoTipo);
    }

    public void alterarCategoria(Categoria novaCategoria) {
        this.categoria = validarCategoria(novaCategoria);
    }

    public void alterarPessoa(Pessoa novaPessoa) {
        this.pessoa = validarPessoa(novaPessoa);
    }

    public void atualizar(String novaDescricao, LocalDate novaDataVencimento, LocalDate novaDataPagamento,
                          BigDecimal novoValor, String novaObservacao, TipoLancamento novoTipo,
                          Categoria novaCategoria, Pessoa novaPessoa) {
        String descricaoValidada = validarDescricao(novaDescricao);
        LocalDate vencimentoValidado = validarDataVencimento(novaDataVencimento);
        BigDecimal valorValidado = validarValor(novoValor);
        TipoLancamento tipoValidado = validarTipo(novoTipo);
        Categoria categoriaValidada = validarCategoria(novaCategoria);
        Pessoa pessoaValidada = validarPessoa(novaPessoa);

        this.descricao = descricaoValidada;
        this.dataVencimento = vencimentoValidado;
        this.dataPagamento = novaDataPagamento;
        this.valor = valorValidado;
        this.observacao = novaObservacao;
        this.tipo = tipoValidado;
        this.categoria = categoriaValidada;
        this.pessoa = pessoaValidada;
    }

    public void atualizarCampos(Lancamento novosDados) {
        if (novosDados != null) {
            atualizar(
                novosDados.descricao,
                novosDados.dataVencimento,
                novosDados.dataPagamento,
                novosDados.valor,
                novosDados.observacao,
                novosDados.tipo,
                novosDados.categoria,
                novosDados.pessoa
            );
        }
    }

    public UUID getId() {
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
