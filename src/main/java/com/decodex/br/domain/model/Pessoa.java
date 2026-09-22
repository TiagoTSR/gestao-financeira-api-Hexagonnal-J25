package com.decodex.br.domain.model;

import com.decodex.br.domain.validations.PessoaValidation;

public class Pessoa {

	private Long id;

	private String nome;

	private Endereco endereco;

	private Boolean ativo;

	private final PessoaValidation validation = new PessoaValidation();

	public Pessoa(Long id, String nome, Endereco endereco, Boolean ativo) {
		this.id = id;
		this.nome = validation.validarNome(nome, "Nome");
		this.endereco = validation.validarEndereco(endereco);
		this.ativo = validation.validarAtivo(ativo);
	}

	public Pessoa(String nome, Endereco endereco, Boolean ativo) {
		this(null, nome, endereco, ativo);
	}

	public Pessoa(Long id, String nome) {
		this.id = validation.validarId(id);
		this.nome = validation.validarNome(nome, "Nome");
		this.endereco = null;
		this.ativo = null;
	}

	public void alterarNome(String novoNome) {
		this.nome = validation.validarNome(novoNome, "Nome");
	}

	public void alterarEndereço(Endereco novoEndereço) {
		this.endereco = validation.validarEndereco(novoEndereço);
	}

	public void alterarAtivo(Boolean alteracaoAtivo) {
		this.ativo = validation.validarAtivo(alteracaoAtivo);
	}

	public void atualizarCampos(Pessoa novosDados) {
		alterarNome(novosDados.nome);
		alterarEndereço(novosDados.endereco);
		alterarAtivo(novosDados.ativo);
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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Pessoa pessoa = (Pessoa) o;

		return id != null && id.equals(pessoa.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

}