package com.decodex.br.adapters.out.persistence.adapter;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.decodex.br.adapters.out.persistence.entity.PessoaEntity;
import com.decodex.br.adapters.out.persistence.mapper.PessoaMapper;
import com.decodex.br.adapters.out.persistence.repository.PessoaRepository;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.domain.port.out.PessoaRepositoryPort;

@Component
public class PessoaRepositoryAdapter implements PessoaRepositoryPort {

    private final PessoaRepository repository;
    private final PessoaMapper mapper;
    
    public PessoaRepositoryAdapter(
            PessoaRepository repository,
            PessoaMapper mapper) {

        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Pessoa save(Pessoa pessoa) {
        return mapper.toDomain(
            repository.save(mapper.toEntity(pessoa))
        );
    }

    @Override
    public Optional<Pessoa> findById(Long id) {
        return repository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public PageResult<Pessoa> findAll(PageRequest request) {

        Page<PessoaEntity> page = repository.findAll(
            org.springframework.data.domain.PageRequest.of(
                request.page(),
                request.size()
            )
        );

        return new PageResult<>(
            page.getContent().stream().map(mapper::toDomain).toList(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}