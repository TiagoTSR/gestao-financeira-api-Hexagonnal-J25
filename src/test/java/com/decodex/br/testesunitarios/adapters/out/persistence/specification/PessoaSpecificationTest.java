package com.decodex.br.testesunitarios.adapters.out.persistence.specification;

import com.decodex.br.adapters.out.persistence.entity.PessoaEntity;
import com.decodex.br.adapters.out.persistence.specification.PessoaSpecification;
import com.decodex.br.application.dto.pessoa.PessoaFilter;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários para PessoaSpecification")
class PessoaSpecificationTest {

    @Mock
    private Root<PessoaEntity> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Path<String> nomePath;

    @Mock
    private Path<Object> enderecoPath;

    @Mock
    private Path<String> cidadePath;

    @Mock
    private Expression<String> lowerNomeExpr;

    @Mock
    private Expression<String> lowerCidadeExpr;

    @Mock
    private Predicate predicate;

    @Mock
    private Predicate conjunctionPredicate;

    @Test
    @DisplayName("Deve retornar conjunção quando o filtro for nulo")
    void deveRetornarConjuncaoQuandoFiltroForNulo() {
        when(cb.conjunction()).thenReturn(conjunctionPredicate);

        Specification<PessoaEntity> spec = PessoaSpecification.fromFilter(null);
        Predicate resultado = spec.toPredicate(root, query, cb);

        assertThat(resultado).isSameAs(conjunctionPredicate);
    }

    @Test
    @DisplayName("Deve escapar caracteres especiais em nome e campos de endereço")
    void deveEscaparCaracteresEmNomeECidade() {
        PessoaFilter filter = new PessoaFilter();
        filter.setNome("Silva_50%");
        filter.setCidade("São\\Paulo");

        when(cb.and(any(Predicate[].class))).thenReturn(predicate);
        when(root.<String>get("nome")).thenReturn(nomePath);
        when(cb.lower(nomePath)).thenReturn(lowerNomeExpr);
        when(cb.like(eq(lowerNomeExpr), eq("%silva\\_50\\%%"), eq('\\'))).thenReturn(predicate);

        when(root.get("endereco")).thenReturn(enderecoPath);
        when(enderecoPath.<String>get("cidade")).thenReturn(cidadePath);
        when(cb.lower(cidadePath)).thenReturn(lowerCidadeExpr);
        when(cb.like(eq(lowerCidadeExpr), eq("%são\\\\paulo%"), eq('\\'))).thenReturn(predicate);

        Specification<PessoaEntity> spec = PessoaSpecification.comFiltro(filter);
        Predicate resultado = spec.toPredicate(root, query, cb);

        assertThat(resultado).isSameAs(predicate);
        verify(cb).like(lowerNomeExpr, "%silva\\_50\\%%", '\\');
        verify(cb).like(lowerCidadeExpr, "%são\\\\paulo%", '\\');
    }
}
