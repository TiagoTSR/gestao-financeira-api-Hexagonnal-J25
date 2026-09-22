package com.decodex.br.domain.validations;

public class UsuarioValidation {

    public String validarUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("O nome de usuário não pode ser vazio.");
        }
        return username;
    }

    public String validarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("O e-mail não pode ser vazio.");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        return email;
    }
}
