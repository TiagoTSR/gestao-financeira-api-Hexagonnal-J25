package com.decodex.br.domain.model;

public class Senha {
    private final String hash;

    public Senha(String hash) {
        if (hash == null || hash.isBlank()) {
            throw new IllegalArgumentException("A senha não pode ser vazia.");
        }
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }
}
