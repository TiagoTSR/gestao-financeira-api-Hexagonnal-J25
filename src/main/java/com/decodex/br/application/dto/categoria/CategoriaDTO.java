package com.decodex.br.application.dto.categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public sealed interface CategoriaDTO {

    record Create(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(min = 3, max = 50, message = "O nome deve ter entre 3 e 50 caracteres.")
        String nome
    ) implements CategoriaDTO {}

    record Update(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(min = 3, max = 50, message = "O nome deve ter entre 3 e 50 caracteres.")
        String nome
    ) implements CategoriaDTO {}

    record Response(
        Long id,
        String nome
    ) implements CategoriaDTO {}
}
