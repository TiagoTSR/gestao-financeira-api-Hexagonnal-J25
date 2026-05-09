package com.decodex.br.domain.port.in;

import java.util.List;

import com.decodex.br.domain.model.Categoria;

public interface CategoriaUseCase {
	
	List<Categoria> findAll();

    Categoria findById(Long id);

    Categoria create(Categoria categoria);

    Categoria update(Long id, Categoria categoria);

    void delete(Long id);

}
