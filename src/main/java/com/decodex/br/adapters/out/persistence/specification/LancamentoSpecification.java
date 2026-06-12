package com.decodex.br.adapters.out.persistence.specification;

import org.springframework.data.jpa.domain.Specification;

import com.decodex.br.adapters.out.persistence.entity.CategoriaEntity;
import com.decodex.br.adapters.out.persistence.entity.LancamentoEntity;
import com.decodex.br.adapters.out.persistence.entity.PessoaEntity;
import com.decodex.br.domain.filter.LancamentoFilter;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public final class LancamentoSpecification {

    private LancamentoSpecification() {
    }

    @SuppressWarnings("unused")
	public static Specification<LancamentoEntity> fromFilter(LancamentoFilter filter) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (filter.getDescricao() != null && !filter.getDescricao().isBlank()) {
                predicates = cb.and(predicates, cb.like(
                    cb.lower(root.get("descricao")),
                    "%" + filter.getDescricao().toLowerCase() + "%"
                ));
            }

            if (filter.getDataVencimento() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("dataVencimento"), filter.getDataVencimento()));
            }

            if (filter.getDataPagamento() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("dataPagamento"), filter.getDataPagamento()));
            }

            if (filter.getValor() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("valor"), filter.getValor()));
            }

            if (filter.getObservacao() != null && !filter.getObservacao().isBlank()) {
                predicates = cb.and(predicates, cb.like(
                    cb.lower(root.get("observacao")),
                    "%" + filter.getObservacao().toLowerCase() + "%"
                ));
            }

            if (filter.getTipo() != null) {
                predicates = cb.and(predicates, cb.equal(root.get("tipo"), filter.getTipo()));
            }

            if (filter.getNomeCategoria() != null && !filter.getNomeCategoria().isBlank()) {
                Join<LancamentoEntity, CategoriaEntity> categoriaJoin = root.join("categoria", JoinType.INNER);
                predicates = cb.and(predicates, cb.like(
                    cb.lower(categoriaJoin.get("nome")),
                    "%" + filter.getNomeCategoria().toLowerCase() + "%"
                ));
            }

            if (filter.getNomePessoa() != null && !filter.getNomePessoa().isBlank()) {
                Join<LancamentoEntity, PessoaEntity> pessoaJoin = root.join("pessoa", JoinType.INNER);
                predicates = cb.and(predicates, cb.like(
                    cb.lower(pessoaJoin.get("nome")),
                    "%" + filter.getNomePessoa().toLowerCase() + "%"
                ));
            }

            return predicates;
        };
    }
}