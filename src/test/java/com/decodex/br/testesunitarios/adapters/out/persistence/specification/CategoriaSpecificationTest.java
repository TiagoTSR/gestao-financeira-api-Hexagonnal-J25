package com.decodex.br.testesunitarios.adapters.out.persistence.specification;

import com.decodex.br.adapters.out.persistence.entity.CategoriaEntity;
import com.decodex.br.adapters.out.persistence.specification.CategoriaSpecification;
import com.decodex.br.application.dto.categoria.CategoriaFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários para CategoriaSpecification")
class CategoriaSpecificationTest {

    @Mock
    private Root<CategoriaEntity> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Path<Object> idPath;

    @Mock
    private Path<String> nomePath;

    @Mock
    private Expression<String> lowerNomeExpr;

    @Mock
    private Predicate idPredicate;

    @Mock
    private Predicate nomePredicate;

    @Mock
    private Predicate conjunctionPredicate;

    @Test
    @DisplayName("Deve retornar conjunção quando o filtro for nulo")
    void deveRetornarConjuncaoQuandoFiltroForNulo() {
        when(cb.conjunction()).thenReturn(conjunctionPredicate);

        Specification<CategoriaEntity> spec = CategoriaSpecification.fromFilter(null);
        Predicate resultado = spec.toPredicate(root, query, cb);

        assertThat(resultado).isSameAs(conjunctionPredicate);
        verify(cb).conjunction();
    }

    @Test
    @DisplayName("Deve aplicar escape correto com caractere '\\' para filtros com LIKE")
    void deveAplicarEscapeCorretoParaNome() {
        CategoriaFilter filter = new CategoriaFilter();
        filter.setNome("10%_Promoção");

        when(cb.and(any(Predicate[].class))).thenReturn(nomePredicate);
        when(root.<String>get("nome")).thenReturn(nomePath);
        when(cb.lower(nomePath)).thenReturn(lowerNomeExpr);
        when(cb.like(eq(lowerNomeExpr), eq("%10\\%\\_promoção%"), eq('\\'))).thenReturn(nomePredicate);

        Specification<CategoriaEntity> spec = CategoriaSpecification.fromFilter(filter);
        Predicate resultado = spec.toPredicate(root, query, cb);

        assertThat(resultado).isSameAs(nomePredicate);
        verify(cb).like(lowerNomeExpr, "%10\\%\\_promoção%", '\\');
    }

    @Test
    @DisplayName("Deve filtrar por id quando informado")
    void deveFiltrarPorId() {
        UUID id = UUID.randomUUID();
        CategoriaFilter filter = new CategoriaFilter();
        filter.setId(id);

        when(cb.and(any(Predicate[].class))).thenReturn(idPredicate);
        when(root.get("id")).thenReturn(idPath);
        when(cb.equal(idPath, id)).thenReturn(idPredicate);

        Specification<CategoriaEntity> spec = CategoriaSpecification.comFiltro(filter);
        Predicate resultado = spec.toPredicate(root, query, cb);

        assertThat(resultado).isSameAs(idPredicate);
        verify(cb).equal(idPath, id);
    }
}
