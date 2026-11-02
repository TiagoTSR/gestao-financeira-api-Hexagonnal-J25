package com.decodex.br.features.autenticacao.application.port.out;

public interface PasswordHasher {

    String hash(String rawPassword);
}
