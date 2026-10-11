package com.decodex.br.testesunitarios.adapters.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import com.decodex.br.adapters.in.web.AuthCookieManager;
import com.decodex.br.application.dto.auth.RefreshRequestDTO;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@DisplayName("Testes unitários para AuthCookieManager")
class AuthCookieManagerTest {

    private AuthCookieManager cookieManager;
    private HttpServletResponse response;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        cookieManager = new AuthCookieManager();
        response = mock(HttpServletResponse.class);
        request = mock(HttpServletRequest.class);
    }

    @Test
    @DisplayName("Deve definir cookies HTTP-Only de acesso e refresh no response")
    void deveDefinirCookiesDeAcessoERefresh() {
        cookieManager.setAuthCookies(response, "access-token-123", "refresh-token-456");

        verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), contains("token=access-token-123"));
        verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), contains("refreshToken=refresh-token-456"));
        verify(response, times(2)).addHeader(eq(HttpHeaders.SET_COOKIE), contains("HttpOnly"));
    }

    @Test
    @DisplayName("Deve limpar cookies HTTP-Only com Max-Age zero")
    void deveLimparCookiesHttpOnly() {
        cookieManager.clearAuthCookies(response);

        verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), contains("token=;"));
        verify(response).addHeader(eq(HttpHeaders.SET_COOKIE), contains("refreshToken=;"));
        verify(response, times(2)).addHeader(eq(HttpHeaders.SET_COOKIE), contains("Max-Age=0"));
    }

    @Test
    @DisplayName("Deve extrair refresh token a partir do DTO de body quando presente")
    void deveExtrairRefreshTokenDoBody() {
        RefreshRequestDTO body = new RefreshRequestDTO("body-refresh-token");

        String token = cookieManager.extrairRefreshToken(body, request);

        assertThat(token).isEqualTo("body-refresh-token");
    }

    @Test
    @DisplayName("Deve extrair refresh token dos cookies quando body for nulo ou vazio")
    void deveExtrairRefreshTokenDosCookiesQuandoBodyAusente() {
        Cookie cookie = new Cookie("refreshToken", "cookie-refresh-token");
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        String tokenComBodyNulo = cookieManager.extrairRefreshToken(null, request);
        String tokenComBodyVazio = cookieManager.extrairRefreshToken(new RefreshRequestDTO("  "), request);

        assertThat(tokenComBodyNulo).isEqualTo("cookie-refresh-token");
        assertThat(tokenComBodyVazio).isEqualTo("cookie-refresh-token");
    }

    @Test
    @DisplayName("Deve retornar nulo quando cookies forem nulos ou request for nulo")
    void deveRetornarNuloQuandoCookiesAusentes() {
        when(request.getCookies()).thenReturn(null);

        assertThat(cookieManager.extrairRefreshToken(request)).isNull();
        assertThat(cookieManager.extrairRefreshToken(null)).isNull();
    }
}
