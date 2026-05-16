package com.decodex.br.testesunitarios.adapters.out.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.adapters.out.persistence.entity.EnderecoEmbeddable;
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

    private final EnderecoEmbeddable enderecoEmbeddable;
    {
        enderecoEmbeddable = new EnderecoEmbeddable();
        enderecoEmbeddable.setLogradouro("Rua X");
        enderecoEmbeddable.setNumero("123");
        enderecoEmbeddable.setComplemento("Apto");
        enderecoEmbeddable.setBairro("Centro");
        enderecoEmbeddable.setCep("12345-678");
        enderecoEmbeddable.setCidade("São Paulo");
        enderecoEmbeddable.setEstado("SP");
    }

    private final Pessoa pessoa = new Pessoa(1L, "Maria", endereco, true);

    @Test
    @DisplayName("Deve converter Entity para Domain corretamente")
    void toDomain() {
        // given
        PessoaEntity entity = new PessoaEntity();
        entity.setId(1L);
        entity.setNome("Maria");
        entity.setEndereco(enderecoEmbeddable);
        entity.setAtivo(true);

        // when
        Pessoa result = mapper.toDomain(entity);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNome()).isEqualTo("Maria");
        assertThat(result.getEndereco().getLogradouro()).isEqualTo("Rua X");
        assertThat(result.getEndereco().getBairro()).isEqualTo("Centro");   // ✅
        assertThat(result.getEndereco().getCidade()).isEqualTo("São Paulo");
        assertThat(result.getEndereco().getEstado()).isEqualTo("SP");
        assertThat(result.getAtivo()).isTrue();
    }

    @Test
    @DisplayName("Deve converter Domain para Entity corretamente")
    void toEntity() {
        // when
        PessoaEntity entity = mapper.toEntity(pessoa);

        // then
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getNome()).isEqualTo("Maria");
        assertThat(entity.getEndereco().getLogradouro()).isEqualTo("Rua X");
        assertThat(entity.getEndereco().getBairro()).isEqualTo("Centro"); 
        assertThat(entity.getEndereco().getCidade()).isEqualTo("São Paulo");
        assertThat(entity.getEndereco().getEstado()).isEqualTo("SP");
        assertThat(entity.getAtivo()).isTrue();
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar Pessoa com endereço nulo")
    void pessoaComEnderecoNulo_DeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            new Pessoa(2L, "José", null, false)
        );
    }
}