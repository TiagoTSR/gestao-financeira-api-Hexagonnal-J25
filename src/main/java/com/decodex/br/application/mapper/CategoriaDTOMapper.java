package com.decodex.br.application.mapper;

import com.decodex.br.application.dto.categoria.CategoriaDTO;
import com.decodex.br.domain.model.Categoria;

public class CategoriaDTOMapper {

    public static Categoria toDomain(CategoriaDTO.Create dto) {
        if (dto == null) return null;

        return new Categoria(
            null,
            dto.nome()
        );
    }

    public static Categoria toDomain(CategoriaDTO.Update dto) {
        if (dto == null) return null;

        return new Categoria(
            null,
            dto.nome()
        );
    }

    public static CategoriaDTO.Response toDTO(Categoria categoria) {
        if (categoria == null) return null;

        return new CategoriaDTO.Response(
            categoria.getId(),
            categoria.getNome()
        );
    }
}