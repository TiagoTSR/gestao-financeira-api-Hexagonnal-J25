package com.decodex.br.domain.port.out;

import java.util.Optional;

import com.decodex.br.domain.filter.LancamentoFilter;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;

public interface LancamentoRepositoryPort {
	
	Lancamento save(Lancamento person);

    Optional<Lancamento> findById(Long id);

    PageResult<Lancamento> findAll(LancamentoFilter filter,PageRequest pageRequest);
    
    void deleteById(Long id);

}
