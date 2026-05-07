package com.decodex.br.adapters.out.persistence.mapper;

import com.decodex.br.adapters.out.persistence.entity.LancamentoEntity;
import com.decodex.br.domain.model.Lancamento;

public class LancamentoMapper {

    private final CategoriaMapper categoriaMapper = new CategoriaMapper();
    private final PessoaMapper pessoaMapper = new PessoaMapper();

    public Lancamento toDomain(LancamentoEntity entity) {
        return new Lancamento(
            entity.getId(),
            entity.getDescricao(),
            entity.getDataVencimento(),
            entity.getDataPagamento(),
            entity.getValor(),
            entity.getObservacao(),
            entity.getTipo(),
            categoriaMapper.toDomain(entity.getCategoria()),
            pessoaMapper.toDomain(entity.getPessoa())
        );
    }

    public LancamentoEntity toEntity(Lancamento lancamento) {
        LancamentoEntity entity = new LancamentoEntity();
        entity.setId(lancamento.getId());
        entity.setDescricao(lancamento.getDescricao());
        entity.setDataVencimento(lancamento.getDataVencimento());
        entity.setDataPagamento(lancamento.getDataPagamento());
        entity.setValor(lancamento.getValor());
        entity.setObservacao(lancamento.getObservacao());
        entity.setTipo(lancamento.getTipo());
        entity.setCategoria(categoriaMapper.toEntity(lancamento.getCategoria()));
        entity.setPessoa(pessoaMapper.toEntity(lancamento.getPessoa()));
        return entity;
    }
}