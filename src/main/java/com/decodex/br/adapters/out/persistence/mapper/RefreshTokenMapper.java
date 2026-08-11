package com.decodex.br.adapters.out.persistence.mapper;

import org.springframework.stereotype.Component;
import com.decodex.br.adapters.out.persistence.entity.RefreshTokenEntity;
import com.decodex.br.domain.model.RefreshToken;

@Component
public class RefreshTokenMapper {

    private final UsuarioMapper usuarioMapper;

    public RefreshTokenMapper(UsuarioMapper usuarioMapper) {
        this.usuarioMapper = usuarioMapper;
    }

    public RefreshToken toDomain(RefreshTokenEntity entity) {
        if (entity == null) {
            return null;
        }
        return new RefreshToken(
                entity.getId(),
                entity.getToken(),
                usuarioMapper.toDomain(entity.getUsuario()),
                entity.getExpiryDate()
        );
    }

    public RefreshTokenEntity toEntity(RefreshToken domain) {
        if (domain == null) {
            return null;
        }
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setId(domain.getId());
        entity.setToken(domain.getToken());
        entity.setUsuario(usuarioMapper.toEntity(domain.getUsuario()));
        entity.setExpiryDate(domain.getExpiryDate());
        return entity;
    }
}
