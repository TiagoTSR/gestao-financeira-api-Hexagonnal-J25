package com.decodex.br.adapters.out.persistence.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.decodex.br.adapters.out.persistence.entity.PessoaEntity;
import com.decodex.br.application.dto.pessoa.PessoaFilter;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public final class PessoaSpecification {

    private PessoaSpecification() {
    }

    public static Specification<PessoaEntity> comFiltro(PessoaFilter filter) {
        return fromFilter(filter);
    }

    public static Specification<PessoaEntity> fromFilter(PessoaFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getNome() != null && !filter.getNome().isBlank()) {
                predicates.add(cb.like(
                    cb.lower(root.get("nome")),
                    "%" + escaparLike(filter.getNome().trim().toLowerCase()) + "%",
                    '\\'
                ));
            }

            if (filter.getAtivo() != null) {
                predicates.add(cb.equal(root.get("ativo"), filter.getAtivo()));
            }

            Path<Object> enderecoPath = root.get("endereco");

            if (filter.getCidade() != null && !filter.getCidade().isBlank()) {
                predicates.add(cb.like(
                    cb.lower(enderecoPath.get("cidade")),
                    "%" + escaparLike(filter.getCidade().trim().toLowerCase()) + "%",
                    '\\'
                ));
            }

            if (filter.getEstado() != null && !filter.getEstado().isBlank()) {
                predicates.add(cb.like(
                    cb.lower(enderecoPath.get("estado")),
                    "%" + escaparLike(filter.getEstado().trim().toLowerCase()) + "%",
                    '\\'
                ));
            }

            if (filter.getBairro() != null && !filter.getBairro().isBlank()) {
                predicates.add(cb.like(
                    cb.lower(enderecoPath.get("bairro")),
                    "%" + escaparLike(filter.getBairro().trim().toLowerCase()) + "%",
                    '\\'
                ));
            }

            if (filter.getCep() != null && !filter.getCep().isBlank()) {
                predicates.add(cb.equal(
                    enderecoPath.get("cep"),
                    filter.getCep()
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static String escaparLike(String valor) {
        return SpecificationHelper.escaparLike(valor);
    }
}