package com.decodex.br.adapters.out.persistence.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.decodex.br.adapters.out.persistence.entity.CategoriaEntity;
import com.decodex.br.application.dto.categoria.CategoriaFilter;

import jakarta.persistence.criteria.Predicate;

public final class CategoriaSpecification {

    private CategoriaSpecification() {
    }

    public static Specification<CategoriaEntity> comFiltro(CategoriaFilter filter) {
        return fromFilter(filter);
    }

    public static Specification<CategoriaEntity> fromFilter(CategoriaFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getId() != null) {
                predicates.add(cb.equal(root.get("id"), filter.getId()));
            }

            if (filter.getNome() != null && !filter.getNome().isBlank()) {
                predicates.add(cb.like(
                    cb.lower(root.get("nome")),
                    "%" + escaparLike(filter.getNome().trim().toLowerCase()) + "%",
                    '\\'
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static String escaparLike(String valor) {
        return SpecificationHelper.escaparLike(valor);
    }
}