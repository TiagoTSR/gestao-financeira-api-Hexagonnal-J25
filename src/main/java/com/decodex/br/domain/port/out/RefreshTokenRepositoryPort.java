package com.decodex.br.domain.port.out;

import java.util.Optional;
import com.decodex.br.domain.model.RefreshToken;
import com.decodex.br.domain.model.Usuario;

public interface RefreshTokenRepositoryPort {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByToken(String token);

    void deleteByToken(String token);

    void deleteByUsuario(Usuario usuario);
}
