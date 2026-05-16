package com.decodex.br.application.mapper;

import com.decodex.br.application.dto.lancamento.LancamentoCreateDTO;
import com.decodex.br.application.dto.lancamento.LancamentoResponseDTO;
import com.decodex.br.application.dto.lancamento.LancamentoUpdateDTO;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.Pessoa;

public class LancamentoDTOMapper {

    public static Lancamento toDomain(LancamentoCreateDTO dto, Categoria categoria, Pessoa pessoa) {
        if (dto == null) return null;

        return new Lancamento(
            null,
            dto.descricao(),
            dto.dataVencimento(),
            dto.dataPagamento(),
            dto.valor(),
            dto.observacao(),
            dto.tipo(),
            categoria,
            pessoa 
        );
    }

    public static Lancamento toDomain(LancamentoUpdateDTO dto, Categoria categoria, Pessoa pessoa) {
        if (dto == null) return null;

        return new Lancamento(
            null,
            dto.descricao(),
            dto.dataVencimento(),
            dto.dataPagamento(),
            dto.valor(),
            dto.observacao(),
            dto.tipo(),
            categoria,
            pessoa 
        );
    }

    public static LancamentoResponseDTO toDTO(Lancamento lancamento) {
        if (lancamento == null) return null;
        
        var categoriaDTO = lancamento.getCategoria() != null 
                ? CategoriaDTOMapper.toDTO(lancamento.getCategoria()) 
                : null;
                
        var pessoaDTO = lancamento.getPessoa() != null 
                ? PessoaDTOMapper.toDTO(lancamento.getPessoa()) 
                : null;

         return new LancamentoResponseDTO(
             lancamento.getId(),
             lancamento.getDescricao(),
             lancamento.getDataVencimento(),
             lancamento.getDataPagamento(),
             lancamento.getValor(),
             lancamento.getObservacao(),
             lancamento.getTipo(),
             categoriaDTO,
             pessoaDTO   
         );
    }
}