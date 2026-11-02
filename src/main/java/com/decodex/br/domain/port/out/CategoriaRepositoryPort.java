package com.decodex.br.domain.port.out;

import java.util.Optional;
import java.util.UUID;

import com.decodex.br.domain.model.Categoria;

public interface CategoriaRepositoryPort {
	
	Categoria save(Categoria categoria);

    Optional<Categoria> findById(UUID id);

    void deleteById(UUID id);

}
