package com.decodex.br.features.autenticacao.application;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import com.decodex.br.domain.exception.RefreshTokenException;
import com.decodex.br.domain.exception.ResourceNotFoundException;
import com.decodex.br.domain.model.RefreshToken;
import com.decodex.br.domain.model.Usuario;
import com.decodex.br.domain.port.out.RefreshTokenRepositoryPort;
import com.decodex.br.domain.port.out.UsuarioRepositoryPort;
import com.decodex.br.features.autenticacao.application.port.in.RefreshTokenUseCase;

public class RefreshTokenApplicationService implements RefreshTokenUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final UsuarioRepositoryPort usuarioRepository;

    public RefreshTokenApplicationService(
            RefreshTokenRepositoryPort refreshTokenRepository,
            UsuarioRepositoryPort usuarioRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public RefreshToken create(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + username));

        refreshTokenRepository.deleteByUsuario(usuario);
        RefreshToken refreshToken = new RefreshToken(
                null,
                UUID.randomUUID().toString(),
                usuario,
                Instant.now().plus(7, ChronoUnit.DAYS));
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.deleteByToken(token.getToken());
            throw new RefreshTokenException("Refresh token expirado. Por favor, realize um novo login.");
        }
        return token;
    }

    @Override
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    @Override
    public RefreshToken verifyAndGet(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RefreshTokenException("Refresh token inválido ou não encontrado."));
        return verifyExpiration(refreshToken);
    }
}
