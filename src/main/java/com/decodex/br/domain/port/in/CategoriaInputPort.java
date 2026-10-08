package com.decodex.br.domain.port.in;

import com.decodex.br.application.dto.categoria.CategoriaFilter;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;

public interface CategoriaInputPort {
	
	PageResult<Categoria> findAll(CategoriaFilter filter, PageRequest pageRequest);

    Categoria findById(Long id);

    Categoria create(Categoria categoria);

    Categoria update(Long id, Categoria categoria);

    void delete(Long id);

}
