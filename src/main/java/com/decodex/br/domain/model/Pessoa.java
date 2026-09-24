package com.decodex.br.domain.model;

import static com.decodex.br.domain.validations.PessoaValidation.validarAtivo;
import static com.decodex.br.domain.validations.PessoaValidation.validarEndereco;
import static com.decodex.br.domain.validations.PessoaValidation.validarId;
import static com.decodex.br.domain.validations.PessoaValidation.validarNome;

public class Pessoa {

	private Long id;

	private String nome;

	private Endereco endereco;

	private Boolean ativo;

	public Pessoa(Long id, String nome, Endereco endereco, Boolean ativo) {
		this.id = id;
		this.nome = validarNome(nome, "Nome");
		this.endereco = validarEndereco(endereco);
		this.ativo = validarAtivo(ativo);
	}

	public Pessoa(String nome, Endereco endereco, Boolean ativo) {
		this(null, nome, endereco, ativo);
	}

	public Pessoa(Long id, String nome) {
		this.id = validarId(id);
		this.nome = validarNome(nome, "Nome");
		this.endereco = null;
		this.ativo = null;
	}

	public void alterarNome(String novoNome) {
		this.nome = validarNome(novoNome, "Nome");
	}

	public void alterarEndereço(Endereco novoEndereço) {
		this.endereco = validarEndereco(novoEndereço);
	}

	public void alterarAtivo(Boolean alteracaoAtivo) {
		this.ativo = validarAtivo(alteracaoAtivo);
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