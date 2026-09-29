package com.decodex.br.application.dto.categoria;

import com.decodex.br.domain.model.Categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaCreateDTO(
    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, max = 50, message = "O nome deve ter entre 3 e 50 caracteres.")
    String nome
) {
    public Categoria toDomain() {
        return new Categoria(null, nome);
    }
}
