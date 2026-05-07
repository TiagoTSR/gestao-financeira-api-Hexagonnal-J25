package com.decodex.br.testesunitarios.adapters.out.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.adapters.out.persistence.entity.CategoriaEntity;
import com.decodex.br.adapters.out.persistence.mapper.CategoriaMapper;
import com.decodex.br.domain.model.Categoria;

@DisplayName("Testes unitários para CategoriaMapper")
class CategoriaMapperTest {

    private final CategoriaMapper mapper = new CategoriaMapper();

    @Test
    @DisplayName("Deve converter Entity para Domain corretamente")
    void toDomain() {
        CategoriaEntity entity = new CategoriaEntity();
        entity.setId(10L);
        entity.setNome("Moradia");

        Categoria domain = mapper.toDomain(entity);

        assertThat(domain.getId()).isEqualTo(10L);
        assertThat(domain.getNome()).isEqualTo("Moradia");
    }

    @Test
    @DisplayName("Deve converter Domain para Entity corretamente")
    void toEntity() {
        Categoria domain = new Categoria(20L, "Lazer");

        CategoriaEntity entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(20L);
        assertThat(entity.getNome()).isEqualTo("Lazer");
    }

    @Test
    @DisplayName("Deve lidar com valores nulos (domain com id nulo)")
    void toEntityWithNullId() {
        Categoria domain = new Categoria(null, "Saúde");

        CategoriaEntity entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getNome()).isEqualTo("Saúde");
    }
}