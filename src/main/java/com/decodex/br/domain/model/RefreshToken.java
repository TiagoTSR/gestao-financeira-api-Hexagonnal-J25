package com.decodex.br.domain.model;

import java.time.Instant;

public class RefreshToken {

    private final Long id;
    private final String token;
    private final Usuario usuario;
    private final Instant expiryDate;

    public RefreshToken(Long id, String token, Usuario usuario, Instant expiryDate) {
        this.id = id;
        this.token = token;
        this.usuario = usuario;
        this.expiryDate = expiryDate;
    }

    public Long getId() {
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
