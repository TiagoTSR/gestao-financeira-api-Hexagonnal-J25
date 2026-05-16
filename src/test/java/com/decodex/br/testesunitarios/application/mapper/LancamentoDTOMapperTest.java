package com.decodex.br.testesunitarios.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.decodex.br.application.dto.lancamento.LancamentoCreateDTO;
import com.decodex.br.application.dto.lancamento.LancamentoResponseDTO;
import com.decodex.br.application.dto.lancamento.LancamentoUpdateDTO;
import com.decodex.br.application.mapper.LancamentoDTOMapper;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.model.TipoLancamento;

class LancamentoDTOMapperTest {

    private final Endereco endereco = new Endereco(
        "Rua Teste", "10", null, "Centro", "00000-000", "São Paulo", "SP"
    );
    private final Categoria categoria = new Categoria(1L, "Categoria Teste");
    private final Pessoa pessoa = new Pessoa(1L, "Pessoa Teste", endereco, true);

    //  toDomain(CreateDTO)

    @Test
    void toDomain_CreateDTO_ShouldConvertToLancamento() {
        LancamentoCreateDTO dto = new LancamentoCreateDTO(
            "Descrição Teste",
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2025, 1, 2),
            new BigDecimal("100.50"),
            "Observação",
            TipoLancamento.RECEITA,
            1L, 1L
        );

        Lancamento lancamento = LancamentoDTOMapper.toDomain(dto, categoria, pessoa);

        assertThat(lancamento.getId()).isNull();
        assertThat(lancamento.getDescricao()).isEqualTo("Descrição Teste");
        assertThat(lancamento.getDataVencimento()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(lancamento.getDataPagamento()).isEqualTo(LocalDate.of(2025, 1, 2));
        assertThat(lancamento.getValor()).isEqualByComparingTo("100.50");
        assertThat(lancamento.getObservacao()).isEqualTo("Observação");
        assertThat(lancamento.getTipo()).isEqualTo(TipoLancamento.RECEITA);
        assertThat(lancamento.getCategoria()).isSameAs(categoria);
        assertThat(lancamento.getPessoa()).isSameAs(pessoa);
    }

    @Test
    void toDomain_CreateDTO_ShouldReturnNull_WhenDTOIsNull() {
        Lancamento lancamento = LancamentoDTOMapper.toDomain((LancamentoCreateDTO) null, categoria, pessoa);

        assertThat(lancamento).isNull();
    }

    //  toDomain(UpdateDTO)

    @Test
    void toDomain_UpdateDTO_ShouldReturnLancamentoComNovosDados() {
        LancamentoUpdateDTO dto = new LancamentoUpdateDTO(
            "Nova Descrição",
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 1, 2),
            new BigDecimal("200.00"),
            "Nova Observação",
            TipoLancamento.RECEITA,
            2L, 2L
        );

        Categoria novaCategoria = new Categoria(2L, "Nova Categoria");
        Endereco novoEndereco = new Endereco("Rua Nova", "20", null, "Bairro Novo", "11111-111", "Rio", "RJ");
        Pessoa novaPessoa = new Pessoa(2L, "Nova Pessoa", novoEndereco, false);

        Lancamento novosDados = LancamentoDTOMapper.toDomain(dto, novaCategoria, novaPessoa);

        assertThat(novosDados.getId()).isNull();
        assertThat(novosDados.getDescricao()).isEqualTo("Nova Descrição");
        assertThat(novosDados.getCategoria()).isSameAs(novaCategoria);
        assertThat(novosDados.getPessoa()).isSameAs(novaPessoa);
    }

    @Test
    void toDomain_UpdateDTO_ShouldReturnNull_WhenDTOIsNull() {
        Lancamento lancamento = LancamentoDTOMapper.toDomain((LancamentoUpdateDTO) null, categoria, pessoa);

        assertThat(lancamento).isNull();
    }

    // fluxo de update completo

    @Test
    void update_ShouldAtualizarLancamento_QuandoToDomainEAtualizarCamposCombinados() {
        Lancamento existing = new Lancamento(
            1L, "Descrição Antiga", LocalDate.now(), null,
            BigDecimal.ZERO, "", TipoLancamento.DESPESA, categoria, pessoa
        );

        Categoria novaCategoria = new Categoria(2L, "Nova Categoria");
        Endereco novoEndereco = new Endereco("Rua Nova", "20", null, "Bairro Novo", "11111-111", "Rio", "RJ");
        Pessoa novaPessoa = new Pessoa(2L, "Nova Pessoa", novoEndereco, false);

        LancamentoUpdateDTO dto = new LancamentoUpdateDTO(
            "Nova Descrição",
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 1, 2),
            new BigDecimal("200.00"),
            "Nova Observação",
            TipoLancamento.RECEITA,
            2L, 2L
        );

        Lancamento novosDados = LancamentoDTOMapper.toDomain(dto, novaCategoria, novaPessoa);
        existing.atualizarCampos(novosDados);

        assertThat(existing.getId()).isEqualTo(1L); // id preservado
        assertThat(existing.getDescricao()).isEqualTo("Nova Descrição");
        assertThat(existing.getDataVencimento()).isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(existing.getDataPagamento()).isEqualTo(LocalDate.of(2026, 1, 2));
        assertThat(existing.getValor()).isEqualByComparingTo("200.00");
        assertThat(existing.getTipo()).isEqualTo(TipoLancamento.RECEITA);
        assertThat(existing.getCategoria()).isSameAs(novaCategoria);
        assertThat(existing.getPessoa()).isSameAs(novaPessoa);
    }

    // toDTO

    @Test
    void toDTO_ShouldConvertLancamentoToResponseDTO() {
        Endereco enderecoDTO = new Endereco("Rua DTO", "30", null, "Bairro DTO", "22222-222", "Curitiba", "PR");
        Categoria cat = new Categoria(3L, "Categoria DTO");
        Pessoa pes = new Pessoa(3L, "Pessoa DTO", enderecoDTO, true);

        Lancamento lancamento = new Lancamento(
            5L, "Descrição", LocalDate.of(2025, 5, 5),
            LocalDate.of(2025, 5, 6), new BigDecimal("99.99"),
            "Obs", TipoLancamento.DESPESA, cat, pes
        );

        LancamentoResponseDTO responseDTO = LancamentoDTOMapper.toDTO(lancamento);

        assertThat(responseDTO.id()).isEqualTo(5L);
        assertThat(responseDTO.descricao()).isEqualTo("Descrição");
        assertThat(responseDTO.dataVencimento()).isEqualTo(LocalDate.of(2025, 5, 5));
        assertThat(responseDTO.dataPagamento()).isEqualTo(LocalDate.of(2025, 5, 6));
        assertThat(responseDTO.valor()).isEqualByComparingTo("99.99");
        assertThat(responseDTO.observacao()).isEqualTo("Obs");
        assertThat(responseDTO.tipo()).isEqualTo(TipoLancamento.DESPESA);
        assertThat(responseDTO.categoria().id()).isEqualTo(3L);
        assertThat(responseDTO.categoria().nome()).isEqualTo("Categoria DTO");
        assertThat(responseDTO.pessoa().id()).isEqualTo(3L);
        assertThat(responseDTO.pessoa().nome()).isEqualTo("Pessoa DTO");
        assertThat(responseDTO.pessoa().logradouro()).isEqualTo("Rua DTO");
        assertThat(responseDTO.pessoa().cidade()).isEqualTo("Curitiba");
        assertThat(responseDTO.pessoa().estado()).isEqualTo("PR");
        assertThat(responseDTO.pessoa().ativo()).isTrue();
    }

    @Test
    void toDTO_ShouldReturnNull_WhenLancamentoIsNull() {
        LancamentoResponseDTO responseDTO = LancamentoDTOMapper.toDTO(null);

        assertThat(responseDTO).isNull();
    }

 // invariante do domínio

    @Test
    void lancamentoConstructor_ShouldThrow_WhenCategoriaIsNull() {
        assertThatThrownBy(() -> new Lancamento(
            null, "Descrição", LocalDate.now(), null,
            BigDecimal.ONE, null, TipoLancamento.DESPESA, null, pessoa
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Categoria");
    }

    @Test
    void lancamentoConstructor_ShouldThrow_WhenPessoaIsNull() {
        assertThatThrownBy(() -> new Lancamento(
            null, "Descrição", LocalDate.now(), null,
            BigDecimal.ONE, null, TipoLancamento.DESPESA, categoria, null
        ))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Pessoa");
    }
}