package com.decodex.br.domain.validations;

import com.decodex.br.domain.exeption.RegraDeNegocioException;

public final class UsuarioValidation {

    private UsuarioValidation() {
    }

    public static String validarUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new RegraDeNegocioException("O nome de usuário não pode ser vazio.");
        }
        return username;
    }

    public static String validarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new RegraDeNegocioException("O e-mail não pode ser vazio.");
        }
        if (!email.contains("@")) {
            throw new RegraDeNegocioException("E-mail inválido.");
        }
        return email;
    }
}
