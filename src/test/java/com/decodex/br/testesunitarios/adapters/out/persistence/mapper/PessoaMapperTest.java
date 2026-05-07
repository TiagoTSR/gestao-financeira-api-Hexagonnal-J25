package com.decodex.br.testesunitarios.adapters.out.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.adapters.out.persistence.entity.PessoaEntity;
import com.decodex.br.adapters.out.persistence.mapper.PessoaMapper;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Pessoa;

@DisplayName("Testes unitários para PessoaMapper")
class PessoaMapperTest {

    private final PessoaMapper mapper = new PessoaMapper();

    private final Endereco endereco = new Endereco(
            "Rua X", "123", "Apto", "Centro", "12345-678", "São Paulo", "SP"
    );
    private final Pessoa pessoa = new Pessoa(1L, "Maria", endereco, true);

    @Test
    @DisplayName("Deve converter Entity para Domain corretamente")
    void toDomain() {
        PessoaEntity entity = new PessoaEntity();
        entity.setId(1L);
        entity.setNome("Maria");
        entity.setEndereco(endereco);
        entity.setAtivo(true);

        Pessoa result = mapper.toDomain(entity);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNome()).isEqualTo("Maria");
        assertThat(result.getEndereco()).isEqualTo(endereco);
        assertThat(result.getAtivo()).isTrue();
    }

    @Test
    @DisplayName("Deve converter Domain para Entity corretamente")
    void toEntity() {
        PessoaEntity entity = mapper.toEntity(pessoa);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getNome()).isEqualTo("Maria");
        assertThat(entity.getEndereco()).isEqualTo(endereco);
        assertThat(entity.getAtivo()).isTrue();
    }

    @Test
    @DisplayName("Deve lidar com endereco nulo")
    void toEntityWithNullEndereco() {
        Pessoa pessoaSemEndereco = new Pessoa(2L, "José", null, false);
        PessoaEntity entity = mapper.toEntity(pessoaSemEndereco);

        assertThat(entity.getEndereco()).isNull();
        assertThat(entity.getAtivo()).isFalse();
    }
}