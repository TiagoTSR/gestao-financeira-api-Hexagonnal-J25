package com.decodex.br.domain.port.in;

import java.util.List;

import com.decodex.br.domain.model.Pessoa;

public interface PessoaUseCase {
	
	List<Pessoa> findAll();

    Pessoa findById(Long id);

    Pessoa create(Pessoa pessoa);

    Pessoa update(Long id, Pessoa pessoa);

    void delete(Long id);

}
