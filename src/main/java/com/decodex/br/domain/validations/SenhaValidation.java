package com.decodex.br.domain.validations;

import com.decodex.br.domain.exeption.RegraDeNegocioException;

public final class SenhaValidation {

    private SenhaValidation() {
    }

    public static String validarHash(String hash) {
        if (hash == null || hash.isBlank()) {
            throw new RegraDeNegocioException("A senha não pode ser vazia.");
        }
        return hash;
    }
}
