package com.decodex.br.testesunitarios.features.autenticacao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.decodex.br.domain.exception.RefreshTokenException;
import com.decodex.br.domain.exception.ResourceNotFoundException;
import com.decodex.br.domain.model.RefreshToken;
import com.decodex.br.domain.model.Usuario;
import com.decodex.br.domain.port.out.RefreshTokenRepositoryPort;
import com.decodex.br.domain.port.out.UsuarioRepositoryPort;
import com.decodex.br.features.autenticacao.application.RefreshTokenApplicationService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários - RefreshTokenApplicationService")
class RefreshTokenApplicationServiceTest {

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepository;

    @Mock
    private UsuarioRepositoryPort usuarioRepository;

    @InjectMocks
    private RefreshTokenApplicationService service;

    private final UUID usuarioId = UUID.randomUUID();
    private final UUID tokenId = UUID.randomUUID();

    @Test
    @DisplayName("Deve criar refresh token com validade de sete dias e remover os anteriores")
    void deveCriarRefreshTokenComSucesso() {
        Usuario usuario = new Usuario(usuarioId, "admin", "hash", "admin@email.com");
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken created = service.create("admin");

        assertThat(created.getToken()).isNotBlank();
        assertThat(created.getUsuario()).isEqualTo(usuario);
        assertThat(created.getExpiryDate()).isAfter(Instant.now().plus(6, ChronoUnit.DAYS));
        verify(refreshTokenRepository).deleteByUsuario(usuario);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Deve rejeitar criação de token para usuário inexistente")
    void deveLancarErroParaUsuarioInexistente() {
        when(usuarioRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create("unknown"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Usuário não encontrado");
    }

    @Test
    @DisplayName("Deve retornar token ainda válido")
    void deveRetornarTokenSeNaoExpirado() {
        Usuario usuario = new Usuario(usuarioId, "admin", "hash", "admin@email.com");
        RefreshToken token = new RefreshToken(
                tokenId, "some-token", usuario, Instant.now().plusSeconds(3600));

        assertThat(service.verifyExpiration(token)).isEqualTo(token);
        verify(refreshTokenRepository, never()).deleteByToken(anyString());
    }

    @Test
    @DisplayName("Deve remover e rejeitar token expirado")
    void deveDeletarTokenExpirado() {
        Usuario usuario = new Usuario(usuarioId, "admin", "hash", "admin@email.com");
        RefreshToken token = new RefreshToken(
                tokenId, "some-token", usuario, Instant.now().minusSeconds(10));

        assertThatThrownBy(() -> service.verifyExpiration(token))
                .isInstanceOf(RefreshTokenException.class)
                .hasMessageContaining("Refresh token expirado");
        verify(refreshTokenRepository).deleteByToken("some-token");
    }

    @Test
    @DisplayName("Deve localizar e verificar refresh token")
    void deveVerificarTokenPorValor() {
        Usuario usuario = new Usuario(usuarioId, "admin", "hash", "admin@email.com");
        RefreshToken token = new RefreshToken(
                tokenId, "valid-token", usuario, Instant.now().plusSeconds(3600));
        when(refreshTokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));

        assertThat(service.verifyAndGet("valid-token")).isEqualTo(token);
    }

    @Test
    @DisplayName("Deve rejeitar refresh token desconhecido")
    void deveRejeitarTokenDesconhecido() {
        when(refreshTokenRepository.findByToken("invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.verifyAndGet("invalid"))
                .isInstanceOf(RefreshTokenException.class)
                .hasMessageContaining("Refresh token inválido");
    }
}
