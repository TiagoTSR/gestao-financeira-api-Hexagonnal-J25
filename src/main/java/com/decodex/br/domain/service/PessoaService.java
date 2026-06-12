package com.decodex.br.domain.service;

import com.decodex.br.domain.exeption.ResourceNotFoundException;
import com.decodex.br.domain.filter.PessoaFilter;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.domain.port.in.PessoaUseCase;
import com.decodex.br.domain.port.out.PessoaRepositoryPort;

public class PessoaService implements PessoaUseCase {
    
    private final PessoaRepositoryPort repository;

    public PessoaService(PessoaRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public PageResult<Pessoa> findAll(PessoaFilter filter,PageRequest pageRequest) {
        return repository.findAll(filter,pageRequest);
    }

    @Override
    public Pessoa findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada: " + id));
    }

    @Override
    public Pessoa create(Pessoa pessoa) {
        return repository.save(pessoa);
    }

    @Override
    public Pessoa update(Long id, Pessoa pessoaDetails) {

        Pessoa existing = findById(id);
        
        existing.atualizarCampos(pessoaDetails);
        
        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }
}