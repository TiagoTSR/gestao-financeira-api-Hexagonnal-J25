package com.decodex.br.features.pessoa.application;

import java.util.UUID;

import com.decodex.br.application.dto.pessoa.PessoaFilter;
import com.decodex.br.domain.exception.ResourceNotFoundException;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.domain.port.out.PessoaRepositoryPort;
import com.decodex.br.features.pessoa.application.port.in.PessoaUseCase;
import com.decodex.br.features.pessoa.application.port.out.PessoaQueryPort;

public class PessoaApplicationService implements PessoaUseCase {

    private final PessoaRepositoryPort repository;
    private final PessoaQueryPort query;

    public PessoaApplicationService(PessoaRepositoryPort repository, PessoaQueryPort query) {
        this.repository = repository;
        this.query = query;
    }

    @Override
    public PageResult<Pessoa> findAll(PessoaFilter filter, PageRequest pageRequest) {
        return query.findAll(filter, pageRequest);
    }

    @Override
    public Pessoa findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada: " + id));
    }

    @Override
    public Pessoa create(Pessoa pessoa) {
        return repository.save(pessoa);
    }

    @Override
    public Pessoa update(UUID id, Pessoa pessoaDetails) {
        Pessoa existing = findById(id);
        existing.atualizarCampos(pessoaDetails);
        return repository.save(existing);
    }

    @Override
    public void atualizarAtivo(UUID id, Boolean ativo) {
        Pessoa existing = findById(id);
        existing.alterarAtivo(ativo);
        repository.save(existing);
    }

    @Override
    public void delete(UUID id) {
        findById(id);
        repository.deleteById(id);
    }
}
