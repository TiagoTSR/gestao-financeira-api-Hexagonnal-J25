package com.decodex.br.application.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
    @NotBlank(message = "Username é obrigatório")
    String username,

    @NotBlank(message = "Password é obrigatório")
    String password
) {}
