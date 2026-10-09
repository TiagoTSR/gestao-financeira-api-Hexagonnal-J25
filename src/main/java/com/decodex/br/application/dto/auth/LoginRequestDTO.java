package com.decodex.br.application.dto.auth;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
    @JsonAlias({"username", "email", "usuario"})
    @NotBlank(message = "Username/Email é obrigatório")
    String username,

    @JsonAlias({"password", "senha"})
    @NotBlank(message = "Password/Senha é obrigatório")
    String password
) {}
