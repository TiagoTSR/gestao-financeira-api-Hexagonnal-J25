package com.decodex.br.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

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

	public Lancamento(Long id, String descricao, LocalDate dataVencimento, LocalDate dataPagamento, BigDecimal valor,
			String observacao, TipoLancamento tipo, Categoria categoria, Pessoa pessoa) {
		super();
		this.id = id;
		this.descricao = validarDescricao(descricao);
		this.dataVencimento = validarDataVencimento(dataVencimento);
		this.dataPagamento = dataPagamento;
		this.valor = valor;
		this.observacao = observacao;
		this.tipo = tipo;
		this.categoria = validarCategoria(categoria);
		this.pessoa = validarPessoa(pessoa);
	}
	
	private String validarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição não pode ser nula ou vazia");
        }
        return descricao;
    }

    private LocalDate validarDataVencimento(LocalDate dataVencimento) {
        if (dataVencimento == null) {
            throw new IllegalArgumentException("Data de vencimento não pode ser nula");
        }
        return dataVencimento;
    }

    private Categoria validarCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria não pode ser nula");
        }
        return categoria;
    }
    
    private Pessoa validarPessoa(Pessoa pessoa) {
        if (pessoa == null) {
            throw new IllegalArgumentException("Pessoa não pode ser nula");
        }
        return pessoa;
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
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    
	    Lancamento lancamento = (Lancamento) o;
	    
	    return id != null && id.equals(lancamento.id);
	}

	@Override
	public int hashCode() {
	    return getClass().hashCode();
	}
    
}
