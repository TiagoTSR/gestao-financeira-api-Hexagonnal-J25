package com.decodex.br.adapters.out.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.decodex.br.domain.model.TipoLancamento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "lancamento")
public class LancamentoEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
    @Column(length = 80)
    private String descricao;
	
	@NotNull
    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;
	
	@Column(name = "data_pagamento")
    private LocalDate dataPagamento;
	
	@NotNull
    @Column(precision = 15, scale = 2)
    private BigDecimal valor;
	
	@Column(length = 255)
    private String observacao;
	
	@NotNull
    @Enumerated(EnumType.STRING)
    private TipoLancamento tipo;
    
	@ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaEntity categoria;
    
    @ManyToOne
    @JoinColumn(name = "pessoa_id", nullable = false)
    private PessoaEntity pessoa;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public LocalDate getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(LocalDate dataPagamento) {
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

	public CategoriaEntity getCategoria() {
		return categoria;
	}

	public void setCategoria(CategoriaEntity categoria) {
		this.categoria = categoria;
	}

	public PessoaEntity getPessoa() {
		return pessoa;
	}

	public void setPessoa(PessoaEntity pessoa) {
		this.pessoa = pessoa;
	}

	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;

	    if (!(o instanceof LancamentoEntity)) return false;

	    LancamentoEntity other = (LancamentoEntity) o;

	    return id != null && id.equals(other.id);
	}

	@Override
	public int hashCode() {
	    return getClass().hashCode();
	}
}
