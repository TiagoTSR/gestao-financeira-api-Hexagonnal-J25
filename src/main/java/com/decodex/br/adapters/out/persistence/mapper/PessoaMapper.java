package com.decodex.br.adapters.out.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import com.decodex.br.adapters.out.persistence.entity.EnderecoEmbeddable;
import com.decodex.br.adapters.out.persistence.entity.PessoaEntity;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Pessoa;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PessoaMapper {

    PessoaMapper INSTANCE = Mappers.getMapper(PessoaMapper.class);

    Pessoa toDomain(PessoaEntity entity);

    PessoaEntity toEntity(Pessoa pessoa);

    Endereco toDomain(EnderecoEmbeddable entity);

    EnderecoEmbeddable toEntity(Endereco endereco);
}