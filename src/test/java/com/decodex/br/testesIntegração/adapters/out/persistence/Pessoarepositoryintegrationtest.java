package com.decodex.br.testesIntegração.adapters.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import com.decodex.br.adapters.out.persistence.entity.EnderecoEmbeddable;
import com.decodex.br.adapters.out.persistence.entity.PessoaEntity;
import com.decodex.br.adapters.out.persistence.repository.PessoaRepository;
import com.decodex.br.testesIntegração.PostgresIntegrationBase;

import jakarta.validation.ConstraintViolationException;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Integração - PessoaRepository")
class PessoaRepositoryIntegrationTest extends PostgresIntegrationBase {

    @Autowired
    private PessoaRepository repository;

    @BeforeEach
    void limpar() {
        repository.deleteAll();
    }

    // -------------------------------------------------------------------------
    // SAVE
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve salvar pessoa com endereço embeddable e gerar ID")
    void save_DevePersistirComEnderecoEGerarId() {
        PessoaEntity entity = pessoaEntity("João Silva", true);

        PessoaEntity saved = repository.save(entity);

        assertThat(saved.getId()).isNotNull().isPositive();
        assertThat(saved.getNome()).isEqualTo("João Silva");
        assertThat(saved.getAtivo()).isTrue();
        assertThat(saved.getEndereco().getLogradouro()).isEqualTo("Rua das Flores");
        assertThat(saved.getEndereco().getCidade()).isEqualTo("São Paulo");
        assertThat(saved.getEndereco().getEstado()).isEqualTo("SP");
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar pessoa com nome nulo (constraint NOT NULL)")
    void save_DeveRejeitarNomeNulo() {
        PessoaEntity entity = pessoaEntity(null, true);

        assertThatThrownBy(() -> repository.saveAndFlush(entity))
            .isInstanceOf(ConstraintViolationException.class); // <-- troque
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar pessoa sem campo ativo (constraint NOT NULL)")
    void save_DeveRejeitarAtivoNulo() {
        PessoaEntity entity = new PessoaEntity();
        entity.setNome("Sem Ativo");
        entity.setEndereco(enderecoEmbeddable());
        // ativo = null

        assertThatThrownBy(() -> repository.saveAndFlush(entity))
            .isInstanceOf(ConstraintViolationException.class); // <-- troque
    }

    // -------------------------------------------------------------------------
    // FIND BY ID
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve buscar pessoa por ID existente com endereço completo")
    void findById_DeveRetornarPessoaComEndereco() {
        PessoaEntity saved = repository.save(pessoaEntity("Maria Rita", true));

        Optional<PessoaEntity> found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getNome()).isEqualTo("Maria Rita");
        assertThat(found.get().getEndereco().getBairro()).isEqualTo("Centro");
        assertThat(found.get().getEndereco().getCep()).isEqualTo("01000-000");
    }

    @Test
    @DisplayName("Deve retornar Optional vazio para ID inexistente")
    void findById_DeveRetornarVazioQuandoNaoExiste() {
        Optional<PessoaEntity> found = repository.findById(999L);

        assertThat(found).isEmpty();
    }

    // -------------------------------------------------------------------------
    // FIND ALL
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve listar todas as pessoas salvas")
    void findAll_DeveRetornarTodas() {
        repository.save(pessoaEntity("João", true));
        repository.save(pessoaEntity("Maria", false));
        repository.save(pessoaEntity("Pedro", true));

        List<PessoaEntity> result = repository.findAll();

        assertThat(result).hasSize(3)
            .extracting(PessoaEntity::getNome)
            .containsExactlyInAnyOrder("João", "Maria", "Pedro");
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve atualizar dados da pessoa")
    void save_DeveAtualizarPessoa() {
        PessoaEntity saved = repository.save(pessoaEntity("Nome Antigo", true));
        saved.setNome("Nome Atualizado");
        saved.setAtivo(false);

        PessoaEntity updated = repository.saveAndFlush(saved);

        assertThat(updated.getNome()).isEqualTo("Nome Atualizado");
        assertThat(updated.getAtivo()).isFalse();
        assertThat(updated.getId()).isEqualTo(saved.getId());
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve deletar pessoa por ID")
    void deleteById_DeveRemoverDoBanco() {
        PessoaEntity saved = repository.save(pessoaEntity("A Deletar", true));

        repository.deleteById(saved.getId());

        assertThat(repository.findById(saved.getId())).isEmpty();
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private PessoaEntity pessoaEntity(String nome, Boolean ativo) {
        PessoaEntity entity = new PessoaEntity();
        entity.setNome(nome);
        entity.setAtivo(ativo);
        entity.setEndereco(enderecoEmbeddable());
        return entity;
    }

    private EnderecoEmbeddable enderecoEmbeddable() {
        EnderecoEmbeddable e = new EnderecoEmbeddable();
        e.setLogradouro("Rua das Flores");
        e.setNumero("10");
        e.setComplemento(null);
        e.setBairro("Centro");
        e.setCep("01000-000");
        e.setCidade("São Paulo");
        e.setEstado("SP");
        return e;
    }
}