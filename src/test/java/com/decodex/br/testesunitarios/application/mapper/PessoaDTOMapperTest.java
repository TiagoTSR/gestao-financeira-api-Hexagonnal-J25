package com.decodex.br.testesunitarios.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import com.decodex.br.application.dto.pessoa.PessoaCreateDTO;
import com.decodex.br.application.dto.pessoa.PessoaResponseDTO;
import com.decodex.br.application.dto.pessoa.PessoaUpdateDTO;
import com.decodex.br.application.mapper.PessoaDTOMapper;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Pessoa;

class PessoaDTOMapperTest {

    private final Endereco endereco = new Endereco(
        "Rua A", "123", null, "Centro", "01000-000", "São Paulo", "SP"
    );

    // toDomain(CreateDTO)

    @Test
    void toDomain_CreateDTO_ShouldReturnPessoaComDadosCorretos() {
        PessoaCreateDTO dto = new PessoaCreateDTO(
            "João Silva", "Rua A", "123", null, "Centro", "01000-000", "São Paulo", "SP", true
        );

        Pessoa pessoa = PessoaDTOMapper.toDomain(dto);

        assertThat(pessoa.getId()).isNull();
        assertThat(pessoa.getNome()).isEqualTo("João Silva");
        assertThat(pessoa.getEndereco().getLogradouro()).isEqualTo("Rua A");
        assertThat(pessoa.getEndereco().getCidade()).isEqualTo("São Paulo");
        assertThat(pessoa.getAtivo()).isTrue();
    }

    @Test
    void toDomain_CreateDTO_ShouldReturnNull_WhenDTOIsNull() {
        Pessoa pessoa = PessoaDTOMapper.toDomain((PessoaCreateDTO) null);

        assertThat(pessoa).isNull();
    }

    // toDomain(UpdateDTO)

    @Test
    void toDomain_UpdateDTO_ShouldReturnPessoaComNovosDados() {
        PessoaUpdateDTO dto = new PessoaUpdateDTO(
            "Novo Nome", "Rua B", "456", null, "Bairro B", "20000-000", "Rio", "RJ", true
        );

        Pessoa novaPessoa = PessoaDTOMapper.toDomain(dto);

        assertThat(novaPessoa.getId()).isNull();
        assertThat(novaPessoa.getNome()).isEqualTo("Novo Nome");
        assertThat(novaPessoa.getEndereco().getLogradouro()).isEqualTo("Rua B");
        assertThat(novaPessoa.getEndereco().getCidade()).isEqualTo("Rio");
        assertThat(novaPessoa.getAtivo()).isTrue();
    }

    @Test
    void toDomain_UpdateDTO_ShouldReturnNull_WhenDTOIsNull() {
        Pessoa pessoa = PessoaDTOMapper.toDomain((PessoaUpdateDTO) null);

        assertThat(pessoa).isNull();
    }

    //fluxo de update completo

    @Test
    void update_ShouldAtualizarPessoa_QuandoToDomainEAtualizarCamposCombinados() {
        // Simula exatamente o que o PessoaService.update() faz
        Pessoa existing = new Pessoa(1L, "Nome Antigo", endereco, false);
        PessoaUpdateDTO dto = new PessoaUpdateDTO(
            "Novo Nome", "Rua B", "456", null, "Bairro B", "20000-000", "Rio", "RJ", true
        );

        Pessoa novosDados = PessoaDTOMapper.toDomain(dto);
        existing.atualizarCampos(novosDados);

        assertThat(existing.getId()).isEqualTo(1L); // id preservado
        assertThat(existing.getNome()).isEqualTo("Novo Nome");
        assertThat(existing.getEndereco().getLogradouro()).isEqualTo("Rua B");
        assertThat(existing.getEndereco().getCidade()).isEqualTo("Rio");
        assertThat(existing.getAtivo()).isTrue();
    }

    // toDTO

    @Test
    void toDTO_ShouldConvertPessoaToResponseDTO() {
        Pessoa pessoa = new Pessoa(2L, "Maria Souza", endereco, false);

        PessoaResponseDTO dto = PessoaDTOMapper.toDTO(pessoa);

        assertThat(dto.id()).isEqualTo(2L);
        assertThat(dto.nome()).isEqualTo("Maria Souza");
        assertThat(dto.logradouro()).isEqualTo("Rua A");
        assertThat(dto.cidade()).isEqualTo("São Paulo");
        assertThat(dto.estado()).isEqualTo("SP");
        assertThat(dto.ativo()).isFalse();
    }

    @Test
    void toDTO_ShouldReturnNull_WhenPessoaIsNull() {
        PessoaResponseDTO dto = PessoaDTOMapper.toDTO(null);

        assertThat(dto).isNull();
    }

    @Test
    void pessoaConstructor_ShouldThrow_WhenEnderecoIsNull() {
        assertThatThrownBy(() -> new Pessoa(3L, "Sem Endereço", null, true))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Endereço não pode ser nulo");
    }
}