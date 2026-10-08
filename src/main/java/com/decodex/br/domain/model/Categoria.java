package com.decodex.br.domain.model;

import java.util.UUID;

import com.decodex.br.domain.annotation.Default;
import static com.decodex.br.domain.validations.CategoriaValidation.validarNome;

public class Categoria {

	private UUID id;
	private String nome;

	@Default
	public Categoria(UUID id, String nome) {
		this.id = id;
		this.nome = validarNome(nome, "Nome");
	}

	public Categoria(String nome) {
		this(null, nome);
	}

	public void alterarNome(String novoNome) {
		this.nome = validarNome(novoNome, "Nome");
	}

	public void atualizar(String novoNome) {
		String nomeValidado = validarNome(novoNome, "Nome");
		this.nome = nomeValidado;
	}

	public void atualizarCampos(Categoria novosDados) {
		if (novosDados != null) {
			atualizar(novosDados.nome);
		}
	}

	public UUID getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Categoria categoria = (Categoria) o;

		return id != null && id.equals(categoria.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}