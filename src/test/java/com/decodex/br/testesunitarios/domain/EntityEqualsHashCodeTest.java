package com.decodex.br.testesunitarios.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.decodex.br.adapters.out.persistence.entity.CategoriaEntity;
import com.decodex.br.adapters.out.persistence.entity.LancamentoEntity;
import com.decodex.br.adapters.out.persistence.entity.PessoaEntity;

@DisplayName("Testes unitários - Equals e HashCode das Entities")
class EntityEqualsHashCodeTest {

    // ─────────────────────────────────────────
    // CategoriaEntity
    // ─────────────────────────────────────────

    @Test
    @DisplayName("CategoriaEntity - objetos com mesmo ID devem ser iguais")
    void categoriaEntity_SameId_ShouldBeEqual() {
        CategoriaEntity c1 = new CategoriaEntity();
        c1.setId(1L);
        c1.setNome("Alimentação");

        CategoriaEntity c2 = new CategoriaEntity();
        c2.setId(1L);
        c2.setNome("Outro nome"); // nome diferente não importa para equals por ID

        assertThat(c1).isEqualTo(c2);
        assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
    }

    @Test
    @DisplayName("CategoriaEntity - objetos com IDs diferentes não devem ser iguais")
    void categoriaEntity_DifferentId_ShouldNotBeEqual() {
        CategoriaEntity c1 = new CategoriaEntity();
        c1.setId(1L);

        CategoriaEntity c2 = new CategoriaEntity();
        c2.setId(2L);

        assertThat(c1).isNotEqualTo(c2);
    }

    @Test
    @DisplayName("CategoriaEntity - ID nulo não deve ser igual a outro com ID")
    void categoriaEntity_NullId_ShouldNotBeEqualToEntityWithId() {
        CategoriaEntity c1 = new CategoriaEntity();
        c1.setId(null);

        CategoriaEntity c2 = new CategoriaEntity();
        c2.setId(1L);

        assertThat(c1).isNotEqualTo(c2);
    }

    @Test
    @DisplayName("CategoriaEntity - mesma instância deve ser igual a si mesma")
    void categoriaEntity_SameInstance_ShouldBeEqual() {
        CategoriaEntity c1 = new CategoriaEntity();
        c1.setId(1L);

        assertThat(c1).isEqualTo(c1);
    }

    @Test
    @DisplayName("CategoriaEntity - não deve ser igual a null")
    void categoriaEntity_ComparedToNull_ShouldNotBeEqual() {
        CategoriaEntity c1 = new CategoriaEntity();
        c1.setId(1L);

        assertThat(c1).isNotEqualTo(null);
    }

    // ─────────────────────────────────────────
    // PessoaEntity
    // ─────────────────────────────────────────

    @Test
    @DisplayName("PessoaEntity - objetos com mesmo ID devem ser iguais")
    void pessoaEntity_SameId_ShouldBeEqual() {
        PessoaEntity p1 = new PessoaEntity();
        p1.setId(1L);
        p1.setNome("João");

        PessoaEntity p2 = new PessoaEntity();
        p2.setId(1L);
        p2.setNome("Maria"); // nome diferente não importa

        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
    }

    @Test
    @DisplayName("PessoaEntity - objetos com IDs diferentes não devem ser iguais")
    void pessoaEntity_DifferentId_ShouldNotBeEqual() {
        PessoaEntity p1 = new PessoaEntity();
        p1.setId(1L);

        PessoaEntity p2 = new PessoaEntity();
        p2.setId(2L);

        assertThat(p1).isNotEqualTo(p2);
    }

    @Test
    @DisplayName("PessoaEntity - ID nulo não deve ser igual a outro com ID")
    void pessoaEntity_NullId_ShouldNotBeEqualToEntityWithId() {
        PessoaEntity p1 = new PessoaEntity();
        p1.setId(null);

        PessoaEntity p2 = new PessoaEntity();
        p2.setId(1L);

        assertThat(p1).isNotEqualTo(p2);
    }

    @Test
    @DisplayName("PessoaEntity - mesma instância deve ser igual a si mesma")
    void pessoaEntity_SameInstance_ShouldBeEqual() {
        PessoaEntity p1 = new PessoaEntity();
        p1.setId(1L);

        assertThat(p1).isEqualTo(p1);
    }

    @Test
    @DisplayName("PessoaEntity - não deve ser igual a null")
    void pessoaEntity_ComparedToNull_ShouldNotBeEqual() {
        PessoaEntity p1 = new PessoaEntity();
        p1.setId(1L);

        assertThat(p1).isNotEqualTo(null);
    }

    // ─────────────────────────────────────────
    // LancamentoEntity
    // ─────────────────────────────────────────

    @Test
    @DisplayName("LancamentoEntity - objetos com mesmo ID devem ser iguais")
    void lancamentoEntity_SameId_ShouldBeEqual() {
        LancamentoEntity l1 = new LancamentoEntity();
        l1.setId(1L);
        l1.setDescricao("Conta de luz");

        LancamentoEntity l2 = new LancamentoEntity();
        l2.setId(1L);
        l2.setDescricao("Outro"); // descrição diferente não importa

        assertThat(l1).isEqualTo(l2);
        assertThat(l1.hashCode()).isEqualTo(l2.hashCode());
    }

    @Test
    @DisplayName("LancamentoEntity - objetos com IDs diferentes não devem ser iguais")
    void lancamentoEntity_DifferentId_ShouldNotBeEqual() {
        LancamentoEntity l1 = new LancamentoEntity();
        l1.setId(1L);

        LancamentoEntity l2 = new LancamentoEntity();
        l2.setId(2L);

        assertThat(l1).isNotEqualTo(l2);
    }

    @Test
    @DisplayName("LancamentoEntity - ID nulo não deve ser igual a outro com ID")
    void lancamentoEntity_NullId_ShouldNotBeEqualToEntityWithId() {
        LancamentoEntity l1 = new LancamentoEntity();
        l1.setId(null);

        LancamentoEntity l2 = new LancamentoEntity();
        l2.setId(1L);

        assertThat(l1).isNotEqualTo(l2);
    }

    @Test
    @DisplayName("LancamentoEntity - mesma instância deve ser igual a si mesma")
    void lancamentoEntity_SameInstance_ShouldBeEqual() {
        LancamentoEntity l1 = new LancamentoEntity();
        l1.setId(1L);

        assertThat(l1).isEqualTo(l1);
    }

    @Test
    @DisplayName("LancamentoEntity - não deve ser igual a null")
    void lancamentoEntity_ComparedToNull_ShouldNotBeEqual() {
        LancamentoEntity l1 = new LancamentoEntity();
        l1.setId(1L);

        assertThat(l1).isNotEqualTo(null);
    }
}