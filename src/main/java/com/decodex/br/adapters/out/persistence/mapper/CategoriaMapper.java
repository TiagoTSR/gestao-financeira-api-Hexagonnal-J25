package com.decodex.br.adapters.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import com.decodex.br.adapters.out.persistence.entity.CategoriaEntity;
import com.decodex.br.domain.model.Categoria;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoriaMapper {

    CategoriaMapper INSTANCE = Mappers.getMapper(CategoriaMapper.class);

    Categoria toDomain(CategoriaEntity entity);

    CategoriaEntity toEntity(Categoria categoria);
}