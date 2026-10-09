package com.decodex.br.application.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record TokenResponseDTO(
    String token,

    @JsonProperty("access_token")
    String accessToken,

    @JsonProperty("refresh_token")
    String refreshToken,

    @JsonProperty("token_type")
    String tokenType,

    @JsonProperty("expires_in")
    Long expiresIn,

    String nome,
    String email,
    List<String> permissoes
) {
    public TokenResponseDTO(String token) {
        this(token, token, null, "Bearer", 900L, null, null, List.of("ROLE_USER"));
    }

    public TokenResponseDTO(String token, String refreshToken, String nome, String email, List<String> permissoes) {
        this(token, token, refreshToken, "Bearer", 900L, nome, email, permissoes != null ? permissoes : List.of("ROLE_USER"));
    }
}
