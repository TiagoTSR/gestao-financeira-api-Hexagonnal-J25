package com.decodex.br.application.dto.categoria;

public class CategoriaFilter {
	
	private Long id;
    private String nome;

    public CategoriaFilter() {
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
    
}
