package com.decodex.br.domain.port.out;

import java.util.Optional;

import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;

public interface CategoriaRepositoryPort {
	
	Categoria save(Categoria person);

    Optional<Categoria> findById(Long id);

    PageResult<Categoria> findAll(PageRequest pageRequest);

    void deleteById(Long id);

}
