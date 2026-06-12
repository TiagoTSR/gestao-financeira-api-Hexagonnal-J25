package com.decodex.br.adapters.out.persistence.specification;

import org.springframework.data.jpa.domain.Specification;

import com.decodex.br.adapters.out.persistence.entity.CategoriaEntity;
import com.decodex.br.domain.filter.CategoriaFilter;

public final class CategoriaSpecification {

    private CategoriaSpecification() {
    }

    @SuppressWarnings("unused")
	public static Specification<CategoriaEntity> fromFilter(CategoriaFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter.getId() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("id"), filter.getId()));
            }

            if (filter.getNome() != null && !filter.getNome().isBlank()) {
                predicates = cb.and(predicates, cb.like(
                    cb.lower(root.get("nome")),
                    "%" + filter.getNome().toLowerCase() + "%"
                ));
            }

            return predicates;
        };
    }
}