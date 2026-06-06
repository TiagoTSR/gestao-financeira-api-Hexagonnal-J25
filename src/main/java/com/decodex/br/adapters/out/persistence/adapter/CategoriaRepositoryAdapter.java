package com.decodex.br.adapters.out.persistence.adapter;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.decodex.br.adapters.out.persistence.entity.CategoriaEntity;
import com.decodex.br.adapters.out.persistence.mapper.CategoriaMapper;
import com.decodex.br.adapters.out.persistence.repository.CategoriaRepository;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.domain.port.out.CategoriaRepositoryPort;

@Component
public class CategoriaRepositoryAdapter implements CategoriaRepositoryPort {

    private final CategoriaRepository repository;
    private final CategoriaMapper mapper;

    public CategoriaRepositoryAdapter(
            CategoriaRepository repository,CategoriaMapper mapper) {

        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Categoria save(Categoria categoria) {
        return mapper.toDomain(
        		repository.save(
                		mapper.toEntity(categoria)
                )
        );
    }

    @Override
    public Optional<Categoria> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public PageResult<Categoria> findAll(PageRequest request) {

        Page<CategoriaEntity> page = repository.findAll(
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