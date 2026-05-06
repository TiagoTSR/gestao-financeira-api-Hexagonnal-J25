package com.decodex.br.domain.port.out;

import java.util.List;
import java.util.Optional;

import com.decodex.br.domain.model.Categoria;

public interface CategoriaRepositoryPort {
	
	Categoria save(Categoria person);

    Optional<Categoria> findById(Long id);

    List<Categoria> findAll();

    void deleteById(Long id);

}
