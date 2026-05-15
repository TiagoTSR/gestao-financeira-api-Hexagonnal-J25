package com.decodex.br.application.mapper;

import com.decodex.br.application.dto.categoria.CategoriaCreateDTO;
import com.decodex.br.application.dto.categoria.CategoriaResponseDTO;
import com.decodex.br.application.dto.categoria.CategoriaUpdateDTO;
import com.decodex.br.domain.model.Categoria;

public class CategoriaDTOMapper {

    public static Categoria toDomain(CategoriaCreateDTO dto) {
        if (dto == null) return null;

        return new Categoria(
            null,
            dto.nome()
        );
    }

    public static Categoria toDomain(CategoriaUpdateDTO dto) {
        if (dto == null) return null;

        return new Categoria(
            null,
            dto.nome()
        );
    }

    public static CategoriaResponseDTO toDTO(Categoria categoria) {
        if (categoria == null) return null;

        return new CategoriaResponseDTO(
            categoria.getId(),
            categoria.getNome()
        );
    }
}