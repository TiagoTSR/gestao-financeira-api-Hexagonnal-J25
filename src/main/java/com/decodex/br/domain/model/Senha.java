package com.decodex.br.domain.model;

import static com.decodex.br.domain.validations.SenhaValidation.validarHash;

public class Senha {
    private final String hash;

    public Senha(String hash) {
        this.hash = validarHash(hash);
    }

    public String getHash() {
        return hash;
    }
}
