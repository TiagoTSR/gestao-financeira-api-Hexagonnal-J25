package com.decodex.br.features.categoria.application;

import java.util.UUID;

import com.decodex.br.application.dto.categoria.CategoriaFilter;
import com.decodex.br.domain.exception.ResourceNotFoundException;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.domain.port.out.CategoriaRepositoryPort;
import com.decodex.br.features.categoria.application.port.in.CategoriaUseCase;
import com.decodex.br.features.categoria.application.port.out.CategoriaQueryPort;

public class CategoriaApplicationService implements CategoriaUseCase {

    private final CategoriaRepositoryPort repository;
    private final CategoriaQueryPort query;

    public CategoriaApplicationService(CategoriaRepositoryPort repository, CategoriaQueryPort query) {
        this.repository = repository;
        this.query = query;
    }

    @Override
    public PageResult<Categoria> findAll(CategoriaFilter filter, PageRequest pageRequest) {
        return query.findAll(filter, pageRequest);
    }

    @Override
    public Categoria findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + id));
    }

    @Override
    public Categoria create(Categoria categoria) {
        return repository.save(categoria);
    }

    @Override
    public Categoria update(UUID id, Categoria categoriaDetails) {
        Categoria existing = findById(id);
        existing.atualizarCampos(categoriaDetails);
        return repository.save(existing);
    }

    @Override
    public void delete(UUID id) {
        findById(id);
        repository.deleteById(id);
    }
}
