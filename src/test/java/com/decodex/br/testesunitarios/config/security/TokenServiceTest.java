package com.decodex.br.testesunitarios.config.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import com.decodex.br.config.security.TokenService;
import java.util.Collections;

@DisplayName("Testes unitários para TokenService")
class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "T2143Lo67r8SuperSecretKey1234567890!");
    }

    @Test
    @DisplayName("Deve gerar e validar token JWT com sucesso")
    void deveGerarEValidarToken() {
        UserDetails userDetails = new User("admin", "password", Collections.emptyList());

        String token = tokenService.generateToken(userDetails);
        assertThat(token).isNotBlank();

        String username = tokenService.validateToken(token);
        assertThat(username).isEqualTo("admin");
    }

    @Test
    @DisplayName("Deve gerar token com tenant_id customizado e extrair com sucesso")
    void deveGerarTokenComTenantIdCustomizadoEExtrair() {
        java.util.UUID customTenantId = java.util.UUID.randomUUID();
        UserDetails userDetails = new User("gestor", "password", Collections.emptyList());

        String token = tokenService.generateToken(userDetails, customTenantId);
        assertThat(token).isNotBlank();

        java.util.UUID extraido = tokenService.extractTenantId(token);
        assertThat(extraido).isEqualTo(customTenantId);
    }

    @Test
    @DisplayName("Deve extrair DEFAULT_TENANT_ID ao receber token inválido ou nulo")
    void deveExtrairDefaultTenantIdParaTokenInvalido() {
        java.util.UUID extraidoInvalido = tokenService.extractTenantId("token-invalido");
        assertThat(extraidoInvalido).isEqualTo(com.decodex.br.config.multitenancy.TenantContext.DEFAULT_TENANT_ID);

        java.util.UUID extraidoNulo = tokenService.extractTenantId(null);
        assertThat(extraidoNulo).isEqualTo(com.decodex.br.config.multitenancy.TenantContext.DEFAULT_TENANT_ID);
    }
}
