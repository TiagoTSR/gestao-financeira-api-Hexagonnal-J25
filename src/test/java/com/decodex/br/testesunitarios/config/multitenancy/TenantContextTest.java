package com.decodex.br.testesunitarios.config.multitenancy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.config.multitenancy.TenantContext;
import com.decodex.br.config.multitenancy.TenantIdentifierResolver;

@DisplayName("Testes unitários para TenantContext e TenantIdentifierResolver")
class TenantContextTest {

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Deve retornar DEFAULT_TENANT_ID quando nenhum tenant for configurado")
    void deveRetornarDefaultTenantIdQuandoNaoConfigurado() {
        assertThat(TenantContext.getTenantId()).isEqualTo(TenantContext.DEFAULT_TENANT_ID);

        TenantIdentifierResolver resolver = new TenantIdentifierResolver();
        assertThat(resolver.resolveCurrentTenantIdentifier()).isEqualTo(TenantContext.DEFAULT_TENANT_ID);
        assertThat(resolver.validateExistingCurrentSessions()).isTrue();
    }

    @Test
    @DisplayName("Deve definir e recuperar tenant específico no contexto")
    void deveDefinirERecuperarTenantEspecifico() {
        UUID customTenant = UUID.randomUUID();
        TenantContext.setTenantId(customTenant);

        assertThat(TenantContext.getTenantId()).isEqualTo(customTenant);

        TenantIdentifierResolver resolver = new TenantIdentifierResolver();
        assertThat(resolver.resolveCurrentTenantIdentifier()).isEqualTo(customTenant);
    }

    @Test
    @DisplayName("Deve limpar o contexto e voltar para o default")
    void deveLimparContexto() {
        UUID customTenant = UUID.randomUUID();
        TenantContext.setTenantId(customTenant);
        assertThat(TenantContext.getTenantId()).isEqualTo(customTenant);

        TenantContext.clear();
        assertThat(TenantContext.getTenantId()).isEqualTo(TenantContext.DEFAULT_TENANT_ID);
    }

    @Test
    @DisplayName("Deve isolar contextos de tenant entre threads diferentes")
    void deveIsolarContextosEntreThreads() throws InterruptedException {
        UUID tenantThreadPrincipal = UUID.randomUUID();
        UUID tenantOutraThread = UUID.randomUUID();

        TenantContext.setTenantId(tenantThreadPrincipal);

        AtomicReference<UUID> capturadoNaOutraThread = new AtomicReference<>();
        Thread worker = new Thread(() -> {
            TenantContext.setTenantId(tenantOutraThread);
            capturadoNaOutraThread.set(TenantContext.getTenantId());
            TenantContext.clear();
        });

        worker.start();
        worker.join();

        assertThat(capturadoNaOutraThread.get()).isEqualTo(tenantOutraThread);
        assertThat(TenantContext.getTenantId()).isEqualTo(tenantThreadPrincipal);
    }
}
