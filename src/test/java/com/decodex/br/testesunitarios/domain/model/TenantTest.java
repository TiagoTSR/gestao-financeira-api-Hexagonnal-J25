package com.decodex.br.testesunitarios.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.domain.model.Tenant;

@DisplayName("Testes unitários para Tenant (modelo de domínio)")
class TenantTest {

    private final UUID id1 = UUID.randomUUID();
    private final UUID id2 = UUID.randomUUID();

    @Test
    @DisplayName("Deve criar Tenant com valores padrão")
    void deveCriarTenantComValoresPadrao() {
        Tenant tenant = new Tenant("Empresa Alpha", "12.345.678/0001-90");

        assertThat(tenant.getId()).isNull();
        assertThat(tenant.getNome()).isEqualTo("Empresa Alpha");
        assertThat(tenant.getCnpj()).isEqualTo("12.345.678/0001-90");
        assertThat(tenant.getAtivo()).isTrue();
        assertThat(tenant.getDataCriacao()).isNotNull();
    }

    @Test
    @DisplayName("Deve comparar tenants por ID")
    void deveCompararPorId() {
        LocalDateTime now = LocalDateTime.now();
        Tenant t1 = new Tenant(id1, "Empresa 1", "CNPJ 1", true, now);
        Tenant t2 = new Tenant(id1, "Empresa 2", "CNPJ 2", false, now);
        Tenant t3 = new Tenant(id2, "Empresa 1", "CNPJ 1", true, now);

        assertThat(t1).isEqualTo(t2);
        assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
        assertThat(t1).isNotEqualTo(t3);
        assertThat(t1).isNotEqualTo(null);
        assertThat(t1).isEqualTo(t1);
    }
}
