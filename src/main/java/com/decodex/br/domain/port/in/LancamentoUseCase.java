package com.decodex.br.domain.port.in;

import java.util.List;

import com.decodex.br.domain.model.Lancamento;

public interface LancamentoUseCase {
	
	List<Lancamento> findAll();

    Lancamento findById(Long id);

    Lancamento create(Lancamento lancamento);

    Lancamento update(Long id, Lancamento lancamento);

    void delete(Long id);

}
