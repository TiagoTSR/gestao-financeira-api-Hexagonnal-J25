package com.decodex.br.adapters.out.persistence.adapter;

import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.decodex.br.adapters.out.persistence.entity.RefreshTokenEntity;
import com.decodex.br.adapters.out.persistence.mapper.RefreshTokenMapper;
import com.decodex.br.adapters.out.persistence.mapper.UsuarioMapper;
import com.decodex.br.adapters.out.persistence.repository.RefreshTokenRepository;
import com.decodex.br.domain.model.RefreshToken;
import com.decodex.br.domain.model.Usuario;
import com.decodex.br.domain.port.out.RefreshTokenRepositoryPort;

@Component
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenRepository repository;
    private final RefreshTokenMapper mapper;
    private final UsuarioMapper usuarioMapper;

    public RefreshTokenRepositoryAdapter(RefreshTokenRepository repository, RefreshTokenMapper mapper, UsuarioMapper usuarioMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    @Transactional
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenEntity entity = mapper.toEntity(refreshToken);
        RefreshTokenEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return repository.findByToken(token)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteByToken(String token) {
        repository.deleteByToken(token);
    }

    @Override
    @Transactional
    public void deleteByUsuario(Usuario usuario) {
        var usuarioEntity = usuarioMapper.toEntity(usuario);
        repository.deleteByUsuario(usuarioEntity);
    }
}
