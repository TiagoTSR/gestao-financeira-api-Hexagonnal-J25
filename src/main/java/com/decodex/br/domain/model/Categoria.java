package com.decodex.br.domain.model;

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
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    
	    Categoria categoria = (Categoria) o;
	    
	    return id != null && id.equals(categoria.id);
	}

	@Override
	public int hashCode() {
	    return getClass().hashCode();
	}
}