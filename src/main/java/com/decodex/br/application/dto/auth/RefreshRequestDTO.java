package com.decodex.br.application.dto.auth;

import com.fasterxml.jackson.annotation.JsonAlias;

public record RefreshRequestDTO(
    @JsonAlias({"refresh_token", "refreshToken"})
    String refreshToken
) {}
