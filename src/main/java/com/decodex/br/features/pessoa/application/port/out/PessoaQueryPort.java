package com.decodex.br.features.pessoa.application.port.out;

import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.features.pessoa.application.model.PessoaFilterQuery;

public interface PessoaQueryPort {

    PageResult<Pessoa> findAll(PessoaFilterQuery filter, PageRequest pageRequest);
}
