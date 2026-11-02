package com.decodex.br.features.categoria.application.port.out;

import com.decodex.br.application.dto.categoria.CategoriaFilter;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;

public interface CategoriaQueryPort {

    PageResult<Categoria> findAll(CategoriaFilter filter, PageRequest pageRequest);
}
