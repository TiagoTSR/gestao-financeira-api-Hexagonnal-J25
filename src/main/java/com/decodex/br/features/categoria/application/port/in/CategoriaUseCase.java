package com.decodex.br.features.categoria.application.port.in;

import java.util.UUID;

import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.features.categoria.application.model.CategoriaFilterQuery;

public interface CategoriaUseCase {

    PageResult<Categoria> findAll(CategoriaFilterQuery filter, PageRequest pageRequest);

    Categoria findById(UUID id);

    Categoria create(Categoria categoria);

    Categoria update(UUID id, Categoria categoria);

    void delete(UUID id);
}
