package com.decodex.br.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import com.decodex.br.application.dto.categoria.CategoriaResponseDTO;
import com.decodex.br.application.dto.lancamento.LancamentoCreateDTO;
import com.decodex.br.application.dto.lancamento.LancamentoResponseDTO;
import com.decodex.br.application.dto.lancamento.LancamentoUpdateDTO;
import com.decodex.br.application.dto.pessoa.PessoaResumoDTO;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.Pessoa;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LancamentoDTOMapper {

    LancamentoDTOMapper INSTANCE = Mappers.getMapper(LancamentoDTOMapper.class);

    default Lancamento toDomain(LancamentoCreateDTO dto, Categoria categoria, Pessoa pessoa) {
        if (dto == null) return null;
        return toDomainInternal(dto, categoria, pessoa);
    }

    default Lancamento toDomain(LancamentoUpdateDTO dto, Categoria categoria, Pessoa pessoa) {
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
    @Mapping(target = "status", source = "dto.status")
    @Mapping(target = "valorPago", source = "dto.valorPago")
    @Mapping(target = "numeroParcela", source = "dto.numeroParcela")
    @Mapping(target = "totalParcelas", source = "dto.totalParcelas")
    Lancamento toDomainInternal(LancamentoCreateDTO dto, Categoria categoria, Pessoa pessoa);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "descricao", source = "dto.descricao")
    @Mapping(target = "dataVencimento", source = "dto.dataVencimento")
    @Mapping(target = "dataPagamento", source = "dto.dataPagamento")
    @Mapping(target = "valor", source = "dto.valor")
    @Mapping(target = "observacao", source = "dto.observacao")
    @Mapping(target = "tipo", source = "dto.tipo")
    @Mapping(target = "categoria", source = "categoria")
    @Mapping(target = "pessoa", source = "pessoa")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "valorPago", ignore = true)
    @Mapping(target = "numeroParcela", ignore = true)
    @Mapping(target = "totalParcelas", ignore = true)
    Lancamento toDomainInternal(LancamentoUpdateDTO dto, Categoria categoria, Pessoa pessoa);

    @Mapping(target = "categoria", source = "categoria")
    @Mapping(target = "pessoa", source = "pessoa")
    LancamentoResponseDTO toDTO(Lancamento lancamento);

    default CategoriaResponseDTO toCategoriaDTO(Categoria categoria) {
        return CategoriaResponseDTO.from(categoria);
    }

    default PessoaResumoDTO toPessoaResumoDTO(Pessoa pessoa) {
        return PessoaResumoDTO.from(pessoa);
    }
}