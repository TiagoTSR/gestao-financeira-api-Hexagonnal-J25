package com.decodex.br.domain.port.out;

import java.util.Optional;
import com.decodex.br.domain.model.Usuario;

public interface UsuarioRepositoryPort {
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByUsernameOrEmail(String username, String email);
    Usuario save(Usuario usuario);
}
