package com.decodex.br.testesunitarios.config.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.decodex.br.config.ratelimit.RateLimitingFilter;
import com.decodex.br.config.security.JwtAuthenticationFilter;
import com.decodex.br.config.security.SecurityConfig;

@DisplayName("Testes unitários para SecurityConfig")
class SecurityConfigTest {

    private static final String DEFAULT_ORIGINS = "http://localhost:3000,http://localhost:4200,http://localhost:5173,http://localhost:8080,http://127.0.0.1:3000,http://127.0.0.1:4200,http://127.0.0.1:5173,https://gestao-financeira-front-angular.vercel.app";

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private RateLimitingFilter rateLimitingFilter;

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(null, null);
        rateLimitingFilter = new RateLimitingFilter(1, 1);
    }

    @Test
    @DisplayName("Deve inicializar CORS com origens locais padrões e suporte a CSRF header")
    void deveInicializarCorsComOrigensPadrao() {
        SecurityConfig config = new SecurityConfig(jwtAuthenticationFilter, rateLimitingFilter, DEFAULT_ORIGINS, false);
        CorsConfigurationSource source = config.corsConfigurationSource();
        CorsConfiguration corsConfig = source.getCorsConfiguration(new MockHttpServletRequest());

        assertThat(corsConfig).isNotNull();
        assertThat(corsConfig.getAllowedOrigins()).contains(
                "http://localhost:3000",
                "http://localhost:4200",
                "https://gestao-financeira-front-angular.vercel.app");
        assertThat(corsConfig.getAllowCredentials()).isTrue();
        assertThat(corsConfig.getAllowedHeaders()).contains("X-XSRF-TOKEN", "Authorization", "Content-Type");
        assertThat(corsConfig.getExposedHeaders()).contains("X-XSRF-TOKEN", "Set-Cookie");
        assertThat(corsConfig.getAllowedMethods()).contains("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH");
    }

    @Test
    @DisplayName("Deve inicializar CORS com origens customizadas de produção")
    void deveInicializarCorsComOrigensCustomizadas() {
        SecurityConfig config = new SecurityConfig(
                jwtAuthenticationFilter,
                rateLimitingFilter,
                "https://meuapp.com.br, https://admin.meuapp.com.br",
                false
        );
        CorsConfigurationSource source = config.corsConfigurationSource();
        CorsConfiguration corsConfig = source.getCorsConfiguration(new MockHttpServletRequest());

        assertThat(corsConfig).isNotNull();
        assertThat(corsConfig.getAllowedOrigins()).containsExactly("https://meuapp.com.br", "https://admin.meuapp.com.br");
        assertThat(corsConfig.getAllowedOrigins()).doesNotContain("*");
    }

    @Test
    @DisplayName("Deve fornecer repositório de CSRF token configurado")
    void deveFornecerCsrfTokenRepository() {
        SecurityConfig config = new SecurityConfig(jwtAuthenticationFilter, rateLimitingFilter, DEFAULT_ORIGINS, false);
        CsrfTokenRepository repository = config.csrfTokenRepository();

        assertThat(repository).isNotNull();
    }

    @Test
    @DisplayName("Deve configurar cookie CSRF para chamadas cross-origin via HTTPS")
    void deveConfigurarCookieCsrfParaChamadasCrossOrigin() {
        SecurityConfig config = new SecurityConfig(jwtAuthenticationFilter, rateLimitingFilter, DEFAULT_ORIGINS, false);
        CsrfTokenRepository repository = config.csrfTokenRepository();
        MockServletContext servletContext = new MockServletContext();
        servletContext.setMajorVersion(6);
        MockHttpServletRequest request = new MockHttpServletRequest(servletContext);
        request.setSecure(true);
        MockHttpServletResponse response = new MockHttpServletResponse();

        repository.saveToken(repository.generateToken(request), request, response);

        assertThat(response.getHeader("Set-Cookie"))
                .contains("Secure");
    }

    @Test
    @DisplayName("Deve fornecer um PasswordEncoder BCrypt")
    void deveFornecerPasswordEncoderBCrypt() {
        SecurityConfig config = new SecurityConfig(jwtAuthenticationFilter, rateLimitingFilter, DEFAULT_ORIGINS, false);
        PasswordEncoder encoder = config.passwordEncoder();

        assertThat(encoder).isNotNull();
        String hash = encoder.encode("senha123");
        assertThat(encoder.matches("senha123", hash)).isTrue();
    }
}
