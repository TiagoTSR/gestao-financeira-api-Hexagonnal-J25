package com.decodex.br.testesunitarios.application.mapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import com.decodex.br.application.dto.pessoa.PessoaCreateDTO;
import com.decodex.br.application.dto.pessoa.PessoaResponseDTO;
import com.decodex.br.application.dto.pessoa.PessoaResumoDTO;
import com.decodex.br.application.dto.pessoa.PessoaUpdateDTO;
import com.decodex.br.domain.exception.RegraDeNegocioException;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Pessoa;

class PessoaDTOMapperTest {

    private final Endereco endereco = new Endereco(
        "Rua A", "123", null, "Centro", "01000-000", "São Paulo", "SP"
    );

    // toDomain(Create)

    @Test
    void toDomain_CreateDTO_ShouldReturnPessoaComDadosCorretos() {
        PessoaCreateDTO dto = new PessoaCreateDTO(
            "João Silva", "Rua A", "123", null, "Centro", "01000-000", "São Paulo", "SP", true
        );

        Pessoa pessoa = dto.toDomain();

        assertThat(pessoa.getId()).isNull();
        assertThat(pessoa.getNome()).isEqualTo("João Silva");
        assertThat(pessoa.getEndereco().getLogradouro()).isEqualTo("Rua A");
        assertThat(pessoa.getEndereco().getCidade()).isEqualTo("São Paulo");
        assertThat(pessoa.getAtivo()).isTrue();
    }

    // toDomain(Update)

    @Test
    void toDomain_UpdateDTO_ShouldReturnPessoaComNovosDados() {
        PessoaUpdateDTO dto = new PessoaUpdateDTO(
            "Novo Nome", "Rua B", "456", null, "Bairro B", "20000-000", "Rio", "RJ", true
        );

        Pessoa novaPessoa = dto.toDomain();

        assertThat(novaPessoa.getId()).isNull();
        assertThat(novaPessoa.getNome()).isEqualTo("Novo Nome");
        assertThat(novaPessoa.getEndereco().getLogradouro()).isEqualTo("Rua B");
        assertThat(novaPessoa.getEndereco().getCidade()).isEqualTo("Rio");
        assertThat(novaPessoa.getAtivo()).isTrue();
    }

    //fluxo de update completo

    @Test
    void update_ShouldAtualizarPessoa_QuandoToDomainEAtualizarCamposCombinados() {
        // Simula exatamente o que o PessoaService.update() faz
        UUID id = UUID.randomUUID();
        Pessoa existing = new Pessoa(id, "Nome Antigo", endereco, false);
        PessoaUpdateDTO dto = new PessoaUpdateDTO(
            "Novo Nome", "Rua B", "456", null, "Bairro B", "20000-000", "Rio", "RJ", true
        );

        Pessoa novosDados = dto.toDomain();
        existing.atualizarCampos(novosDados);

        assertThat(existing.getId()).isEqualTo(id); // id preservado
        assertThat(existing.getNome()).isEqualTo("Novo Nome");
        assertThat(existing.getEndereco().getLogradouro()).isEqualTo("Rua B");
        assertThat(existing.getEndereco().getCidade()).isEqualTo("Rio");
        assertThat(existing.getAtivo()).isTrue();
    }

    // from (Response)

    @Test
    void from_ShouldConvertPessoaToResponseDTO() {
        UUID id = UUID.randomUUID();
        Pessoa pessoa = new Pessoa(id, "Maria Souza", endereco, false);

        PessoaResponseDTO dto = PessoaResponseDTO.from(pessoa);

        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.nome()).isEqualTo("Maria Souza");
        assertThat(dto.logradouro()).isEqualTo("Rua A");
        assertThat(dto.cidade()).isEqualTo("São Paulo");
        assertThat(dto.estado()).isEqualTo("SP");
        assertThat(dto.ativo()).isFalse();
    }

    @Test
    void from_Response_ShouldReturnNull_WhenPessoaIsNull() {
        PessoaResponseDTO dto = PessoaResponseDTO.from(null);

        assertThat(dto).isNull();
    }

    // from (Resumo)

    @Test
    void from_ShouldConvertPessoaToResumoDTO() {
        UUID id = UUID.randomUUID();
        Pessoa pessoa = new Pessoa(id, "Maria Souza", endereco, false);

        PessoaResumoDTO dto = PessoaResumoDTO.from(pessoa);

        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.nome()).isEqualTo("Maria Souza");
    }

    @Test
    void from_Resumo_ShouldReturnNull_WhenPessoaIsNull() {
        PessoaResumoDTO dto = PessoaResumoDTO.from(null);

        assertThat(dto).isNull();
    }

    @Test
    void pessoaConstructor_ShouldThrow_WhenEnderecoIsNull() {
        UUID id = UUID.randomUUID();
        assertThatThrownBy(() -> new Pessoa(id, "Sem Endereço", null, true))
            .isInstanceOf(RegraDeNegocioException.class)
            .hasMessageContaining("Endereço não pode ser nulo");
    }
}