package com.decodex.br.domain.model;

import static com.decodex.br.domain.validations.LancamentoValidation.validarCategoria;
import static com.decodex.br.domain.validations.LancamentoValidation.validarDataVencimento;
import static com.decodex.br.domain.validations.LancamentoValidation.validarDescricao;
import static com.decodex.br.domain.validations.LancamentoValidation.validarPessoa;
import static com.decodex.br.domain.validations.LancamentoValidation.validarStatus;
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

    private StatusLancamento status;

    private BigDecimal valorPago;

    private Integer numeroParcela;

    private Integer totalParcelas;

    public Lancamento(UUID id, String descricao, LocalDate dataVencimento, LocalDate dataPagamento, BigDecimal valor,
            String observacao, TipoLancamento tipo, Categoria categoria, Pessoa pessoa) {
        this(id, descricao, dataVencimento, dataPagamento, valor, observacao, tipo, categoria, pessoa,
             (dataPagamento != null && tipo != null) ? tipo.getStatusQuitado() : StatusLancamento.PENDENTE,
             (dataPagamento != null) ? valor : null, 1, 1);
    }

    @Default
    public Lancamento(UUID id, String descricao, LocalDate dataVencimento, LocalDate dataPagamento, BigDecimal valor,
            String observacao, TipoLancamento tipo, Categoria categoria, Pessoa pessoa, StatusLancamento status,
            BigDecimal valorPago, Integer numeroParcela, Integer totalParcelas) {
        this.id = id;
        this.descricao = validarDescricao(descricao);
        this.dataVencimento = validarDataVencimento(dataVencimento);
        this.dataPagamento = dataPagamento;
        this.valor = validarValor(valor);
        this.observacao = observacao;
        this.tipo = validarTipo(tipo);
        this.categoria = validarCategoria(categoria);
        this.pessoa = validarPessoa(pessoa);
        StatusLancamento statusInicial = status != null ? status : ((dataPagamento != null) ? this.tipo.getStatusQuitado() : StatusLancamento.PENDENTE);
        this.status = validarStatus(statusInicial, this.tipo);
        this.valorPago = valorPago;
        this.numeroParcela = numeroParcela != null ? numeroParcela : 1;
        this.totalParcelas = totalParcelas != null ? totalParcelas : 1;
    }

    public Lancamento(String descricao, LocalDate dataVencimento, LocalDate dataPagamento, BigDecimal valor,
            String observacao, TipoLancamento tipo, Categoria categoria, Pessoa pessoa) {
        this(null, descricao, dataVencimento, dataPagamento, valor, observacao, tipo, categoria, pessoa);
    }

    public Lancamento(String descricao, LocalDate dataVencimento, LocalDate dataPagamento, BigDecimal valor,
            String observacao, TipoLancamento tipo, Categoria categoria, Pessoa pessoa, StatusLancamento status,
            BigDecimal valorPago, Integer numeroParcela, Integer totalParcelas) {
        this(null, descricao, dataVencimento, dataPagamento, valor, observacao, tipo, categoria, pessoa, status, valorPago, numeroParcela, totalParcelas);
    }

    public void alterarDescricao(String novaDescricao) {
        this.descricao = validarDescricao(novaDescricao);
    }

    public void alterarDataVencimento(LocalDate novaDataVencimento) {
        this.dataVencimento = validarDataVencimento(novaDataVencimento);
    }

    public void alterarDataPagamento(LocalDate novaDataPagamento) {
        this.dataPagamento = novaDataPagamento;
        if (novaDataPagamento != null && this.status.isPendente()) {
            this.status = this.tipo.getStatusQuitado();
            if (this.valorPago == null) {
                this.valorPago = this.valor;
            }
        } else if (novaDataPagamento == null && this.status.isQuitado()) {
            this.status = StatusLancamento.PENDENTE;
            this.valorPago = null;
        }
    }

    public void alterarValor(BigDecimal novoValor) {
        this.valor = validarValor(novoValor);
    }

    public void alterarObservacao(String novaObservacao) {
        this.observacao = novaObservacao;
    }

    public void alterarTipo(TipoLancamento novoTipo) {
        this.tipo = validarTipo(novoTipo);
        if (this.status != null && this.status.isQuitado()) {
            this.status = this.tipo.getStatusQuitado();
        }
    }

    public void alterarCategoria(Categoria novaCategoria) {
        this.categoria = validarCategoria(novaCategoria);
    }

    public void alterarPessoa(Pessoa novaPessoa) {
        this.pessoa = validarPessoa(novaPessoa);
    }

    public void quitar(LocalDate dataPagamento, BigDecimal valorPago) {
        this.dataPagamento = dataPagamento != null ? dataPagamento : LocalDate.now();
        this.valorPago = valorPago != null ? validarValor(valorPago) : this.valor;
        this.status = this.tipo.getStatusQuitado();
    }

    public void cancelar() {
        this.status = StatusLancamento.CANCELADO;
    }

    public void reabrir() {
        this.status = StatusLancamento.PENDENTE;
        this.dataPagamento = null;
        this.valorPago = null;
    }

    public void alterarStatus(StatusLancamento novoStatus) {
        this.status = validarStatus(novoStatus != null ? novoStatus : StatusLancamento.PENDENTE, this.tipo);
    }

    public void alterarValorPago(BigDecimal novoValorPago) {
        this.valorPago = novoValorPago;
    }

    public void alterarNumeroParcela(Integer novoNumeroParcela) {
        this.numeroParcela = novoNumeroParcela != null ? novoNumeroParcela : 1;
    }

    public void alterarTotalParcelas(Integer novoTotalParcelas) {
        this.totalParcelas = novoTotalParcelas != null ? novoTotalParcelas : 1;
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

        if (novaDataPagamento != null && this.status.isPendente()) {
            this.status = tipoValidado.getStatusQuitado();
            if (this.valorPago == null) {
                this.valorPago = valorValidado;
            }
        } else if (novaDataPagamento == null && this.status.isQuitado()) {
            this.status = StatusLancamento.PENDENTE;
            this.valorPago = null;
        } else if (this.status.isQuitado()) {
            this.status = tipoValidado.getStatusQuitado();
        }
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
            if (novosDados.status != null) {
                this.status = validarStatus(novosDados.status, this.tipo);
            }
            if (novosDados.valorPago != null) {
                this.valorPago = novosDados.valorPago;
            }
            if (novosDados.numeroParcela != null) {
                this.numeroParcela = novosDados.numeroParcela;
            }
            if (novosDados.totalParcelas != null) {
                this.totalParcelas = novosDados.totalParcelas;
            }
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

    public StatusLancamento getStatus() {
        return status;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public Integer getNumeroParcela() {
        return numeroParcela;
    }

    public Integer getTotalParcelas() {
        return totalParcelas;
    }

    public boolean isQuitado() {
        return this.status != null && this.status.isQuitado();
    }

    public boolean isPendente() {
        return this.status != null && this.status.isPendente();
    }

    public boolean isCancelado() {
        return this.status != null && this.status.isCancelado();
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
