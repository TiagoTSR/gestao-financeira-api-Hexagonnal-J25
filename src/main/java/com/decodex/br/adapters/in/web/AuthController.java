package com.decodex.br.adapters.in.web;

import com.decodex.br.adapters.in.web.documentation.AuthControllerDoc;
import com.decodex.br.application.dto.auth.LoginRequestDTO;
import com.decodex.br.application.dto.auth.RefreshRequestDTO;
import com.decodex.br.application.dto.auth.TokenResponseDTO;
import com.decodex.br.config.security.TokenService;
import com.decodex.br.domain.exception.RefreshTokenException;
import com.decodex.br.domain.model.RefreshToken;
import com.decodex.br.domain.port.in.RefreshTokenInputPort;
import com.decodex.br.domain.port.out.UsuarioRepositoryPort;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

import java.time.Duration;
import java.util.List;

@RestController
public class AuthController implements AuthControllerDoc {

    private static final String COOKIE_ACCESS_TOKEN = "token";
    private static final String COOKIE_REFRESH_TOKEN = "refreshToken";
    private static final Duration ACCESS_TOKEN_MAX_AGE = Duration.ofMinutes(15);
    private static final Duration REFRESH_TOKEN_MAX_AGE = Duration.ofDays(7);
    private static final long ACCESS_TOKEN_EXPIRATION_SECONDS = 900L;
    private static final String TOKEN_TYPE = "Bearer";

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RefreshTokenInputPort refreshTokenInputPort;
    private final UserDetailsService userDetailsService;
    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public AuthController(
            AuthenticationManager authenticationManager,
            TokenService tokenService,
            RefreshTokenInputPort refreshTokenInputPort,
            @Lazy UserDetailsService userDetailsService,
            @Lazy UsuarioRepositoryPort usuarioRepositoryPort) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.refreshTokenInputPort = refreshTokenInputPort;
        this.userDetailsService = userDetailsService;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    @Override
    @PostMapping(value = {"/auth/login", "/login"})
    public ResponseEntity<TokenResponseDTO> login(
            @RequestBody @Valid LoginRequestDTO loginRequest,
            HttpServletResponse response) {

        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
        );
        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        String token = tokenService.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenInputPort.create(userDetails.getUsername());

        setAuthCookies(response, token, refreshToken.getToken());

        return ResponseEntity.ok(criarTokenResponse(userDetails, token, refreshToken.getToken()));
    }

    @PostMapping(value = {"/auth/refresh", "/refresh"})
    public ResponseEntity<TokenResponseDTO> refresh(
            @RequestBody(required = false) RefreshRequestDTO body,
            HttpServletRequest request,
            HttpServletResponse response) {

        String tokenStr = extrairRefreshToken(body, request);
        if (tokenStr == null || tokenStr.isBlank()) {
            throw new RefreshTokenException("Refresh token ausente. Por favor, faça login novamente.");
        }

        RefreshToken validRefreshToken = refreshTokenInputPort.verifyAndGet(tokenStr);
        String username = validRefreshToken.getUsuario().getUsername();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        String newAccessToken = tokenService.generateToken(userDetails);
        RefreshToken newRefreshToken = refreshTokenInputPort.create(username);

        setAuthCookies(response, newAccessToken, newRefreshToken.getToken());

        return ResponseEntity.ok(criarTokenResponse(userDetails, newAccessToken, newRefreshToken.getToken()));
    }

    @Override
    public ResponseEntity<TokenResponseDTO> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {
        return refresh(null, request, response);
    }

    @Override
    @PostMapping(value = {"/auth/logout", "/logout"})
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        var cookie = WebUtils.getCookie(request, COOKIE_REFRESH_TOKEN);
        if (cookie != null && cookie.getValue() != null && !cookie.getValue().isEmpty()) {
            refreshTokenInputPort.deleteByToken(cookie.getValue());
        }

        clearAuthCookies(response);

        return ResponseEntity.noContent().build();
    }

    private TokenResponseDTO criarTokenResponse(UserDetails userDetails, String accessToken, String refreshToken) {
        String username = userDetails.getUsername();
        String email = username;

        if (usuarioRepositoryPort != null) {
            email = usuarioRepositoryPort.findByUsernameOrEmail(username, username)
                    .map(com.decodex.br.domain.model.Usuario::getEmail)
                    .orElse(username);
        }

        List<String> permissoes = (userDetails.getAuthorities() != null)
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

    private String extrairRefreshToken(RefreshRequestDTO body, HttpServletRequest request) {
        if (body != null && body.refreshToken() != null && !body.refreshToken().isBlank()) {
            return body.refreshToken();
        }
        var cookie = WebUtils.getCookie(request, COOKIE_REFRESH_TOKEN);
        return (cookie != null) ? cookie.getValue() : null;
    }

    private void setAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        adicionarCookie(response, COOKIE_ACCESS_TOKEN, accessToken, ACCESS_TOKEN_MAX_AGE);
        adicionarCookie(response, COOKIE_REFRESH_TOKEN, refreshToken, REFRESH_TOKEN_MAX_AGE);
    }

    private void clearAuthCookies(HttpServletResponse response) {
        adicionarCookie(response, COOKIE_ACCESS_TOKEN, "", Duration.ZERO);
        adicionarCookie(response, COOKIE_REFRESH_TOKEN, "", Duration.ZERO);
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
