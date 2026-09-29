package com.decodex.br.testesunitarios.adapters.out.persistence.specification;

import com.decodex.br.adapters.out.persistence.entity.CategoriaEntity;
import com.decodex.br.adapters.out.persistence.entity.LancamentoEntity;
import com.decodex.br.adapters.out.persistence.entity.PessoaEntity;
import com.decodex.br.adapters.out.persistence.specification.LancamentoSpecification;
import com.decodex.br.application.dto.lancamento.LancamentoFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários para LancamentoSpecification")
class LancamentoSpecificationTest {

    @Mock
    private Root<LancamentoEntity> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Path<String> descricaoPath;

    @Mock
    private Expression<String> lowerDescricaoExpr;

    @Mock
    private Join<LancamentoEntity, CategoriaEntity> categoriaJoin;

    @Mock
    private Path<String> nomeCategoriaPath;

    @Mock
    private Expression<String> lowerNomeCategoriaExpr;

    @Mock
    private Predicate predicate;

    @Mock
    private Predicate conjunctionPredicate;

    @Test
    @DisplayName("Deve retornar conjunção quando filtro for nulo")
    void deveRetornarConjuncaoQuandoFiltroForNulo() {
        when(cb.conjunction()).thenReturn(conjunctionPredicate);

        Specification<LancamentoEntity> spec = LancamentoSpecification.fromFilter(null);
        Predicate resultado = spec.toPredicate(root, query, cb);

        assertThat(resultado).isSameAs(conjunctionPredicate);
    }

    @Test
    @DisplayName("Deve escapar caracteres especiais em descrição e joins de categoria e pessoa")
    void deveEscaparCaracteresEmDescricaoECategoria() {
        LancamentoFilter filter = new LancamentoFilter();
        filter.setDescricao("Pagamento 100%");
        filter.setNomeCategoria("Alim_Extra");

        when(cb.and(any(Predicate[].class))).thenReturn(predicate);
        when(root.<String>get("descricao")).thenReturn(descricaoPath);
        when(cb.lower(descricaoPath)).thenReturn(lowerDescricaoExpr);
        when(cb.like(eq(lowerDescricaoExpr), eq("%pagamento 100\\%%"), eq('\\'))).thenReturn(predicate);

        when(root.<LancamentoEntity, CategoriaEntity>join("categoria", JoinType.INNER)).thenReturn(categoriaJoin);
        when(categoriaJoin.<String>get("nome")).thenReturn(nomeCategoriaPath);
        when(cb.lower(nomeCategoriaPath)).thenReturn(lowerNomeCategoriaExpr);
        when(cb.like(eq(lowerNomeCategoriaExpr), eq("%alim\\_extra%"), eq('\\'))).thenReturn(predicate);

        Specification<LancamentoEntity> spec = LancamentoSpecification.comFiltro(filter);
        Predicate resultado = spec.toPredicate(root, query, cb);

        assertThat(resultado).isSameAs(predicate);
        verify(cb).like(lowerDescricaoExpr, "%pagamento 100\\%%", '\\');
        verify(cb).like(lowerNomeCategoriaExpr, "%alim\\_extra%", '\\');
    }
}
