package com.decodex.br.testesunitarios.adapters.out.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.decodex.br.adapters.out.persistence.entity.UsuarioEntity;
import com.decodex.br.adapters.out.persistence.mapper.UsuarioMapper;
import com.decodex.br.domain.model.Usuario;

@DisplayName("Testes unitários para UsuarioMapper")
class UsuarioMapperTest {

    private final UsuarioMapper mapper = new UsuarioMapper();

    @Test
    @DisplayName("Deve converter UsuarioEntity para Usuario (Dominio) corretamente")
    void deveConverterParaDomain() {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(1L);
        entity.setUsername("usuario1");
        entity.setPassword("$2a$10$xyz");
        entity.setEmail("usuario1@email.com");

        Usuario domain = mapper.toDomain(entity);

        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(1L);
        assertThat(domain.getUsername()).isEqualTo("usuario1");
        assertThat(domain.getPassword()).isEqualTo("$2a$10$xyz");
        assertThat(domain.getEmail()).isEqualTo("usuario1@email.com");
    }

    @Test
    @DisplayName("Deve retornar null ao converter UsuarioEntity nulo para dominio")
    void deveRetornarNullParaDomainNulo() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    @DisplayName("Deve converter Usuario (Dominio) para UsuarioEntity corretamente")
    void deveConverterParaEntity() {
        Usuario domain = new Usuario(2L, "usuario2", "$2a$10$abc", "usuario2@email.com");

        UsuarioEntity entity = mapper.toEntity(domain);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(2L);
        assertThat(entity.getUsername()).isEqualTo("usuario2");
        assertThat(entity.getPassword()).isEqualTo("$2a$10$abc");
        assertThat(entity.getEmail()).isEqualTo("usuario2@email.com");
    }

    @Test
    @DisplayName("Deve retornar null ao converter Usuario nulo para entity")
    void deveRetornarNullParaEntityNulo() {
        assertThat(mapper.toEntity(null)).isNull();
    }
}
