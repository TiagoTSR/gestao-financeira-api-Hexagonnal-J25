package com.decodex.br.domain.model;

import com.decodex.br.domain.validations.SenhaValidation;

public class Senha {
    private final String hash;
    private final SenhaValidation validation = new SenhaValidation();

    public Senha(String hash) {
        this.hash = validation.validarHash(hash);
    }

    public String getHash() {
        return hash;
    }
}
