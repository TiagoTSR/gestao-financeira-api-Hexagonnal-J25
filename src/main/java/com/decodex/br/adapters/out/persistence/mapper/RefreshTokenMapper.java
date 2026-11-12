package com.decodex.br.adapters.out.persistence.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.decodex.br.adapters.out.persistence.entity.RefreshTokenEntity;
import com.decodex.br.domain.model.RefreshToken;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = {UsuarioMapper.class}
)
public interface RefreshTokenMapper {

    RefreshToken toDomain(RefreshTokenEntity entity);

    @org.mapstruct.Mapping(target = "tenantId", ignore = true)
    RefreshTokenEntity toEntity(RefreshToken domain);
}
