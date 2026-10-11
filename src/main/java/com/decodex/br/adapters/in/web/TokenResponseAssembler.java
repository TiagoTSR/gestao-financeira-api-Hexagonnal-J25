package com.decodex.br.adapters.in.web;

import com.decodex.br.application.dto.auth.TokenResponseDTO;
import com.decodex.br.domain.model.Usuario;
import com.decodex.br.domain.port.out.UsuarioRepositoryPort;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TokenResponseAssembler {

    public static final String TOKEN_TYPE = "Bearer";
    public static final long ACCESS_TOKEN_EXPIRATION_SECONDS = 900L;

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public TokenResponseAssembler(@Lazy UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    public TokenResponseDTO montar(UserDetails userDetails, String accessToken, String refreshToken) {
        String username = userDetails.getUsername();
        String email = username;

        if (usuarioRepositoryPort != null) {
            email = usuarioRepositoryPort.findByUsernameOrEmail(username, username)
                    .map(Usuario::getEmail)
                    .orElse(username);
        }

        List<String> permissoes = (userDetails.getAuthorities() != null && !userDetails.getAuthorities().isEmpty())
                ? userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()
                : List.of("ROLE_USER");

        return new TokenResponseDTO(
                accessToken,
                accessToken,
                refreshToken,
                TOKEN_TYPE,
                ACCESS_TOKEN_EXPIRATION_SECONDS,
                username,
                email,
                permissoes
        );
    }
}
