package com.decodex.br.adapters.out.persistence.mapper;

import org.springframework.stereotype.Component;
import com.decodex.br.adapters.out.persistence.entity.UsuarioEntity;
import com.decodex.br.domain.model.Usuario;

@Component
public class UsuarioMapper {

    public Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Usuario(
            entity.getId(),
            entity.getUsername(),
            entity.getPassword(),
            entity.getEmail()
        );
    }

    public UsuarioEntity toEntity(Usuario domain) {
        if (domain == null) {
            return null;
        }
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(domain.getId());
        entity.setUsername(domain.getUsername());
        entity.setPassword(domain.getPassword());
        entity.setEmail(domain.getEmail());
        return entity;
    }
}
