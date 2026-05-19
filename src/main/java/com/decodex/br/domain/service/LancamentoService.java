package com.decodex.br.domain.service;

import java.util.List;

import com.decodex.br.domain.exeption.ResourceNotFoundException;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.port.in.LancamentoUseCase;
import com.decodex.br.domain.port.out.LancamentoRepositoryPort;

public class LancamentoService implements LancamentoUseCase {
	
	private final LancamentoRepositoryPort repository;

    public LancamentoService(LancamentoRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public List<Lancamento> findAll() {
        return repository.findAll();
    }

    @Override
    public Lancamento findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lancamento não encontrado: " + id));
    }

    @Override
    public Lancamento create(Lancamento lancamento) {
        return repository.save(lancamento);
    }

    @Override
    public Lancamento update(Long id, Lancamento lancamentoDetails) {
        Lancamento existing = findById(id);
        existing.atualizarCampos(lancamentoDetails);
        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

}
