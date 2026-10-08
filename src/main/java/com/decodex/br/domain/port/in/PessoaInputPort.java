package com.decodex.br.domain.port.in;

import java.util.UUID;

import com.decodex.br.application.dto.pessoa.PessoaFilter;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;

public interface PessoaInputPort {
	
	PageResult<Pessoa> findAll(PessoaFilter filter, PageRequest pageRequest);

    Pessoa findById(UUID id);

    Pessoa create(Pessoa pessoa);

    Pessoa update(UUID id, Pessoa pessoa);

    void delete(UUID id);

}
