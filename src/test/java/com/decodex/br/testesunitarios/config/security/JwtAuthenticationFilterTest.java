package com.decodex.br.testesunitarios.config.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import com.decodex.br.config.security.JwtAuthenticationFilter;
import com.decodex.br.config.security.JpaUserDetailsService;
import com.decodex.br.config.security.TokenService;
import java.io.IOException;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários para JwtAuthenticationFilter")
class JwtAuthenticationFilterTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private JpaUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve autenticar com sucesso quando o token válido estiver no cabeçalho Authorization")
    void deveAutenticarComTokenNoCabecalho() throws ServletException, IOException {
        UserDetails userDetails = new User("admin", "password", Collections.emptyList());

        when(request.getHeader("Authorization")).thenReturn("Bearer token-valido-cabecalho");
        when(tokenService.validateToken("token-valido-cabecalho")).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("admin");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve autenticar com sucesso quando o token válido estiver no cookie seguro")
    void deveAutenticarComTokenNoCookie() throws ServletException, IOException {
        UserDetails userDetails = new User("admin", "password", Collections.emptyList());
        Cookie cookie = new Cookie("token", "token-valido-cookie");

        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        when(tokenService.validateToken("token-valido-cookie")).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("admin");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Não deve autenticar se o token estiver ausente tanto no cabeçalho quanto no cookie")
    void naoDeveAutenticarSemToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
}
