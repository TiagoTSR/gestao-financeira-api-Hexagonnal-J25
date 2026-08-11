package com.decodex.br.testesunitarios.adapters.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.decodex.br.adapters.in.web.AuthController;
import com.decodex.br.application.dto.auth.LoginRequestDTO;
import com.decodex.br.application.dto.auth.TokenResponseDTO;
import com.decodex.br.config.security.TokenService;
import com.decodex.br.domain.exeption.RefreshTokenException;
import com.decodex.br.domain.model.RefreshToken;
import com.decodex.br.domain.model.Usuario;
import com.decodex.br.domain.port.in.RefreshTokenUseCase;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários para AuthController")
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private RefreshTokenUseCase refreshTokenUseCase;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private Authentication authentication;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("Deve autenticar com sucesso e retornar token JWT e Cookie Set-Cookie")
    void deveAutenticarERetornarToken() {
        LoginRequestDTO loginRequest = new LoginRequestDTO("admin", "123456");
        UserDetails userDetails = new User("admin", "password", Collections.emptyList());
        Usuario userDomain = new Usuario(1L, "admin", "hash", "admin@email.com");
        RefreshToken refreshToken = new RefreshToken(1L, "mocked-refresh-token", userDomain, Instant.now().plusSeconds(3600));

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(tokenService.generateToken(userDetails)).thenReturn("mocked-jwt-token");
        when(refreshTokenUseCase.create("admin")).thenReturn(refreshToken);

        ResponseEntity<TokenResponseDTO> responseEntity = authController.login(loginRequest, response);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().token()).isEqualTo("mocked-jwt-token");

        verify(response, times(2)).addHeader(
            eq(org.springframework.http.HttpHeaders.SET_COOKIE),
            anyString()
        );
    }

    @Test
    @DisplayName("Deve rotacionar o refresh token e retornar novo token de acesso no refresh")
    void deveRotacionarTokensNoRefresh() {
        Cookie cookie = new Cookie("refreshToken", "valid-refresh-token");
        Usuario userDomain = new Usuario(1L, "admin", "hash", "admin@email.com");
        RefreshToken oldRefreshToken = new RefreshToken(1L, "valid-refresh-token", userDomain, Instant.now().plusSeconds(3600));
        RefreshToken newRefreshToken = new RefreshToken(2L, "new-refresh-token", userDomain, Instant.now().plusSeconds(3600));
        UserDetails userDetails = new User("admin", "password", Collections.emptyList());

        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        when(refreshTokenUseCase.verifyAndGet("valid-refresh-token")).thenReturn(oldRefreshToken);
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(tokenService.generateToken(userDetails)).thenReturn("new-access-token");
        when(refreshTokenUseCase.create("admin")).thenReturn(newRefreshToken);

        ResponseEntity<TokenResponseDTO> responseEntity = authController.refresh(request, response);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().token()).isEqualTo("new-access-token");
        verify(response, times(2)).addHeader(eq(org.springframework.http.HttpHeaders.SET_COOKIE), anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção no refresh quando o cookie do refresh token estiver ausente")
    void deveLancarExcecaoQuandoCookieAusente() {
        when(request.getCookies()).thenReturn(null);

        assertThatThrownBy(() -> authController.refresh(request, response))
            .isInstanceOf(RefreshTokenException.class)
            .hasMessageContaining("Refresh token ausente");
    }

    @Test
    @DisplayName("Deve limpar cookies e deletar token do banco no logout")
    void deveLimparDadosNoLogout() {
        Cookie cookie = new Cookie("refreshToken", "some-refresh-token");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        ResponseEntity<Void> responseEntity = authController.logout(request, response);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(refreshTokenUseCase).deleteByToken("some-refresh-token");
        verify(response, times(2)).addHeader(eq(org.springframework.http.HttpHeaders.SET_COOKIE), anyString());
    }
}
