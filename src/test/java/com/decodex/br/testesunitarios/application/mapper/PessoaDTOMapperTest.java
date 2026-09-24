package com.decodex.br.testesunitarios.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import com.decodex.br.application.dto.pessoa.PessoaDTO;
import com.decodex.br.domain.exeption.RegraDeNegocioException;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Pessoa;

class PessoaDTOMapperTest {

    private final Endereco endereco = new Endereco(
        "Rua A", "123", null, "Centro", "01000-000", "São Paulo", "SP"
    );

    // toDomain(Create)

    @Test
    void toDomain_CreateDTO_ShouldReturnPessoaComDadosCorretos() {
        PessoaDTO.Create dto = new PessoaDTO.Create(
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
        PessoaDTO.Update dto = new PessoaDTO.Update(
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
        Pessoa existing = new Pessoa(1L, "Nome Antigo", endereco, false);
        PessoaDTO.Update dto = new PessoaDTO.Update(
            "Novo Nome", "Rua B", "456", null, "Bairro B", "20000-000", "Rio", "RJ", true
        );

        Pessoa novosDados = dto.toDomain();
        existing.atualizarCampos(novosDados);

        assertThat(existing.getId()).isEqualTo(1L); // id preservado
        assertThat(existing.getNome()).isEqualTo("Novo Nome");
        assertThat(existing.getEndereco().getLogradouro()).isEqualTo("Rua B");
        assertThat(existing.getEndereco().getCidade()).isEqualTo("Rio");
        assertThat(existing.getAtivo()).isTrue();
    }

    // from (Response)

    @Test
    void from_ShouldConvertPessoaToResponseDTO() {
        Pessoa pessoa = new Pessoa(2L, "Maria Souza", endereco, false);

        PessoaDTO.Response dto = PessoaDTO.Response.from(pessoa);

        assertThat(dto.id()).isEqualTo(2L);
        assertThat(dto.nome()).isEqualTo("Maria Souza");
        assertThat(dto.logradouro()).isEqualTo("Rua A");
        assertThat(dto.cidade()).isEqualTo("São Paulo");
        assertThat(dto.estado()).isEqualTo("SP");
        assertThat(dto.ativo()).isFalse();
    }

    @Test
    void from_Response_ShouldReturnNull_WhenPessoaIsNull() {
        PessoaDTO.Response dto = PessoaDTO.Response.from(null);

        assertThat(dto).isNull();
    }

    // from (Resumo)

    @Test
    void from_ShouldConvertPessoaToResumoDTO() {
        Pessoa pessoa = new Pessoa(2L, "Maria Souza", endereco, false);

        PessoaDTO.Resumo dto = PessoaDTO.Resumo.from(pessoa);

        assertThat(dto.id()).isEqualTo(2L);
        assertThat(dto.nome()).isEqualTo("Maria Souza");
    }

    @Test
    void from_Resumo_ShouldReturnNull_WhenPessoaIsNull() {
        PessoaDTO.Resumo dto = PessoaDTO.Resumo.from(null);

        assertThat(dto).isNull();
    }

    @Test
    void pessoaConstructor_ShouldThrow_WhenEnderecoIsNull() {
        assertThatThrownBy(() -> new Pessoa(3L, "Sem Endereço", null, true))
            .isInstanceOf(RegraDeNegocioException.class)
            .hasMessageContaining("Endereço não pode ser nulo");
    }
}