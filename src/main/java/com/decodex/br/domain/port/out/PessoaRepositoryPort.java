package com.decodex.br.domain.port.out;

import java.util.Optional;
import java.util.UUID;

import com.decodex.br.application.dto.pessoa.PessoaFilter;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;

public interface PessoaRepositoryPort {
	
	Pessoa save(Pessoa person);

    Optional<Pessoa> findById(UUID id);

    PageResult<Pessoa> findAll(PessoaFilter filter, PageRequest pageRequest);

    void deleteById(UUID id);

}
