package com.decodex.br.application.dto.lancamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.decodex.br.domain.model.TipoLancamento;

public class LancamentoFilter {
	
	private String descricao;
    private LocalDate dataVencimento;
    private LocalDate dataVencimentoDe;
    private LocalDate dataVencimentoAte;
    private LocalDate dataPagamento;
    private BigDecimal valor;
    private String observacao;
    private TipoLancamento tipo;
    private String nomeCategoria;
    private String nomePessoa;
    private UUID categoriaId;
    private UUID pessoaId;
    
    public LancamentoFilter() {
    }

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public LocalDate getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(LocalDate dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public LocalDate getDataVencimentoDe() {
		return dataVencimentoDe;
	}

	public void setDataVencimentoDe(LocalDate dataVencimentoDe) {
		this.dataVencimentoDe = dataVencimentoDe;
	}

	public void setData_vencimento_de(LocalDate dataVencimentoDe) {
		this.dataVencimentoDe = dataVencimentoDe;
	}

	public LocalDate getDataVencimentoAte() {
		return dataVencimentoAte;
	}

	public void setDataVencimentoAte(LocalDate dataVencimentoAte) {
		this.dataVencimentoAte = dataVencimentoAte;
	}

	public void setData_vencimento_ate(LocalDate dataVencimentoAte) {
		this.dataVencimentoAte = dataVencimentoAte;
	}

	public LocalDate getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(LocalDate dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public void setData_pagamento(LocalDate dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public TipoLancamento getTipo() {
		return tipo;
	}

	public void setTipo(TipoLancamento tipo) {
		this.tipo = tipo;
	}

	public String getNomeCategoria() {
		return nomeCategoria;
	}

	public void setNomeCategoria(String nomeCategoria) {
		this.nomeCategoria = nomeCategoria;
	}

	public String getNomePessoa() {
		return nomePessoa;
	}

	public void setNomePessoa(String nomePessoa) {
		this.nomePessoa = nomePessoa;
	}

	public UUID getCategoriaId() {
		return categoriaId;
	}

	public void setCategoriaId(UUID categoriaId) {
		this.categoriaId = categoriaId;
	}

	public void setCategoria_id(UUID categoriaId) {
		this.categoriaId = categoriaId;
	}

	public UUID getPessoaId() {
		return pessoaId;
	}

	public void setPessoaId(UUID pessoaId) {
		this.pessoaId = pessoaId;
	}

	public void setPessoa_id(UUID pessoaId) {
		this.pessoaId = pessoaId;
	}
}
