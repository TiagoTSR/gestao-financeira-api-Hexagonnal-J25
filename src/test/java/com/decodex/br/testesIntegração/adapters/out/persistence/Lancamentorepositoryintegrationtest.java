package com.decodex.br.testesIntegração.adapters.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import com.decodex.br.adapters.out.persistence.entity.CategoriaEntity;
import com.decodex.br.adapters.out.persistence.entity.EnderecoEmbeddable;
import com.decodex.br.adapters.out.persistence.entity.LancamentoEntity;
import com.decodex.br.adapters.out.persistence.entity.PessoaEntity;
import com.decodex.br.adapters.out.persistence.repository.CategoriaRepository;
import com.decodex.br.adapters.out.persistence.repository.LancamentoRepository;
import com.decodex.br.adapters.out.persistence.repository.PessoaRepository;
import com.decodex.br.domain.model.TipoLancamento;
import com.decodex.br.testesIntegração.PostgresIntegrationBase;

import jakarta.validation.ConstraintViolationException;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Integração - LancamentoRepository")
class LancamentoRepositoryIntegrationTest extends PostgresIntegrationBase {

    @Autowired private LancamentoRepository lancamentoRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private PessoaRepository pessoaRepository;

    private CategoriaEntity categoria;
    private PessoaEntity pessoa;

    @BeforeEach
    void setUp() {
        lancamentoRepository.deleteAll();
        pessoaRepository.deleteAll();
        categoriaRepository.deleteAll();

        CategoriaEntity cat = new CategoriaEntity();
        cat.setNome("Alimentação");
        categoria = categoriaRepository.save(cat);

        PessoaEntity pes = new PessoaEntity();
        pes.setNome("João Silva");
        pes.setAtivo(true);
        pes.setEndereco(enderecoEmbeddable());
        pessoa = pessoaRepository.save(pes);
    }

    // -------------------------------------------------------------------------
    // SAVE
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve salvar lançamento com FK de categoria e pessoa válidas")
    void save_DevePersistirComRelacionamentos() {
        LancamentoEntity entity = lancamentoEntity(
            "Salário", new BigDecimal("6500.00"), TipoLancamento.RECEITA
        );

        LancamentoEntity saved = lancamentoRepository.save(entity);

        assertThat(saved.getId()).isNotNull().isPositive();
        assertThat(saved.getDescricao()).isEqualTo("Salário");
        assertThat(saved.getValor()).isEqualByComparingTo("6500.00");
        assertThat(saved.getTipo()).isEqualTo(TipoLancamento.RECEITA);
        assertThat(saved.getCategoria().getId()).isEqualTo(categoria.getId());
        assertThat(saved.getPessoa().getId()).isEqualTo(pessoa.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar lançamento sem descrição (NOT NULL)")
    void save_DeveRejeitarDescricaoNula() {
        LancamentoEntity entity = lancamentoEntity(null, BigDecimal.TEN, TipoLancamento.DESPESA);

        assertThatThrownBy(() -> lancamentoRepository.saveAndFlush(entity))
            .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar lançamento sem categoria (FK NOT NULL)")
    // Este teste valida exatamente o erro categoria_id que você teve
    void save_DeveRejeitarCategoriaNula() {
        LancamentoEntity entity = lancamentoEntity("Sem Categoria", BigDecimal.TEN, TipoLancamento.DESPESA);
        entity.setCategoria(null);

        assertThatThrownBy(() -> lancamentoRepository.saveAndFlush(entity))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar lançamento sem pessoa (FK NOT NULL)")
    void save_DeveRejeitarPessoaNula() {
        LancamentoEntity entity = lancamentoEntity("Sem Pessoa", BigDecimal.TEN, TipoLancamento.DESPESA);
        entity.setPessoa(null);

        assertThatThrownBy(() -> lancamentoRepository.saveAndFlush(entity))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    // -------------------------------------------------------------------------
    // FIND BY ID — valida JOIN com categoria e pessoa
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve buscar lançamento por ID com categoria e pessoa carregados")
    void findById_DeveRetornarLancamentoComRelacionamentos() {
        LancamentoEntity saved = lancamentoRepository.save(
            lancamentoEntity("Cinema", new BigDecimal("50.00"), TipoLancamento.DESPESA)
        );

        Optional<LancamentoEntity> found = lancamentoRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getDescricao()).isEqualTo("Cinema");
        assertThat(found.get().getCategoria()).isNotNull();
        assertThat(found.get().getCategoria().getNome()).isEqualTo("Alimentação");
        assertThat(found.get().getPessoa()).isNotNull();
        assertThat(found.get().getPessoa().getNome()).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("Deve retornar Optional vazio para ID inexistente")
    void findById_DeveRetornarVazioQuandoNaoExiste() {
        Optional<LancamentoEntity> found = lancamentoRepository.findById(999L);

        assertThat(found).isEmpty();
    }

    // -------------------------------------------------------------------------
    // FIND ALL
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve listar todos os lançamentos com seus relacionamentos")
    void findAll_DeveRetornarTodosComRelacionamentos() {
        lancamentoRepository.save(lancamentoEntity("Salário", new BigDecimal("6500.00"), TipoLancamento.RECEITA));
        lancamentoRepository.save(lancamentoEntity("Aluguel", new BigDecimal("1200.00"), TipoLancamento.DESPESA));

        List<LancamentoEntity> result = lancamentoRepository.findAll();

        assertThat(result).hasSize(2)
            .extracting(LancamentoEntity::getDescricao)
            .containsExactlyInAnyOrder("Salário", "Aluguel");

        // garante que os JOINs funcionam — esse era o bug categoria_id
        result.forEach(l -> {
            assertThat(l.getCategoria()).isNotNull();
            assertThat(l.getPessoa()).isNotNull();
        });
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve atualizar valor e descrição do lançamento")
    void save_DeveAtualizarLancamento() {
        LancamentoEntity saved = lancamentoRepository.save(
            lancamentoEntity("Original", new BigDecimal("100.00"), TipoLancamento.DESPESA)
        );
        saved.setDescricao("Atualizado");
        saved.setValor(new BigDecimal("200.00"));

        LancamentoEntity updated = lancamentoRepository.saveAndFlush(saved);

        assertThat(updated.getDescricao()).isEqualTo("Atualizado");
        assertThat(updated.getValor()).isEqualByComparingTo("200.00");
        assertThat(updated.getId()).isEqualTo(saved.getId());
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve deletar lançamento sem afetar categoria e pessoa")
    void deleteById_DeveRemoverApenasLancamento() {
        LancamentoEntity saved = lancamentoRepository.save(
            lancamentoEntity("A Deletar", BigDecimal.TEN, TipoLancamento.DESPESA)
        );
        Long lancamentoId = saved.getId();

        lancamentoRepository.deleteById(lancamentoId);

        assertThat(lancamentoRepository.findById(lancamentoId)).isEmpty();
        // FK inversa: deletar lançamento NÃO deve deletar categoria/pessoa
        assertThat(categoriaRepository.findById(categoria.getId())).isPresent();
        assertThat(pessoaRepository.findById(pessoa.getId())).isPresent();
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private LancamentoEntity lancamentoEntity(String descricao, BigDecimal valor, TipoLancamento tipo) {
        LancamentoEntity entity = new LancamentoEntity();
        entity.setDescricao(descricao);
        entity.setDataVencimento(LocalDate.of(2025, 6, 10));
        entity.setValor(valor);
        entity.setTipo(tipo);
        entity.setCategoria(categoria);
        entity.setPessoa(pessoa);
        return entity;
    }

    private EnderecoEmbeddable enderecoEmbeddable() {
        EnderecoEmbeddable e = new EnderecoEmbeddable();
        e.setLogradouro("Rua das Flores");
        e.setNumero("10");
        e.setBairro("Centro");
        e.setCep("01000-000");
        e.setCidade("São Paulo");
        e.setEstado("SP");
        return e;
    }
}