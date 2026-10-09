package com.decodex.br.adapters.in.web;

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

import java.util.List;

@RestController
public class AuthController implements AuthControllerDoc {

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
        @Lazy UsuarioRepositoryPort usuarioRepositoryPort
    ) {
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
        HttpServletResponse response
    ) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        
        var token = tokenService.generateToken(userDetails);
        var refreshToken = refreshTokenInputPort.create(userDetails.getUsername());
        
        setAuthCookies(response, token, refreshToken.getToken());

        String email = userDetails.getUsername();
        if (usuarioRepositoryPort != null) {
            email = usuarioRepositoryPort.findByUsernameOrEmail(userDetails.getUsername(), userDetails.getUsername())
                    .map(com.decodex.br.domain.model.Usuario::getEmail)
                    .orElse(userDetails.getUsername());
        }

        List<String> permissoes = userDetails.getAuthorities() != null 
                ? userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()
                : List.of("ROLE_USER");
        
        return ResponseEntity.ok(new TokenResponseDTO(
            token,
            token,
            refreshToken.getToken(),
            "Bearer",
            900L,
            userDetails.getUsername(),
            email,
            permissoes
        ));
    }

    @PostMapping(value = {"/auth/refresh", "/refresh"})
    public ResponseEntity<TokenResponseDTO> refresh(
        @RequestBody(required = false) RefreshRequestDTO body,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        String tokenStr = null;
        if (body != null && body.refreshToken() != null && !body.refreshToken().isBlank()) {
            tokenStr = body.refreshToken();
        } else {
            var cookie = WebUtils.getCookie(request, "refreshToken");
            if (cookie != null) {
                tokenStr = cookie.getValue();
            }
        }

        if (tokenStr == null || tokenStr.isBlank()) {
            throw new RefreshTokenException("Refresh token ausente. Por favor, faça login novamente.");
        }

        RefreshToken validRefreshToken = refreshTokenInputPort.verifyAndGet(tokenStr);
        String username = validRefreshToken.getUsuario().getUsername();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        String newAccessToken = tokenService.generateToken(userDetails);
        RefreshToken newRefreshToken = refreshTokenInputPort.create(username);

        setAuthCookies(response, newAccessToken, newRefreshToken.getToken());

        String email = username;
        if (usuarioRepositoryPort != null) {
            email = usuarioRepositoryPort.findByUsernameOrEmail(username, username)
                    .map(com.decodex.br.domain.model.Usuario::getEmail)
                    .orElse(username);
        }

        List<String> permissoes = userDetails.getAuthorities() != null 
                ? userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()
                : List.of("ROLE_USER");

        return ResponseEntity.ok(new TokenResponseDTO(
            newAccessToken,
            newAccessToken,
            newRefreshToken.getToken(),
            "Bearer",
            900L,
            username,
            email,
            permissoes
        ));
    }

    @Override
    public ResponseEntity<TokenResponseDTO> refresh(
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        return refresh(null, request, response);
    }

    @Override
    @PostMapping(value = {"/auth/logout", "/logout"})
    public ResponseEntity<Void> logout(
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        var cookie = WebUtils.getCookie(request, "refreshToken");
        if (cookie != null && cookie.getValue() != null && !cookie.getValue().isEmpty()) {
            refreshTokenInputPort.deleteByToken(cookie.getValue());
        }

        clearAuthCookies(response);

        return ResponseEntity.noContent().build();
    }

    private void setAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        var accessCookie = ResponseCookie.from("token", accessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(900) // 15 minutos
                .sameSite("Lax")
                .build();

        var refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(604800) // 7 dias
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    private void clearAuthCookies(HttpServletResponse response) {
        var accessCookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        var refreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}
