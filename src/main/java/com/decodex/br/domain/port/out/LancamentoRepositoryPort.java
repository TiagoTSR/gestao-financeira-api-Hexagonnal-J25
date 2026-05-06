package com.decodex.br.domain.port.out;

import java.util.List;
import java.util.Optional;

import com.decodex.br.domain.model.Lancamento;

public interface LancamentoRepositoryPort {
	
	Lancamento save(Lancamento person);

    Optional<Lancamento> findById(Long id);

    List<Lancamento> findAll();

    void deleteById(Long id);

}
