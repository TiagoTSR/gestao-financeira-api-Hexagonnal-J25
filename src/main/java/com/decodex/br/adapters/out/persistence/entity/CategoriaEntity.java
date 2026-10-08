package com.decodex.br.adapters.out.persistence.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "categoria", uniqueConstraints = {
    @UniqueConstraint(name = "uk_categoria_nome", columnNames = "nome")
})
public class CategoriaEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@NotBlank
    @Size(min = 3, max = 50)
    @Column(length = 50, nullable = false)
    private String nome;
    
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
	
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;

	    if (!(o instanceof CategoriaEntity)) return false;

	    CategoriaEntity other = (CategoriaEntity) o;

	    return id != null && id.equals(other.id);
	}

	@Override
	public int hashCode() {
	    return getClass().hashCode();
	}
}
