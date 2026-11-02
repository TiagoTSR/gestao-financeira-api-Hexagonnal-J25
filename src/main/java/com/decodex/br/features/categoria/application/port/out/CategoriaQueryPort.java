package com.decodex.br.features.categoria.application.port.out;

import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.features.categoria.application.model.CategoriaFilterQuery;

public interface CategoriaQueryPort {

    PageResult<Categoria> findAll(CategoriaFilterQuery filter, PageRequest pageRequest);
}
