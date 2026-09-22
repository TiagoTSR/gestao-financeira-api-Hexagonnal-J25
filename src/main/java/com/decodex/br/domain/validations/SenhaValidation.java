package com.decodex.br.domain.validations;

public class SenhaValidation {

    public String validarHash(String hash) {
        if (hash == null || hash.isBlank()) {
            throw new IllegalArgumentException("A senha não pode ser vazia.");
        }
        return hash;
    }
}
