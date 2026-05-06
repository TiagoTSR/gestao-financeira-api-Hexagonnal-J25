package com.decodex.br.domain.model;

import java.util.Objects;

public class Categoria {

	private Long id;
    private String nome;

    public Categoria(Long id, String nome) {
        this.id = id;
        this.nome = validarNomeCategoria(nome, "Nome");
    }

	private String validarNomeCategoria(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " não pode ser vazio");
        }
        return valor;
    }

	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, nome);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Categoria other = (Categoria) obj;
		return Objects.equals(id, other.id) && Objects.equals(nome, other.nome);
	}    
	
}