package com.decodex.br.adapters.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.decodex.br.adapters.out.persistence.entity.CategoriaEntity;
import com.decodex.br.domain.model.Categoria;

@Component
public class CategoriaMapper {
 public Categoria toDomain(CategoriaEntity entity) {
     if (entity == null) return null;
     return new Categoria(
         entity.getId(),
         entity.getNome()
     );
 }

 public CategoriaEntity toEntity(Categoria categoria) {
     if (categoria == null) return null;
     CategoriaEntity entity = new CategoriaEntity();
     entity.setId(categoria.getId());
     entity.setNome(categoria.getNome());
     return entity;
 }
}