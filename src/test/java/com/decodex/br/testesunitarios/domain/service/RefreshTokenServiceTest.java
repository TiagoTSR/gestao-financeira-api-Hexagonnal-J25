package com.decodex.br.testesunitarios.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.decodex.br.domain.exeption.RefreshTokenException;
import com.decodex.br.domain.exeption.ResourceNotFoundException;
import com.decodex.br.domain.model.RefreshToken;
import com.decodex.br.domain.model.Usuario;
import com.decodex.br.domain.port.out.RefreshTokenRepositoryPort;
import com.decodex.br.domain.port.out.UsuarioRepositoryPort;
import com.decodex.br.domain.service.RefreshTokenService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários - RefreshTokenService")
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @InjectMocks
    private RefreshTokenService service;

    @Test
    @DisplayName("Deve criar um RefreshToken com validade de 7 dias e limpar anteriores")
    void deveCriarRefreshTokenComSucesso() {
        Usuario usuario = new Usuario(1L, "admin", "hash", "admin@email.com");
        when(usuarioRepositoryPort.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(refreshTokenRepositoryPort.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken created = service.create("admin");

        assertThat(created).isNotNull();
        assertThat(created.getToken()).isNotBlank();
        assertThat(created.getUsuario()).isEqualTo(usuario);
        assertThat(created.getExpiryDate()).isAfter(Instant.now().plus(6, ChronoUnit.DAYS));

        verify(refreshTokenRepositoryPort).deleteByUsuario(usuario);
        verify(refreshTokenRepositoryPort).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar criar token para usuário não existente")
    void deveLancarErroCriarTokenUsuarioInexistente() {
        when(usuarioRepositoryPort.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create("unknown"))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Usuário não encontrado");
    }

    @Test
    @DisplayName("Deve validar a expiração e retornar o token se ele for válido")
    void deveRetornarTokenSeNaoExpirado() {
        Usuario usuario = new Usuario(1L, "admin", "hash", "admin@email.com");
        RefreshToken token = new RefreshToken(1L, "some-token", usuario, Instant.now().plusSeconds(3600));

        RefreshToken result = service.verifyExpiration(token);

        assertThat(result).isEqualTo(token);
        verify(refreshTokenRepositoryPort, never()).deleteByToken(anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção e deletar do banco se o token estiver expirado")
    void deveLancarExcecaoEDeletarSeExpirado() {
        Usuario usuario = new Usuario(1L, "admin", "hash", "admin@email.com");
        RefreshToken token = new RefreshToken(1L, "some-token", usuario, Instant.now().minusSeconds(10));

        assertThatThrownBy(() -> service.verifyExpiration(token))
            .isInstanceOf(RefreshTokenException.class)
            .hasMessageContaining("Refresh token expirado");

        verify(refreshTokenRepositoryPort).deleteByToken("some-token");
    }

    @Test
    @DisplayName("Deve verificar e retornar o token através do verifyAndGet")
    void deveVerificarERetornarNoVerifyAndGet() {
        Usuario usuario = new Usuario(1L, "admin", "hash", "admin@email.com");
        RefreshToken token = new RefreshToken(1L, "valid-token", usuario, Instant.now().plusSeconds(3600));

        when(refreshTokenRepositoryPort.findByToken("valid-token")).thenReturn(Optional.of(token));

        RefreshToken result = service.verifyAndGet("valid-token");

        assertThat(result).isEqualTo(token);
    }

    @Test
    @DisplayName("Deve lançar exceção no verifyAndGet se o token não for encontrado")
    void deveLancarExcecaoSeTokenNaoEncontrado() {
        when(refreshTokenRepositoryPort.findByToken("invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.verifyAndGet("invalid"))
            .isInstanceOf(RefreshTokenException.class)
            .hasMessageContaining("Refresh token inválido ou não encontrado");
    }

    @Test
    @DisplayName("Deve deletar token com sucesso")
    void deveDeletarToken() {
        service.deleteByToken("some-token");
        verify(refreshTokenRepositoryPort).deleteByToken("some-token");
    }
}
