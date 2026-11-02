package com.decodex.br.features.autenticacao.application.model;

import java.util.UUID;

public record RegisteredUser(UUID id, String username, String email) {
}
