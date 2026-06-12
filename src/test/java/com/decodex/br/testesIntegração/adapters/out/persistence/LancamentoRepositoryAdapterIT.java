package com.decodex.br.testesIntegração.adapters.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.decodex.br.adapters.out.persistence.adapter.LancamentoRepositoryAdapter;
import com.decodex.br.adapters.out.persistence.entity.CategoriaEntity;
import com.decodex.br.adapters.out.persistence.entity.EnderecoEmbeddable;
import com.decodex.br.adapters.out.persistence.entity.LancamentoEntity;
import com.decodex.br.adapters.out.persistence.entity.PessoaEntity;
import com.decodex.br.adapters.out.persistence.mapper.LancamentoMapper;
import com.decodex.br.adapters.out.persistence.repository.CategoriaRepository;
import com.decodex.br.adapters.out.persistence.repository.LancamentoRepository;
import com.decodex.br.adapters.out.persistence.repository.PessoaRepository;
import com.decodex.br.domain.filter.LancamentoFilter;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.TipoLancamento;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.testesIntegração.PostgresIntegrationBase;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({LancamentoRepositoryAdapter.class, LancamentoMapper.class})
@DisplayName("Testes de Integração - LancamentoRepositoryAdapter (Testcontainers)")
class LancamentoRepositoryAdapterIT extends PostgresIntegrationBase {

    @Autowired
    private LancamentoRepositoryAdapter adapter;

    @Autowired
    private LancamentoRepository repository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    private CategoriaEntity alimentacao;
    private CategoriaEntity lazer;
    private PessoaEntity tiago;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        categoriaRepository.deleteAll();
        pessoaRepository.deleteAll();

        alimentacao = new CategoriaEntity();
        alimentacao.setNome("Alimentação");
        categoriaRepository.save(alimentacao);

        lazer = new CategoriaEntity();
        lazer.setNome("Lazer");
        categoriaRepository.save(lazer);

        tiago = new PessoaEntity();
        tiago.setNome("Tiago");
        tiago.setAtivo(true);
     
        EnderecoEmbeddable enderecoTiago = new EnderecoEmbeddable();
        enderecoTiago.setLogradouro("Rua das Flores");
        enderecoTiago.setNumero("100");
        enderecoTiago.setComplemento("Apto 201");
        enderecoTiago.setBairro("Centro");
        enderecoTiago.setCep("12345678");
        enderecoTiago.setCidade("São Paulo");
        enderecoTiago.setEstado("SP");
        tiago.setEndereco(enderecoTiago);
        pessoaRepository.save(tiago);

        LancamentoEntity l1 = new LancamentoEntity();
        l1.setDescricao("Almoço de negócios");
        l1.setValor(new BigDecimal("150.00"));
        l1.setTipo(TipoLancamento.DESPESA);
        l1.setDataVencimento(LocalDate.of(2026, 6, 15));
        l1.setCategoria(alimentacao);
        l1.setPessoa(tiago);

        LancamentoEntity l2 = new LancamentoEntity();
        l2.setDescricao("Cinema fim de semana");
        l2.setValor(new BigDecimal("60.00"));
        l2.setTipo(TipoLancamento.DESPESA);
        l2.setDataVencimento(LocalDate.of(2026, 6, 20));
        l2.setCategoria(lazer);
        l2.setPessoa(tiago);

        repository.save(l1);
        repository.save(l2);
    }

    @Test
    @DisplayName("Deve filtrar lançamentos por descrição parcial e tipo")
    void findAll_PorDescricaoETipo_DeveRetornarCorrespondente() {
        LancamentoFilter filtro = new LancamentoFilter();
        filtro.setDescricao("almoço");
        filtro.setTipo(TipoLancamento.DESPESA);
        PageRequest pageRequest = new PageRequest(0, 10);

        PageResult<Lancamento> resultado = adapter.findAll(filtro, pageRequest);

        assertThat(resultado.content()).hasSize(1);
        assertThat(resultado.content().get(0).getDescricao()).isEqualTo("Almoço de negócios");
    }

    @Test
    @DisplayName("Deve filtrar lançamentos pelo nome da categoria usando INNER JOIN")
    void findAll_PorNomeCategoria_DeveExecutarJoinEFiltrar() {
        LancamentoFilter filtro = new LancamentoFilter();
        filtro.setNomeCategoria("lazer");
        PageRequest pageRequest = new PageRequest(0, 10);

        PageResult<Lancamento> resultado = adapter.findAll(filtro, pageRequest);

        assertThat(resultado.content()).hasSize(1);
        assertThat(resultado.content().get(0).getDescricao()).isEqualTo("Cinema fim de semana");
    }

    @Test
    @DisplayName("Deve filtrar lançamentos combinando data de vencimento e valor exato")
    void findAll_PorDataEValor_DeveRetornarCorrespondente() {
        LancamentoFilter filtro = new LancamentoFilter();
        filtro.setDataVencimento(LocalDate.of(2026, 6, 15));
        filtro.setValor(new BigDecimal("150.00"));
        PageRequest pageRequest = new PageRequest(0, 10);

        PageResult<Lancamento> resultado = adapter.findAll(filtro, pageRequest);

        assertThat(resultado.content()).hasSize(1);
        assertThat(resultado.content().get(0).getDescricao()).isEqualTo("Almoço de negócios");
    }
}