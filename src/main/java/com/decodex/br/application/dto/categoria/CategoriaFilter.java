package com.decodex.br.application.dto.categoria;

import java.util.UUID;

public class CategoriaFilter {
	
	private UUID id;
    private String nome;

    public CategoriaFilter() {
    }

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
    
}
