package com.decodex.br.testesunitarios.features.relatorios;

import com.decodex.br.config.multitenancy.TenantContext;
import com.decodex.br.features.relatorios.application.model.RelatorioRequester;
import com.decodex.br.features.relatorios.infrastructure.context.CurrentRelatorioRequesterAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CurrentRelatorioRequesterAdapterTest {

    private final CurrentRelatorioRequesterAdapter adapter = new CurrentRelatorioRequesterAdapter();

    @AfterEach
    void clearContext() {
        TenantContext.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveObterTenantEUsuarioAutenticadoDaBorda() {
        UUID tenantId = UUID.randomUUID();
        TenantContext.setTenantId(tenantId);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("financeiro@teste.com", "senha", Collections.emptyList()));

        RelatorioRequester requester = adapter.currentRequester();

        assertEquals(tenantId, requester.tenantId());
        assertEquals("financeiro@teste.com", requester.usuario());
    }

    @Test
    void deveUsarUsuarioSistemaQuandoNaoAutenticado() {
        TenantContext.clear();
        SecurityContextHolder.clearContext();

        RelatorioRequester requester = adapter.currentRequester();

        assertEquals(TenantContext.DEFAULT_TENANT_ID, requester.tenantId());
        assertEquals("SISTEMA", requester.usuario());
    }
}
