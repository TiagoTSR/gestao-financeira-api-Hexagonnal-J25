package com.decodex.br.domain.port.in;

import com.decodex.br.domain.filter.LancamentoFilter;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;

public interface LancamentoUseCase {
	
	PageResult<Lancamento> findAll(LancamentoFilter filter,PageRequest pageRequest);

    Lancamento findById(Long id);

    Lancamento create(Lancamento lancamento);

    Lancamento update(Long id, Lancamento lancamento);

    void delete(Long id);

}
