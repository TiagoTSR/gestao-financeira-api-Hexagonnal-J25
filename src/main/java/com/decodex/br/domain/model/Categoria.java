package com.decodex.br.domain.model;

import static com.decodex.br.domain.validations.CategoriaValidation.validarNome;

public class Categoria {

	private Long id;
	private String nome;

	public Categoria(Long id, String nome) {
		this.id = id;
		this.nome = validarNome(nome, "Nome");
	}

	public Categoria(String nome) {
		this(null, nome);
	}

	public void alterarNome(String novoNome) {
		this.nome = validarNome(novoNome, "Nome");
	}

	public void atualizarCampos(Categoria novosDados) {
		alterarNome(novosDados.nome);
	}

	public Long getId() {
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