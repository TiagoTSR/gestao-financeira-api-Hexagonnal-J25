package com.decodex.br.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final com.decodex.br.config.ratelimit.RateLimitingFilter rateLimitingFilter;
    private final List<String> allowedOrigins;
    private final boolean swaggerEnabled;

    public SecurityConfig(
            @Lazy JwtAuthenticationFilter jwtAuthenticationFilter,
            @Lazy com.decodex.br.config.ratelimit.RateLimitingFilter rateLimitingFilter,
            @Value("${api.security.cors.allowed-origins:http://localhost:3000,http://localhost:4200,http://localhost:5173,http://localhost:8080,http://127.0.0.1:3000,http://127.0.0.1:4200,http://127.0.0.1:5173,https://gestao-financeira-front-angular.vercel.app}") String origins,
            @Value("${springdoc.swagger-ui.enabled:false}") boolean swaggerEnabled) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.rateLimitingFilter = rateLimitingFilter;
        this.swaggerEnabled = swaggerEnabled;
        this.allowedOrigins = Arrays.stream(origins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList();
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository repository = new CookieCsrfTokenRepository();
        repository.setCookiePath("/");
        repository.setCookieCustomizer(cookie -> cookie.sameSite("None").secure(true));
        return repository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        try {
            http
                    .cors(Customizer.withDefaults())
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .exceptionHandling(exceptions -> exceptions
                            .authenticationEntryPoint((request, response, authException) -> {
                                response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                                response.setContentType("application/json");
                                response.setCharacterEncoding("UTF-8");
                                response.getWriter().write(
                                        "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Token ausente, inválido ou expirado.\"}");
                            }))
                    .authorizeHttpRequests(authorize -> {
                        authorize
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/auth/**", "/csrf").permitAll()
                                .requestMatchers(HttpMethod.POST, "/auth/**", "/login").permitAll()
                                .requestMatchers("/relatorios/painel", "/api/relatorios/painel").permitAll();

                        if (swaggerEnabled) {
                            authorize.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                                    .permitAll();
                        }

                        authorize.anyRequest().authenticated();
                    })
                    .addFilterBefore(rateLimitingFilter,
                            org.springframework.security.web.session.DisableEncodeUrlFilter.class)
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                    .csrf(csrf -> csrf
                            .csrfTokenRepository(csrfTokenRepository())
                            .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                    .addFilterAfter(new CsrfCookieFilter(), CsrfFilter.class);

            return http.build();
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao configurar a cadeia de filtros de seguranca", e);
        }
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With",
                "X-Tenant-ID",
                "X-XSRF-TOKEN"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of(
                "Authorization",
                "Set-Cookie",
                "X-XSRF-TOKEN",
                "X-Rate-Limit-Limit",
                "X-Rate-Limit-Remaining",
                "X-Rate-Limit-Retry-After-Seconds"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) {
        try {
            return authenticationConfiguration.getAuthenticationManager();
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao obter AuthenticationManager", e);
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
