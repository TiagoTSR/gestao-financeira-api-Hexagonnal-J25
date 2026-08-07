package com.decodex.br.testesunitarios.adapters.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
import com.decodex.br.adapters.in.web.AuthController;
import com.decodex.br.application.dto.auth.LoginRequestDTO;
import com.decodex.br.application.dto.auth.TokenResponseDTO;
import com.decodex.br.config.security.TokenService;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários para AuthController")
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("Deve autenticar com sucesso e retornar token JWT")
    void deveAutenticarERetornarToken() {
        LoginRequestDTO request = new LoginRequestDTO("admin", "123456");
        UserDetails userDetails = new User("admin", "password", Collections.emptyList());

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(tokenService.generateToken(userDetails)).thenReturn("mocked-jwt-token");

        ResponseEntity<TokenResponseDTO> response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().token()).isEqualTo("mocked-jwt-token");
    }
}
