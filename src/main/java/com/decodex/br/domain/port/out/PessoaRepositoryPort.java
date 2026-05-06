package com.decodex.br.domain.port.out;

import java.util.List;
import java.util.Optional;

import com.decodex.br.domain.model.Pessoa;

public interface PessoaRepositoryPort {
	
	Pessoa save(Pessoa person);

    Optional<Pessoa> findById(Long id);

    List<Pessoa> findAll();

    void deleteById(Long id);

}
