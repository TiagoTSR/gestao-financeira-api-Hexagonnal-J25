package com.decodex.br.domain.model;

public class Pessoa {

	private Long id;
	
    private String nome;

    private Endereco endereco;

    private Boolean ativo;

	public Pessoa(Long id, String nome, Endereco endereco, Boolean ativo) {
		super();
		this.id = id;
		this.nome = validarNomePessoa(nome, "Nome");
		this.endereco = endereco;
		this.ativo = validarAtivo(ativo);
	}
	
	private String validarNomePessoa(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " não pode ser vazio");
        }
        return valor;
    }
	
	private Boolean validarAtivo(Boolean ativo) {
        if (ativo == null) {
            throw new IllegalArgumentException("Ativo não pode ser vazio (deve ser true ou false)");
        }
        return ativo;
    }

	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public Boolean getAtivo() {
		return ativo;
	}
    
 }