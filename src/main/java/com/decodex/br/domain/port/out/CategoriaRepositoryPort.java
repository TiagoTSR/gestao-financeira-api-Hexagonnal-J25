package com.decodex.br.domain.port.out;

import java.util.Optional;
import java.util.UUID;

import com.decodex.br.application.dto.categoria.CategoriaFilter;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;

public interface CategoriaRepositoryPort {
	
	Categoria save(Categoria person);

    Optional<Categoria> findById(UUID id);

    PageResult<Categoria> findAll(CategoriaFilter filter, PageRequest pageRequest);

    void deleteById(UUID id);

}
