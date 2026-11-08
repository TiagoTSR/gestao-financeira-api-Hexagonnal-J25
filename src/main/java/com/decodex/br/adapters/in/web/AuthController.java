package com.decodex.br.adapters.in.web;

import com.decodex.br.adapters.in.web.documentation.AuthControllerDoc;
import com.decodex.br.application.dto.auth.LoginRequestDTO;
import com.decodex.br.application.dto.auth.RefreshRequestDTO;
import com.decodex.br.application.dto.auth.TokenResponseDTO;
import com.decodex.br.config.security.TokenService;
import com.decodex.br.domain.exception.RefreshTokenException;
import com.decodex.br.domain.model.RefreshToken;
import com.decodex.br.features.autenticacao.application.model.RegisterUserCommand;
import com.decodex.br.features.autenticacao.application.port.in.RefreshTokenUseCase;
import com.decodex.br.features.autenticacao.application.port.in.RegisterUserUseCase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.decodex.br.application.dto.auth.RegisterRequestDTO;
import com.decodex.br.application.dto.auth.UsuarioResponseDTO;
import com.decodex.br.features.autenticacao.application.model.RegisteredUser;

@RestController
public class AuthController implements AuthControllerDoc {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final UserDetailsService userDetailsService;
    private final AuthCookieManager cookieManager;
    private final TokenResponseAssembler tokenResponseAssembler;
    private final RegisterUserUseCase registerUserUseCase;

    @Autowired
    public AuthController(
            AuthenticationManager authenticationManager,
            TokenService tokenService,
            RefreshTokenUseCase refreshTokenUseCase,
            @Lazy UserDetailsService userDetailsService,
            AuthCookieManager cookieManager,
            TokenResponseAssembler tokenResponseAssembler,
            RegisterUserUseCase registerUserUseCase) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.userDetailsService = userDetailsService;
        this.cookieManager = cookieManager;
        this.tokenResponseAssembler = tokenResponseAssembler;
        this.registerUserUseCase = registerUserUseCase;
    }

    @GetMapping(value = { "/auth/csrf", "/csrf" })
    public ResponseEntity<Void> csrf() {
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping(value = { "/auth/login", "/login" })
    public ResponseEntity<TokenResponseDTO> login(
            @RequestBody @Valid LoginRequestDTO loginRequest,
            HttpServletResponse response) {

        String username = loginRequest.username().trim();
        if (username.contains("@")) {
            username = username.toLowerCase(Locale.ROOT);
        }
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, loginRequest.password().trim()));
        // authenticate() throws AuthenticationException on failure; never returns null
        if (!(auth.getPrincipal() instanceof UserDetails userDetails)) {
            throw new org.springframework.security.authentication.BadCredentialsException("Credenciais inválidas");
        }

        String token = tokenService.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenUseCase.create(userDetails.getUsername());

        cookieManager.setAuthCookies(response, token, refreshToken.getToken());

        return ResponseEntity.ok(tokenResponseAssembler.montar(userDetails, token, refreshToken.getToken()));
    }

    @PostMapping(value = { "/auth/refresh", "/refresh" })
    public ResponseEntity<TokenResponseDTO> refresh(
            @RequestBody(required = false) RefreshRequestDTO body,
            HttpServletRequest request,
            HttpServletResponse response) {

        String tokenStr = cookieManager.extrairRefreshToken(body, request);
        if (tokenStr == null || tokenStr.isBlank()) {
            throw new RefreshTokenException("Refresh token ausente. Por favor, faça login novamente.");
        }

        RefreshToken validRefreshToken = refreshTokenUseCase.verifyAndGet(tokenStr);
        if (validRefreshToken.getUsuario() == null) {
            throw new RefreshTokenException("Usuário associado ao refresh token não encontrado.");
        }
        String username = validRefreshToken.getUsuario().getUsername();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        // loadUserByUsername throws UsernameNotFoundException if not found; never returns null

        String newAccessToken = tokenService.generateToken(userDetails);
        RefreshToken newRefreshToken = refreshTokenUseCase.create(username);

        cookieManager.setAuthCookies(response, newAccessToken, newRefreshToken.getToken());

        return ResponseEntity
                .ok(tokenResponseAssembler.montar(userDetails, newAccessToken, newRefreshToken.getToken()));
    }

    @Override
    public ResponseEntity<TokenResponseDTO> refresh(
            HttpServletRequest request,
            HttpServletResponse response) {
        return refresh(null, request, response);
    }

    @Override
    @PostMapping(value = { "/auth/logout", "/logout" })
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response) {

        String tokenStr = cookieManager.extrairRefreshToken(request);
        if (tokenStr != null && !tokenStr.isBlank()) {
            refreshTokenUseCase.deleteByToken(tokenStr);
        }

        cookieManager.clearAuthCookies(response);

        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping(value = { "/auth/register", "/auth/cadastrar" })
    public ResponseEntity<UsuarioResponseDTO> register(
            @RequestBody @Valid RegisterRequestDTO request) {

        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .body(toResponse(registerUserUseCase.register(new RegisterUserCommand(
                        request.username(),
                        request.password(),
                        request.email()))));
    }

    private UsuarioResponseDTO toResponse(RegisteredUser user) {
        return new UsuarioResponseDTO(user.id(), user.username(), user.email());
    }
}
