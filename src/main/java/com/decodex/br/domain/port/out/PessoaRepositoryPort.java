package com.decodex.br.domain.port.out;

import java.util.Optional;

import com.decodex.br.domain.filter.PessoaFilter;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;

public interface PessoaRepositoryPort {
	
	Pessoa save(Pessoa person);

    Optional<Pessoa> findById(Long id);

    PageResult<Pessoa> findAll(PessoaFilter filter,PageRequest pageRequest);

    void deleteById(Long id);

}
