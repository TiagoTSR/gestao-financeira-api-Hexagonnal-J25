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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.decodex.br.application.dto.auth.RegisterRequestDTO;
import com.decodex.br.application.dto.auth.UsuarioResponseDTO;
import com.decodex.br.domain.exception.RegraDeNegocioException;
import com.decodex.br.domain.model.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
public class AuthController implements AuthControllerDoc {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RefreshTokenInputPort refreshTokenInputPort;
    private final UserDetailsService userDetailsService;
    private final AuthCookieManager cookieManager;
    private final TokenResponseAssembler tokenResponseAssembler;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(
            AuthenticationManager authenticationManager,
            TokenService tokenService,
            RefreshTokenInputPort refreshTokenInputPort,
            @Lazy UserDetailsService userDetailsService,
            AuthCookieManager cookieManager,
            TokenResponseAssembler tokenResponseAssembler,
            @Lazy UsuarioRepositoryPort usuarioRepositoryPort,
            @Lazy PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.refreshTokenInputPort = refreshTokenInputPort;
        this.userDetailsService = userDetailsService;
        this.cookieManager = cookieManager;
        this.tokenResponseAssembler = tokenResponseAssembler;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthController(
            AuthenticationManager authenticationManager,
            TokenService tokenService,
            RefreshTokenInputPort refreshTokenInputPort,
            @Lazy UserDetailsService userDetailsService,
            @Lazy UsuarioRepositoryPort usuarioRepositoryPort,
            @Lazy PasswordEncoder passwordEncoder) {
        this(authenticationManager, tokenService, refreshTokenInputPort, userDetailsService,
                new AuthCookieManager(), new TokenResponseAssembler(usuarioRepositoryPort),
                usuarioRepositoryPort, passwordEncoder);
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

        cookieManager.setAuthCookies(response, token, refreshToken.getToken());

        return ResponseEntity.ok(tokenResponseAssembler.montar(userDetails, token, refreshToken.getToken()));
    }

    @PostMapping(value = {"/auth/refresh", "/refresh"})
    public ResponseEntity<TokenResponseDTO> refresh(
            @RequestBody(required = false) RefreshRequestDTO body,
            HttpServletRequest request,
            HttpServletResponse response) {

        String tokenStr = cookieManager.extrairRefreshToken(body, request);
        if (tokenStr == null || tokenStr.isBlank()) {
            throw new RefreshTokenException("Refresh token ausente. Por favor, faça login novamente.");
        }

        RefreshToken validRefreshToken = refreshTokenInputPort.verifyAndGet(tokenStr);
        String username = validRefreshToken.getUsuario().getUsername();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        String newAccessToken = tokenService.generateToken(userDetails);
        RefreshToken newRefreshToken = refreshTokenInputPort.create(username);

        cookieManager.setAuthCookies(response, newAccessToken, newRefreshToken.getToken());

        return ResponseEntity.ok(tokenResponseAssembler.montar(userDetails, newAccessToken, newRefreshToken.getToken()));
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

        String tokenStr = cookieManager.extrairRefreshToken(request);
        if (tokenStr != null && !tokenStr.isBlank()) {
            refreshTokenInputPort.deleteByToken(tokenStr);
        }

        cookieManager.clearAuthCookies(response);

        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping(value = {"/auth/register", "/auth/cadastrar"})
    public ResponseEntity<UsuarioResponseDTO> register(
            @RequestBody @Valid RegisterRequestDTO request) {

        if (usuarioRepositoryPort.findByUsername(request.username()).isPresent()) {
            throw new RegraDeNegocioException("Já existe um usuário cadastrado com o username: " + request.username());
        }
        if (usuarioRepositoryPort.findByUsernameOrEmail(request.email(), request.email()).isPresent()) {
            throw new RegraDeNegocioException("Já existe um usuário cadastrado com o e-mail: " + request.email());
        }

        Usuario usuario = new Usuario(
                request.username().trim(),
                passwordEncoder.encode(request.password().trim()),
                request.email().trim().toLowerCase()
        );

        Usuario salvo = usuarioRepositoryPort.save(usuario);

        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .body(new UsuarioResponseDTO(salvo.getId(), salvo.getUsername(), salvo.getEmail()));
    }
}

