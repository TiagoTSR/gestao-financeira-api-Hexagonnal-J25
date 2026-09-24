package com.decodex.br.application.dto.categoria;

import com.decodex.br.domain.model.Categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public sealed interface CategoriaDTO {

    record Create(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(min = 3, max = 50, message = "O nome deve ter entre 3 e 50 caracteres.")
        String nome
    ) implements CategoriaDTO {
        public Categoria toDomain() {
            return new Categoria(null, nome);
        }
    }

    record Update(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(min = 3, max = 50, message = "O nome deve ter entre 3 e 50 caracteres.")
        String nome
    ) implements CategoriaDTO {
        public Categoria toDomain() {
            return new Categoria(null, nome);
        }
    }

    record Response(
        Long id,
        String nome
    ) implements CategoriaDTO {
        public static Response from(Categoria categoria) {
            if (categoria == null) return null;
            return new Response(categoria.getId(), categoria.getNome());
        }
    }
}
