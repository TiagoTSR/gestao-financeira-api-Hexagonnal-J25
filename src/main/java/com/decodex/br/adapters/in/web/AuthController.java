package com.decodex.br.adapters.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.decodex.br.adapters.in.web.documentation.AuthControllerDoc;
import com.decodex.br.application.dto.auth.LoginRequestDTO;
import com.decodex.br.application.dto.auth.TokenResponseDTO;
import com.decodex.br.config.security.TokenService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController implements AuthControllerDoc {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(
        @RequestBody @Valid LoginRequestDTO loginRequest,
        jakarta.servlet.http.HttpServletResponse response
    ) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        
        var token = tokenService.generateToken((UserDetails) auth.getPrincipal());
        
        var cookie = org.springframework.http.ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7200)
                .sameSite("Lax")
                .build();
                
        response.addHeader(org.springframework.http.HttpHeaders.SET_COOKIE, cookie.toString());
        
        return ResponseEntity.ok(new TokenResponseDTO(token));
    }
}
