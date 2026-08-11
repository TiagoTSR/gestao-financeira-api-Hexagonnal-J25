package com.decodex.br.domain.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import com.decodex.br.domain.exeption.RefreshTokenException;
import com.decodex.br.domain.exeption.ResourceNotFoundException;
import com.decodex.br.domain.model.RefreshToken;
import com.decodex.br.domain.model.Usuario;
import com.decodex.br.domain.port.in.RefreshTokenUseCase;
import com.decodex.br.domain.port.out.RefreshTokenRepositoryPort;
import com.decodex.br.domain.port.out.UsuarioRepositoryPort;

public class RefreshTokenService implements RefreshTokenUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public RefreshTokenService(RefreshTokenRepositoryPort refreshTokenRepositoryPort, UsuarioRepositoryPort usuarioRepositoryPort) {
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    @Override
    public RefreshToken create(String username) {
        Usuario usuario = usuarioRepositoryPort.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + username));
        
        // Remove tokens anteriores do usuário para garantir sessão única
        refreshTokenRepositoryPort.deleteByUsuario(usuario);

        RefreshToken refreshToken = new RefreshToken(
                null,
                UUID.randomUUID().toString(),
                usuario,
                Instant.now().plus(7, ChronoUnit.DAYS)
        );

        return refreshTokenRepositoryPort.save(refreshToken);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepositoryPort.deleteByToken(token.getToken());
            throw new RefreshTokenException("Refresh token expirado. Por favor, realize um novo login.");
        }
        return token;
    }

    @Override
    public void deleteByToken(String token) {
        refreshTokenRepositoryPort.deleteByToken(token);
    }

    @Override
    public RefreshToken verifyAndGet(String token) {
        RefreshToken refreshToken = refreshTokenRepositoryPort.findByToken(token)
                .orElseThrow(() -> new RefreshTokenException("Refresh token inválido ou não encontrado."));
        return verifyExpiration(refreshToken);
    }
}
