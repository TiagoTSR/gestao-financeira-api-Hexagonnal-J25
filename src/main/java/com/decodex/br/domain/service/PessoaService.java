package com.decodex.br.domain.service;

import java.util.List;

import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.port.in.PessoaUseCase;
import com.decodex.br.domain.port.out.PessoaRepositoryPort;

public class PessoaService implements PessoaUseCase {
	
	private final PessoaRepositoryPort repository;

    public PessoaService(PessoaRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public List<Pessoa> findAll() {
        return repository.findAll();
    }

    @Override
    public Pessoa findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pessoa não encontrado: " + id));
    }

    @Override
    public Pessoa create(Pessoa pessoa) {
        return repository.save(pessoa);
    }

    @Override
    public Pessoa update(Long id, Pessoa pessoaDetails) {
        Pessoa existing = findById(id);

        existing.alterarNome(pessoaDetails.getNome());
        existing.alterarEndereço(pessoaDetails.getEndereco());
        existing.alterarAtivo(pessoaDetails.getAtivo());

        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

}
