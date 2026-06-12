package com.decodex.br.domain.service;

import com.decodex.br.domain.exeption.ResourceNotFoundException;
import com.decodex.br.domain.filter.CategoriaFilter;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.domain.port.in.CategoriaUseCase;
import com.decodex.br.domain.port.out.CategoriaRepositoryPort;


public class CategoriaService implements CategoriaUseCase {

	private final CategoriaRepositoryPort repository;

    public CategoriaService(CategoriaRepositoryPort repository) {
        this.repository = repository;
    }

    public PageResult<Categoria> findAll(CategoriaFilter filter,PageRequest pageRequest) {
        return repository.findAll(filter,pageRequest);
    }

    @Override
    public Categoria findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrado: " + id));
    }

    @Override
    public Categoria create(Categoria categoria) {
        return repository.save(categoria);
    }

    @Override
    public Categoria update(Long id, Categoria categoriaDetails) {
        Categoria existing = findById(id);
        existing.atualizarCampos(categoriaDetails);
        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }
}