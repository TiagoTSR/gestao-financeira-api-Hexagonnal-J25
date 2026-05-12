package com.decodex.br.domain.service;

import java.util.List;

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
                .orElseThrow(() -> new RuntimeException("Lancamento não encontrado: " + id));
    }

    @Override
    public Lancamento create(Lancamento lancamento) {
        return repository.save(lancamento);
    }

    @Override
    public Lancamento update(Long id, Lancamento lancamentoDetails) {
        Lancamento existing = findById(id);

        existing.alterarDescricao(lancamentoDetails.getDescricao());
        existing.alterarDataVencimento(lancamentoDetails.getDataVencimento());
        existing.alterarDataPagamento(lancamentoDetails.getDataPagamento());
        existing.alterarValor(lancamentoDetails.getValor());
        existing.alterarObservacao(lancamentoDetails.getObservacao());
        existing.alterarTipo(lancamentoDetails.getTipo());
        existing.alterarCategoria(lancamentoDetails.getCategoria());
        existing.alterarPessoa(lancamentoDetails.getPessoa());

        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

}
