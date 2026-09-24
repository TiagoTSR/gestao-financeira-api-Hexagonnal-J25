package com.decodex.br.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import com.decodex.br.application.dto.categoria.CategoriaDTO;
import com.decodex.br.application.dto.lancamento.LancamentoDTO;
import com.decodex.br.application.dto.pessoa.PessoaDTO;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.Pessoa;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LancamentoDTOMapper {

    LancamentoDTOMapper INSTANCE = Mappers.getMapper(LancamentoDTOMapper.class);

    default Lancamento toDomain(LancamentoDTO.Create dto, Categoria categoria, Pessoa pessoa) {
        if (dto == null) return null;
        return toDomainInternal(dto, categoria, pessoa);
    }

    default Lancamento toDomain(LancamentoDTO.Update dto, Categoria categoria, Pessoa pessoa) {
        if (dto == null) return null;
        return toDomainInternal(dto, categoria, pessoa);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "descricao", source = "dto.descricao")
    @Mapping(target = "dataVencimento", source = "dto.dataVencimento")
    @Mapping(target = "dataPagamento", source = "dto.dataPagamento")
    @Mapping(target = "valor", source = "dto.valor")
    @Mapping(target = "observacao", source = "dto.observacao")
    @Mapping(target = "tipo", source = "dto.tipo")
    @Mapping(target = "categoria", source = "categoria")
    @Mapping(target = "pessoa", source = "pessoa")
    Lancamento toDomainInternal(LancamentoDTO.Create dto, Categoria categoria, Pessoa pessoa);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "descricao", source = "dto.descricao")
    @Mapping(target = "dataVencimento", source = "dto.dataVencimento")
    @Mapping(target = "dataPagamento", source = "dto.dataPagamento")
    @Mapping(target = "valor", source = "dto.valor")
    @Mapping(target = "observacao", source = "dto.observacao")
    @Mapping(target = "tipo", source = "dto.tipo")
    @Mapping(target = "categoria", source = "categoria")
    @Mapping(target = "pessoa", source = "pessoa")
    Lancamento toDomainInternal(LancamentoDTO.Update dto, Categoria categoria, Pessoa pessoa);

    @Mapping(target = "categoria", source = "categoria")
    @Mapping(target = "pessoa", source = "pessoa")
    LancamentoDTO.Response toDTO(Lancamento lancamento);

    default CategoriaDTO.Response toCategoriaDTO(Categoria categoria) {
        return CategoriaDTO.Response.from(categoria);
    }

    default PessoaDTO.Resumo toPessoaResumoDTO(Pessoa pessoa) {
        return PessoaDTO.Resumo.from(pessoa);
    }
}