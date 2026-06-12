package com.decodex.br.adapters.out.persistence.specification;

import org.springframework.data.jpa.domain.Specification;

import com.decodex.br.adapters.out.persistence.entity.PessoaEntity;
import com.decodex.br.domain.filter.PessoaFilter;

import jakarta.persistence.criteria.Path;

public final class PessoaSpecification {

    private PessoaSpecification() {
    }

    @SuppressWarnings("unused")
	public static Specification<PessoaEntity> fromFilter(PessoaFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter.getNome() != null && !filter.getNome().isBlank()) {
                predicates = cb.and(predicates, cb.like(
                    cb.lower(root.get("nome")),
                    "%" + filter.getNome().toLowerCase() + "%"
                ));
            }

            if (filter.getAtivo() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("ativo"), filter.getAtivo()));
            }

            Path<Object> enderecoPath = root.get("endereco");

            if (filter.getCidade() != null && !filter.getCidade().isBlank()) {
                predicates = cb.and(predicates, cb.like(
                    cb.lower(enderecoPath.get("cidade")),
                    "%" + filter.getCidade().toLowerCase() + "%"
                ));
            }

            if (filter.getEstado() != null && !filter.getEstado().isBlank()) {
                predicates = cb.and(predicates, cb.like(
                    cb.lower(enderecoPath.get("estado")),
                    "%" + filter.getEstado().toLowerCase() + "%"
                ));
            }

            if (filter.getBairro() != null && !filter.getBairro().isBlank()) {
                predicates = cb.and(predicates, cb.like(
                    cb.lower(enderecoPath.get("bairro")),
                    "%" + filter.getBairro().toLowerCase() + "%"
                ));
            }

            if (filter.getCep() != null && !filter.getCep().isBlank()) {
                predicates = cb.and(predicates, cb.equal(
                    enderecoPath.get("cep"),
                    filter.getCep()
                ));
            }

            return predicates;
        };
    }
}