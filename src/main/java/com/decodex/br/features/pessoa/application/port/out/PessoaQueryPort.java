package com.decodex.br.features.pessoa.application.port.out;

import com.decodex.br.application.dto.pessoa.PessoaFilter;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;

public interface PessoaQueryPort {

    PageResult<Pessoa> findAll(PessoaFilter filter, PageRequest pageRequest);
}
