package com.decodex.br.features.autenticacao.application.model;

public record RegisterUserCommand(String username, String password, String email) {
}
