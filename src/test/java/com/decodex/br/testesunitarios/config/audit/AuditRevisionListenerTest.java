package com.decodex.br.testesunitarios.config.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.decodex.br.config.audit.AuditRevisionEntity;
import com.decodex.br.config.audit.AuditRevisionListener;
import com.decodex.br.config.multitenancy.TenantContext;

@DisplayName("Testes unitários para AuditRevisionListener e AuditRevisionEntity")
class AuditRevisionListenerTest {

    private AuditRevisionListener listener;

    @BeforeEach
    void setUp() {
        listener = new AuditRevisionListener();
        SecurityContextHolder.clearContext();
        TenantContext.clear();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        TenantContext.clear();
    }

    @Test
    @DisplayName("Deve registrar nome do usuário autenticado e tenant atual na nova revisão")
    void deveRegistrarUsuarioAutenticadoETenant() {
        UUID customTenant = UUID.randomUUID();
        TenantContext.setTenantId(customTenant);

        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("tiago.admin");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        AuditRevisionEntity entity = new AuditRevisionEntity();
        listener.newRevision(entity);

        assertThat(entity.getUsuario()).isEqualTo("tiago.admin");
        assertThat(entity.getTenantId()).isEqualTo(customTenant);
    }

    @Test
    @DisplayName("Deve registrar SISTEMA quando não houver usuário autenticado")
    void deveRegistrarSistemaQuandoNaoHouverUsuario() {
        AuditRevisionEntity entity = new AuditRevisionEntity();
        listener.newRevision(entity);

        assertThat(entity.getUsuario()).isEqualTo("SISTEMA");
        assertThat(entity.getTenantId()).isEqualTo(TenantContext.DEFAULT_TENANT_ID);
    }

    @Test
    @DisplayName("Deve registrar SISTEMA quando usuário for anonymousUser")
    void deveRegistrarSistemaQuandoUsuarioAnonimo() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("anonymousUser");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        AuditRevisionEntity entity = new AuditRevisionEntity();
        listener.newRevision(entity);

        assertThat(entity.getUsuario()).isEqualTo("SISTEMA");
    }

    @Test
    @DisplayName("Deve testar getters e setters de AuditRevisionEntity")
    void deveTestarGettersESetters() {
        AuditRevisionEntity entity = new AuditRevisionEntity();
        entity.setId(10L);
        entity.setTimestamp(1760000000000L);
        entity.setUsuario("admin");
        UUID tenantId = UUID.randomUUID();
        entity.setTenantId(tenantId);

        assertThat(entity.getId()).isEqualTo(10L);
        assertThat(entity.getTimestamp()).isEqualTo(1760000000000L);
        assertThat(entity.getDataRevisao()).isNotNull();
        assertThat(entity.getUsuario()).isEqualTo("admin");
        assertThat(entity.getTenantId()).isEqualTo(tenantId);
    }
}
