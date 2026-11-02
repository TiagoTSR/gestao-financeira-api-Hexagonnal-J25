package com.decodex.br.testesunitarios.features.autenticacao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.decodex.br.domain.exception.RegraDeNegocioException;
import com.decodex.br.domain.model.Usuario;
import com.decodex.br.domain.port.out.UsuarioRepositoryPort;
import com.decodex.br.features.autenticacao.application.RegisterUserApplicationService;
import com.decodex.br.features.autenticacao.application.model.RegisterUserCommand;
import com.decodex.br.features.autenticacao.application.port.out.PasswordHasher;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários - RegisterUserApplicationService")
class RegisterUserApplicationServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private RegisterUserApplicationService service;

    @Test
    @DisplayName("Deve normalizar username/e-mail e codificar senha antes de salvar")
    void deveCadastrarUsuarioNormalizado() {
        UUID userId = UUID.randomUUID();
        when(usuarioRepository.findByUsername("new-user")).thenReturn(Optional.empty());
        when(usuarioRepository.findByUsernameOrEmail("new@email.com", "new@email.com"))
                .thenReturn(Optional.empty());
        when(passwordHasher.hash("password")).thenReturn("hashed-password");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            return new Usuario(userId, usuario.getUsername(), usuario.getPassword(), usuario.getEmail());
        });

        var result = service.register(new RegisterUserCommand(
                " new-user ", " password ", " NEW@EMAIL.COM "));

        assertThat(result.id()).isEqualTo(userId);
        assertThat(result.username()).isEqualTo("new-user");
        assertThat(result.email()).isEqualTo("new@email.com");
        verify(passwordHasher).hash("password");
        verify(usuarioRepository).save(argThat(usuario ->
                usuario.getUsername().equals("new-user")
                        && usuario.getEmail().equals("new@email.com")
                        && usuario.getPassword().equals("hashed-password")));
    }

    @Test
    @DisplayName("Deve rejeitar username já cadastrado")
    void deveRejeitarUsernameDuplicado() {
        when(usuarioRepository.findByUsername("existing")).thenReturn(Optional.of(
                new Usuario(UUID.randomUUID(), "existing", "hash", "existing@email.com")));

        assertThatThrownBy(() -> service.register(
                new RegisterUserCommand("existing", "password", "new@email.com")))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("username");
        verifyNoInteractions(passwordHasher);
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve rejeitar e-mail já cadastrado")
    void deveRejeitarEmailDuplicado() {
        when(usuarioRepository.findByUsername("new-user")).thenReturn(Optional.empty());
        when(usuarioRepository.findByUsernameOrEmail("existing@email.com", "existing@email.com"))
                .thenReturn(Optional.of(new Usuario(
                        UUID.randomUUID(), "existing", "hash", "existing@email.com")));

        assertThatThrownBy(() -> service.register(
                new RegisterUserCommand("new-user", "password", "existing@email.com")))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining("e-mail");
        verifyNoInteractions(passwordHasher);
        verify(usuarioRepository, never()).save(any());
    }
}
