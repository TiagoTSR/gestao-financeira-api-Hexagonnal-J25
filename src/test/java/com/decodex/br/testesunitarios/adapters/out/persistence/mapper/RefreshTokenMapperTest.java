package com.decodex.br.testesunitarios.adapters.out.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.decodex.br.adapters.out.persistence.entity.RefreshTokenEntity;
import com.decodex.br.adapters.out.persistence.entity.UsuarioEntity;
import com.decodex.br.adapters.out.persistence.mapper.RefreshTokenMapper;
import com.decodex.br.adapters.out.persistence.mapper.RefreshTokenMapperImpl;
import com.decodex.br.adapters.out.persistence.mapper.UsuarioMapper;
import com.decodex.br.domain.model.RefreshToken;
import com.decodex.br.domain.model.Usuario;

@DisplayName("Testes unitários para RefreshTokenMapper")
class RefreshTokenMapperTest {

    private final RefreshTokenMapper mapper = new RefreshTokenMapperImpl(UsuarioMapper.INSTANCE);

    @Test
    @DisplayName("Deve converter RefreshTokenEntity para RefreshToken (Dominio) corretamente")
    void deveConverterParaDomain() {
        UUID userId = UUID.randomUUID();
        UsuarioEntity userEntity = new UsuarioEntity();
        userEntity.setId(userId);
        userEntity.setUsername("admin");
        userEntity.setPassword("hash");
        userEntity.setEmail("admin@email.com");

        UUID tokenId = UUID.randomUUID();
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setId(tokenId);
        entity.setToken("some-uuid-token");
        entity.setUsuario(userEntity);
        entity.setExpiryDate(Instant.parse("2026-08-11T12:00:00Z"));

        RefreshToken domain = mapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(tokenId);
        assertThat(domain.getToken()).isEqualTo("some-uuid-token");
        assertThat(domain.getUsuario().getUsername()).isEqualTo("admin");
        assertThat(domain.getExpiryDate()).isEqualTo("2026-08-11T12:00:00Z");
    }

    @Test
    @DisplayName("Deve retornar null ao converter RefreshTokenEntity nulo para dominio")
    void deveRetornarNullParaDomainNulo() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    @DisplayName("Deve converter RefreshToken (Dominio) para RefreshTokenEntity corretamente")
    void deveConverterParaEntity() {
        UUID userId = UUID.randomUUID();
        Usuario userDomain = new Usuario(userId, "admin", "hash", "admin@email.com");
        UUID tokenId = UUID.randomUUID();
        RefreshToken domain = new RefreshToken(tokenId, "another-token", userDomain, Instant.parse("2026-08-11T13:00:00Z"));

        RefreshTokenEntity entity = mapper.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(tokenId);
        assertThat(entity.getToken()).isEqualTo("another-token");
        assertThat(entity.getUsuario().getUsername()).isEqualTo("admin");
        assertThat(entity.getExpiryDate()).isEqualTo("2026-08-11T13:00:00Z");
    }

    @Test
    @DisplayName("Deve retornar null ao converter RefreshToken nulo para entity")
    void deveRetornarNullParaEntityNulo() {
        assertThat(mapper.toEntity(null)).isNull();
    }
}
