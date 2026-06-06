package com.decodex.br.domain.port.in;

import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;

public interface PessoaUseCase {
	
	PageResult<Pessoa> findAll(PageRequest pageRequest);

    Pessoa findById(Long id);

    Pessoa create(Pessoa pessoa);

    Pessoa update(Long id, Pessoa pessoa);

    void delete(Long id);

}
