package com.decodex.br.features.pessoa.application.port.in;

import java.util.UUID;

import com.decodex.br.application.dto.pessoa.PessoaFilter;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;

public interface PessoaUseCase {

    PageResult<Pessoa> findAll(PessoaFilter filter, PageRequest pageRequest);

    Pessoa findById(UUID id);

    Pessoa create(Pessoa pessoa);

    Pessoa update(UUID id, Pessoa pessoa);

    void atualizarAtivo(UUID id, Boolean ativo);

    void delete(UUID id);
}
