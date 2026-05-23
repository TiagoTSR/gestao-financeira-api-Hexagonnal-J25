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

import com.decodex.br.adapters.out.persistence.entity.CategoriaEntity;
import com.decodex.br.adapters.out.persistence.repository.CategoriaRepository;
import com.decodex.br.testesIntegração.PostgresIntegrationBase;

import jakarta.validation.ConstraintViolationException;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Integração - CategoriaRepository")
class CategoriaRepositoryIntegrationTest extends PostgresIntegrationBase {

    @Autowired
    private CategoriaRepository repository;

    @BeforeEach
    void limpar() {
        repository.deleteAll();
    }

    // -------------------------------------------------------------------------
    // SAVE
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve salvar categoria e gerar ID automaticamente")
    void save_DevePersistirEGerarId() {
        CategoriaEntity entity = new CategoriaEntity();
        entity.setNome("Alimentação");

        CategoriaEntity saved = repository.save(entity);

        assertThat(saved.getId()).isNotNull().isPositive();
        assertThat(saved.getNome()).isEqualTo("Alimentação");
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar categoria com nome nulo (constraint NOT NULL)")
    void save_DeveRejeitarNomeNulo() {
        CategoriaEntity entity = new CategoriaEntity();
        entity.setNome(null);

        assertThatThrownBy(() -> repository.saveAndFlush(entity))
            .isInstanceOf(ConstraintViolationException.class); // <-- troque aqui
    }

    // -------------------------------------------------------------------------
    // FIND BY ID
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve buscar categoria por ID existente")
    void findById_DeveRetornarCategoria() {
        CategoriaEntity saved = repository.save(categoriaEntity("Lazer"));

        Optional<CategoriaEntity> found = repository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getNome()).isEqualTo("Lazer");
    }

    @Test
    @DisplayName("Deve retornar Optional vazio para ID inexistente")
    void findById_DeveRetornarVazioQuandoNaoExiste() {
        Optional<CategoriaEntity> found = repository.findById(999L);

        assertThat(found).isEmpty();
    }

    // -------------------------------------------------------------------------
    // FIND ALL
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve listar todas as categorias salvas")
    void findAll_DeveRetornarTodas() {
        repository.save(categoriaEntity("Lazer"));
        repository.save(categoriaEntity("Alimentação"));
        repository.save(categoriaEntity("Saúde"));

        List<CategoriaEntity> result = repository.findAll();

        assertThat(result).hasSize(3)
            .extracting(CategoriaEntity::getNome)
            .containsExactlyInAnyOrder("Lazer", "Alimentação", "Saúde");
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há categorias")
    void findAll_DeveRetornarVazioQuandoNaoHaDados() {
        List<CategoriaEntity> result = repository.findAll();

        assertThat(result).isEmpty();
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Deve deletar categoria por ID")
    void deleteById_DeveRemoverDobanco() {
        CategoriaEntity saved = repository.save(categoriaEntity("Farmácia"));

        repository.deleteById(saved.getId());

        assertThat(repository.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("Deve atualizar nome da categoria")
    void save_DeveAtualizarNome() {
        CategoriaEntity saved = repository.save(categoriaEntity("Antigo"));
        saved.setNome("Atualizado");

        CategoriaEntity updated = repository.saveAndFlush(saved);

        assertThat(updated.getNome()).isEqualTo("Atualizado");
        assertThat(updated.getId()).isEqualTo(saved.getId());
    }

    // ── helper ────────────────────────────────────────────────────────────────

    private CategoriaEntity categoriaEntity(String nome) {
        CategoriaEntity entity = new CategoriaEntity();
        entity.setNome(nome);
        return entity;
    }
}