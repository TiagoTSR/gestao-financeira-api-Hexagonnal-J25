package com.decodex.br.domain.port.out;

import java.util.Optional;
import java.util.UUID;

import com.decodex.br.application.dto.lancamento.LancamentoFilter;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;

public interface LancamentoRepositoryPort {
	
	Lancamento save(Lancamento lancamento);

    Optional<Lancamento> findById(UUID id);

    PageResult<Lancamento> findAll(LancamentoFilter filter, PageRequest pageRequest);

    java.util.List<Lancamento> findAll();
    
    void deleteById(UUID id);

}
