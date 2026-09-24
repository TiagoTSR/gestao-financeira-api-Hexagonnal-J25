package com.decodex.br.adapters.out.persistence.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import com.decodex.br.adapters.out.persistence.entity.LancamentoEntity;
import com.decodex.br.domain.model.Lancamento;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = {CategoriaMapper.class, PessoaMapper.class}
)
public interface LancamentoMapper {

    LancamentoMapper INSTANCE = Mappers.getMapper(LancamentoMapper.class);

    Lancamento toDomain(LancamentoEntity entity);

    LancamentoEntity toEntity(Lancamento lancamento);
}