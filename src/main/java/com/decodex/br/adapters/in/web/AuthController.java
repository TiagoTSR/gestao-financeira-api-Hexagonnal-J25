package com.decodex.br.adapters.in.web;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;
import com.decodex.br.adapters.in.web.documentation.AuthControllerDoc;
import com.decodex.br.application.dto.auth.LoginRequestDTO;
import com.decodex.br.application.dto.auth.TokenResponseDTO;
import com.decodex.br.config.security.TokenService;
import com.decodex.br.domain.exeption.RefreshTokenException;
import com.decodex.br.domain.model.RefreshToken;
import com.decodex.br.domain.port.in.RefreshTokenUseCase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController implements AuthControllerDoc {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final UserDetailsService userDetailsService;

    public AuthController(
        AuthenticationManager authenticationManager,
        TokenService tokenService,
        RefreshTokenUseCase refreshTokenUseCase,
        @Lazy UserDetailsService userDetailsService
    ) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.userDetailsService = userDetailsService;
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(
        @RequestBody @Valid LoginRequestDTO loginRequest,
        HttpServletResponse response
    ) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        
        var token = tokenService.generateToken(userDetails);
        var refreshToken = refreshTokenUseCase.create(userDetails.getUsername());
        
        setAuthCookies(response, token, refreshToken.getToken());
        
        return ResponseEntity.ok(new TokenResponseDTO(token));
    }

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDTO> refresh(
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        var cookie = WebUtils.getCookie(request, "refreshToken");
        if (cookie == null || cookie.getValue() == null || cookie.getValue().isEmpty()) {
            throw new RefreshTokenException("Refresh token ausente. Por favor, faça login novamente.");
        }

        RefreshToken validRefreshToken = refreshTokenUseCase.verifyAndGet(cookie.getValue());
        String username = validRefreshToken.getUsuario().getUsername();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        String newAccessToken = tokenService.generateToken(userDetails);
        RefreshToken newRefreshToken = refreshTokenUseCase.create(username);

        setAuthCookies(response, newAccessToken, newRefreshToken.getToken());

        return ResponseEntity.ok(new TokenResponseDTO(newAccessToken));
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        var cookie = WebUtils.getCookie(request, "refreshToken");
        if (cookie != null && cookie.getValue() != null && !cookie.getValue().isEmpty()) {
            refreshTokenUseCase.deleteByToken(cookie.getValue());
        }

        clearAuthCookies(response);

        return ResponseEntity.noContent().build();
    }

    private void setAuthCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        var accessCookie = ResponseCookie.from("token", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(900) // 15 minutos
                .sameSite("Lax")
                .build();

        var refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
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
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        var refreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}
