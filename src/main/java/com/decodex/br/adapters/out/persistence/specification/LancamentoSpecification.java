package com.decodex.br.adapters.out.persistence.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.decodex.br.adapters.out.persistence.entity.CategoriaEntity;
import com.decodex.br.adapters.out.persistence.entity.LancamentoEntity;
import com.decodex.br.adapters.out.persistence.entity.PessoaEntity;
import com.decodex.br.application.dto.lancamento.LancamentoFilter;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public final class LancamentoSpecification {

    private LancamentoSpecification() {
    }

    public static Specification<LancamentoEntity> comFiltro(LancamentoFilter filter) {
        return fromFilter(filter);
    }

    public static Specification<LancamentoEntity> fromFilter(LancamentoFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            if (filter.getDescricao() != null && !filter.getDescricao().isBlank()) {
                predicates.add(cb.like(
                    cb.lower(root.get("descricao")),
                    "%" + escaparLike(filter.getDescricao().trim().toLowerCase()) + "%",
                    '\\'
                ));
            }

            if (filter.getDataVencimento() != null) {
                predicates.add(cb.equal(root.get("dataVencimento"), filter.getDataVencimento()));
            }

            if (filter.getDataVencimentoDe() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataVencimento"), filter.getDataVencimentoDe()));
            }

            if (filter.getDataVencimentoAte() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dataVencimento"), filter.getDataVencimentoAte()));
            }

            if (filter.getDataPagamento() != null) {
                predicates.add(cb.equal(root.get("dataPagamento"), filter.getDataPagamento()));
            }

            if (filter.getValor() != null) {
                predicates.add(cb.equal(root.get("valor"), filter.getValor()));
            }

            if (filter.getObservacao() != null && !filter.getObservacao().isBlank()) {
                predicates.add(cb.like(
                    cb.lower(root.get("observacao")),
                    "%" + escaparLike(filter.getObservacao().trim().toLowerCase()) + "%",
                    '\\'
                ));
            }

            if (filter.getTipo() != null) {
                predicates.add(cb.equal(root.get("tipo"), filter.getTipo()));
            }

            if (filter.getCategoriaId() != null) {
                predicates.add(cb.equal(root.get("categoria").get("id"), filter.getCategoriaId()));
            }

            if (filter.getPessoaId() != null) {
                predicates.add(cb.equal(root.get("pessoa").get("id"), filter.getPessoaId()));
            }

            if (filter.getNomeCategoria() != null && !filter.getNomeCategoria().isBlank()) {
                Join<LancamentoEntity, CategoriaEntity> categoriaJoin = root.join("categoria", JoinType.INNER);
                predicates.add(cb.like(
                    cb.lower(categoriaJoin.get("nome")),
                    "%" + escaparLike(filter.getNomeCategoria().trim().toLowerCase()) + "%",
                    '\\'
                ));
            }

            if (filter.getNomePessoa() != null && !filter.getNomePessoa().isBlank()) {
                Join<LancamentoEntity, PessoaEntity> pessoaJoin = root.join("pessoa", JoinType.INNER);
                predicates.add(cb.like(
                    cb.lower(pessoaJoin.get("nome")),
                    "%" + escaparLike(filter.getNomePessoa().trim().toLowerCase()) + "%",
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