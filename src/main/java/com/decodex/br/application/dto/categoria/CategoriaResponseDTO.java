package com.decodex.br.application.dto.categoria;

import com.decodex.br.domain.model.Categoria;

public record CategoriaResponseDTO(
    Long id,
    String nome
) {
    public static CategoriaResponseDTO from(Categoria categoria) {
        if (categoria == null) return null;
        return new CategoriaResponseDTO(categoria.getId(), categoria.getNome());
    }
}
