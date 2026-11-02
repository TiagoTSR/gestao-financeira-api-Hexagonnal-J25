package com.decodex.br.features.autenticacao.application;

import com.decodex.br.domain.exception.RegraDeNegocioException;
import com.decodex.br.domain.model.Usuario;
import com.decodex.br.domain.port.out.UsuarioRepositoryPort;
import com.decodex.br.features.autenticacao.application.model.RegisterUserCommand;
import com.decodex.br.features.autenticacao.application.model.RegisteredUser;
import com.decodex.br.features.autenticacao.application.port.in.RegisterUserUseCase;
import com.decodex.br.features.autenticacao.application.port.out.PasswordHasher;

public class RegisterUserApplicationService implements RegisterUserUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordHasher passwordHasher;

    public RegisterUserApplicationService(
            UsuarioRepositoryPort usuarioRepository,
            PasswordHasher passwordHasher) {
        this.usuarioRepository = usuarioRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public RegisteredUser register(RegisterUserCommand command) {
        String username = command.username().trim();
        String email = command.email().trim().toLowerCase();

        if (usuarioRepository.findByUsername(username).isPresent()) {
            throw new RegraDeNegocioException(
                    "Já existe um usuário cadastrado com o username: " + username);
        }
        if (usuarioRepository.findByUsernameOrEmail(email, email).isPresent()) {
            throw new RegraDeNegocioException(
                    "Já existe um usuário cadastrado com o e-mail: " + email);
        }

        Usuario saved = usuarioRepository.save(new Usuario(
                username,
                passwordHasher.hash(command.password().trim()),
                email));
        return new RegisteredUser(saved.getId(), saved.getUsername(), saved.getEmail());
    }
}
