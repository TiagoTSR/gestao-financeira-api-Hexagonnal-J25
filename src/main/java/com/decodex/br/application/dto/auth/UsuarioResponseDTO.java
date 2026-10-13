package com.decodex.br.application.dto.auth;

import java.util.UUID;

public record UsuarioResponseDTO(
    UUID id,
    String username,
    String email
) {}
