package com.decodex.br.adapters.out.persistence;

import com.decodex.br.adapters.out.persistence.entity.LancamentoEntity;
import com.decodex.br.adapters.out.persistence.entity.PessoaEntity;
import com.decodex.br.domain.model.LancamentoEstatisticaPessoa;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.model.TipoLancamento;
import com.decodex.br.domain.port.out.LancamentoEstatisticaPort;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class LancamentoEstatisticaAdapter implements LancamentoEstatisticaPort {

    private final EntityManager manager;

    public LancamentoEstatisticaAdapter(EntityManager manager) {
        this.manager = manager;
    }

    @Override
    public List<LancamentoEstatisticaPessoa> porPessoa(LocalDate inicio, LocalDate fim) {
        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);

        Root<LancamentoEntity> root = query.from(LancamentoEntity.class);

        var joinPessoa = root.join("pessoa");

        query.select(cb.array(
                root.get("tipo"),
                joinPessoa.get("id"),
                joinPessoa.get("nome"),
                cb.sum(root.get("valor"))));

        query.where(criarRestricoesPorData(root, cb, inicio, fim));

        query.groupBy(
                root.get("tipo"),
                joinPessoa.get("id"),
                joinPessoa.get("nome"));

        return manager.createQuery(query)
                .getResultList()
                .stream()
                .map(this::mapearParaDominio)
                .toList();
    }

    private LancamentoEstatisticaPessoa mapearParaDominio(Object[] row) {
        TipoLancamento tipo = (TipoLancamento) row[0];
        Long pessoaId = (Long) row[1];
        String pessoaNome = (String) row[2];
        BigDecimal total = (BigDecimal) row[3];

        Pessoa pessoa = new Pessoa(pessoaId, pessoaNome);

        return new LancamentoEstatisticaPessoa(tipo, pessoa, total);
    }

    private Predicate[] criarRestricoesPorData(Root<LancamentoEntity> root,
            CriteriaBuilder cb, LocalDate inicio, LocalDate fim) {
        return new Predicate[] {
                cb.greaterThanOrEqualTo(root.get("dataVencimento"), inicio),
                cb.lessThanOrEqualTo(root.get("dataVencimento"), fim)
        };
    }
}