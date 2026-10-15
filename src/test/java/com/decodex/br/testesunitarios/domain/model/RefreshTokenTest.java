package com.decodex.br.testesunitarios.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.domain.model.RefreshToken;
import com.decodex.br.domain.model.Usuario;

@DisplayName("Testes unitários para RefreshToken (modelo de domínio)")
class RefreshTokenTest {

    private final UUID id1 = UUID.randomUUID();
    private final UUID id2 = UUID.randomUUID();
    private final Usuario usuario = new Usuario("admin", "hash12345", "admin@email.com");

    @Test
    @DisplayName("Deve verificar se token está expirado")
    void deveVerificarExpiracao() {
        RefreshToken expirado = new RefreshToken(id1, "tok-1", usuario, Instant.now().minus(1, ChronoUnit.HOURS));
        RefreshToken valido = new RefreshToken(id2, "tok-2", usuario, Instant.now().plus(1, ChronoUnit.HOURS));

        assertThat(expirado.isExpired()).isTrue();
        assertThat(valido.isExpired()).isFalse();
    }

    @Test
    @DisplayName("Deve comparar refresh tokens por ID")
    void deveCompararPorId() {
        Instant now = Instant.now();
        RefreshToken t1 = new RefreshToken(id1, "token-a", usuario, now);
        RefreshToken t2 = new RefreshToken(id1, "token-b", usuario, now);
        RefreshToken t3 = new RefreshToken(id2, "token-a", usuario, now);

        assertThat(t1).isEqualTo(t2);
        assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
        assertThat(t1).isNotEqualTo(t3);
        assertThat(t1).isNotEqualTo(null);
        assertThat(t1).isEqualTo(t1);
    }
}
