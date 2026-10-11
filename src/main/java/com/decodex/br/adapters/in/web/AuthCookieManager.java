package com.decodex.br.adapters.in.web;

import com.decodex.br.application.dto.auth.RefreshRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.time.Duration;

@Component
public class AuthCookieManager {

    public static final String COOKIE_ACCESS_TOKEN = "token";
    public static final String COOKIE_REFRESH_TOKEN = "refreshToken";
    public static final Duration ACCESS_TOKEN_MAX_AGE = Duration.ofMinutes(15);
    public static final Duration REFRESH_TOKEN_MAX_AGE = Duration.ofDays(7);

    public void setAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        adicionarCookie(response, COOKIE_ACCESS_TOKEN, accessToken, ACCESS_TOKEN_MAX_AGE);
        adicionarCookie(response, COOKIE_REFRESH_TOKEN, refreshToken, REFRESH_TOKEN_MAX_AGE);
    }

    public void clearAuthCookies(HttpServletResponse response) {
        adicionarCookie(response, COOKIE_ACCESS_TOKEN, "", Duration.ZERO);
        adicionarCookie(response, COOKIE_REFRESH_TOKEN, "", Duration.ZERO);
    }

    public String extrairRefreshToken(RefreshRequestDTO body, HttpServletRequest request) {
        if (body != null && body.refreshToken() != null && !body.refreshToken().isBlank()) {
            return body.refreshToken();
        }
        return extrairRefreshToken(request);
    }

    public String extrairRefreshToken(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        var cookie = WebUtils.getCookie(request, COOKIE_REFRESH_TOKEN);
        return (cookie != null) ? cookie.getValue() : null;
    }

    private void adicionarCookie(HttpServletResponse response, String nome, String valor, Duration maxAge) {
        var cookie = ResponseCookie.from(nome, valor)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(maxAge)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
