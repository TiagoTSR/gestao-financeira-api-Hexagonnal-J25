package com.decodex.br.domain.port.out;

import java.util.Optional;
import java.util.UUID;

import com.decodex.br.domain.model.Pessoa;

public interface PessoaRepositoryPort {
	
	Pessoa save(Pessoa person);

    Optional<Pessoa> findById(UUID id);

    void deleteById(UUID id);

}
