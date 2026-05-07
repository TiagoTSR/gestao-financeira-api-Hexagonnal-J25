package com.decodex.br.testesunitarios.adapters.out.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.adapters.out.persistence.entity.CategoriaEntity;
import com.decodex.br.adapters.out.persistence.entity.LancamentoEntity;
import com.decodex.br.adapters.out.persistence.entity.PessoaEntity;
import com.decodex.br.adapters.out.persistence.mapper.LancamentoMapper;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.model.TipoLancamento;

@DisplayName("Testes unitários para LancamentoMapper com mapeamento aninhado")
class LancamentoMapperTest {

    private LancamentoMapper mapper;

    private CategoriaEntity categoriaEntity;
    private PessoaEntity pessoaEntity;

    private Categoria categoriaDomain;
    private Pessoa pessoaDomain;

    @BeforeEach
    void setUp() {
        mapper = new LancamentoMapper();

        categoriaEntity = new CategoriaEntity();
        categoriaEntity.setId(1L);
        categoriaEntity.setNome("Alimentação");

        categoriaDomain = new Categoria(1L, "Alimentação");

        Endereco endereco = new Endereco(
            "Rua das Flores", "100", "Apto 201", "Centro",
            "12345-678", "São Paulo", "SP"
        );
        pessoaEntity = new PessoaEntity();
        pessoaEntity.setId(2L);
        pessoaEntity.setNome("Maria Silva");
        pessoaEntity.setEndereco(endereco);
        pessoaEntity.setAtivo(true);

        pessoaDomain = new Pessoa(2L, "Maria Silva", endereco, true);
    }

    @Test
    @DisplayName("Deve converter LancamentoEntity para Lancamento (domínio) com categoria e pessoa mapeados")
    void toDomain_ShouldMapAllFieldsIncludingNestedObjects() {
   
        LancamentoEntity entity = new LancamentoEntity();
        entity.setId(100L);
        entity.setDescricao("Compra no supermercado");
        entity.setDataVencimento(LocalDate.of(2025, 5, 20));
        entity.setDataPagamento(LocalDate.of(2025, 5, 18));
        entity.setValor(new BigDecimal("250.75"));
        entity.setObservacao("Pagamento com cartão");
        entity.setTipo(TipoLancamento.DESPESA);
        entity.setCategoria(categoriaEntity);
        entity.setPessoa(pessoaEntity);

        Lancamento domain = mapper.toDomain(entity);

        assertThat(domain.getId()).isEqualTo(100L);
        assertThat(domain.getDescricao()).isEqualTo("Compra no supermercado");
        assertThat(domain.getDataVencimento()).isEqualTo(LocalDate.of(2025, 5, 20));
        assertThat(domain.getDataPagamento()).isEqualTo(LocalDate.of(2025, 5, 18));
        assertThat(domain.getValor()).isEqualTo(new BigDecimal("250.75"));
        assertThat(domain.getObservacao()).isEqualTo("Pagamento com cartão");
        assertThat(domain.getTipo()).isEqualTo(TipoLancamento.DESPESA);

        assertThat(domain.getCategoria()).isEqualTo(categoriaDomain);
        assertThat(domain.getCategoria())
        .usingRecursiveComparison()
        .isEqualTo(categoriaDomain);

        assertThat(domain.getPessoa())
        .usingRecursiveComparison()
        .isEqualTo(pessoaDomain);
    }

    @Test
    @DisplayName("Deve converter Lancamento (domínio) para LancamentoEntity com categoria e pessoa mapeados")
    void toEntity_ShouldMapAllFieldsIncludingNestedObjects() {
   
        Lancamento domain = new Lancamento(
            200L,
            "Salário mensal",
            LocalDate.of(2025, 6, 1),
            LocalDate.of(2025, 5, 30),
            new BigDecimal("5000.00"),
            "Depósito em conta",
            TipoLancamento.RECEITA,
            categoriaDomain,
            pessoaDomain
        );

        LancamentoEntity entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(200L);
        assertThat(entity.getDescricao()).isEqualTo("Salário mensal");
        assertThat(entity.getDataVencimento()).isEqualTo(LocalDate.of(2025, 6, 1));
        assertThat(entity.getDataPagamento()).isEqualTo(LocalDate.of(2025, 5, 30));
        assertThat(entity.getValor()).isEqualTo(new BigDecimal("5000.00"));
        assertThat(entity.getObservacao()).isEqualTo("Depósito em conta");
        assertThat(entity.getTipo()).isEqualTo(TipoLancamento.RECEITA);

        assertThat(entity.getCategoria().getId()).isEqualTo(categoriaEntity.getId());
        assertThat(entity.getCategoria().getNome()).isEqualTo(categoriaEntity.getNome());

        assertThat(entity.getPessoa().getId()).isEqualTo(pessoaEntity.getId());
        assertThat(entity.getPessoa().getNome()).isEqualTo(pessoaEntity.getNome());
        assertThat(entity.getPessoa().getEndereco()).isEqualTo(pessoaEntity.getEndereco());
        assertThat(entity.getPessoa().getAtivo()).isEqualTo(pessoaEntity.getAtivo());
    }

    @Test
    @DisplayName("Deve lançar exceção ao converter LancamentoEntity para domínio quando Categoria for nula")
    void toDomain_WhenCategoriaIsNull_ShouldThrowException() {
 
        LancamentoEntity entity = new LancamentoEntity();
        entity.setId(300L);
        entity.setDescricao("Teste sem categoria");
        entity.setDataVencimento(LocalDate.now());
        entity.setValor(BigDecimal.TEN);
        entity.setTipo(TipoLancamento.DESPESA);
        entity.setCategoria(null); 
        entity.setPessoa(pessoaEntity);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            mapper.toDomain(entity);
        });

        assertThat(exception.getMessage()).isEqualTo("Categoria não pode ser nula");
    }

    @Test
    @DisplayName("Deve lançar exceção ao converter LancamentoEntity para domínio quando Pessoa for nula")
    void toDomain_WhenPessoaIsNull_ShouldThrowException() {

        LancamentoEntity entity = new LancamentoEntity();
        entity.setId(301L);
        entity.setDescricao("Teste sem pessoa");
        entity.setDataVencimento(LocalDate.now());
        entity.setValor(BigDecimal.TEN);
        entity.setTipo(TipoLancamento.DESPESA);
        entity.setCategoria(categoriaEntity);
        entity.setPessoa(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            mapper.toDomain(entity);
        });

        assertThat(exception.getMessage()).isEqualTo("Pessoa não pode ser nula");
    }

    @Test
    @DisplayName("Deve preservar a integridade bidirecional após conversão dupla")
    void shouldBeBidirectionalConsistent() {
   
        Lancamento originalDomain = new Lancamento(
            500L,
            "Freelance",
            LocalDate.of(2025, 7, 15),
            null,
            new BigDecimal("1200.00"),
            "Projeto concluído",
            TipoLancamento.RECEITA,
            categoriaDomain,
            pessoaDomain
        );

        LancamentoEntity entity = mapper.toEntity(originalDomain);
        Lancamento convertedDomain = mapper.toDomain(entity);

        assertThat(convertedDomain)
            .usingRecursiveComparison()
            .isEqualTo(originalDomain);
    }
}