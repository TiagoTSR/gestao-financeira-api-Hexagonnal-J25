package com.decodex.br.domain.model;

import java.time.Instant;
import java.util.UUID;

public class RefreshToken {

    private final UUID id;
    private final String token;
    private final Usuario usuario;
    private final Instant expiryDate;

    public RefreshToken(UUID id, String token, Usuario usuario, Instant expiryDate) {
        this.id = id;
        this.token = token;
        this.usuario = usuario;
        this.expiryDate = expiryDate;
    }

    public UUID getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public boolean isExpired() {
        return expiryDate.isBefore(Instant.now());
    }
}
