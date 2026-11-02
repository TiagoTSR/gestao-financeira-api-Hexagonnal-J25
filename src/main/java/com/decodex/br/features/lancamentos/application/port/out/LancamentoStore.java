package com.decodex.br.features.lancamentos.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.features.lancamentos.application.model.LancamentoFilterQuery;
import com.decodex.br.features.lancamentos.application.model.LancamentoHistoryItem;

public interface LancamentoStore {

    Lancamento save(Lancamento lancamento);

    Optional<Lancamento> findById(UUID id);

    PageResult<Lancamento> findAll(LancamentoFilterQuery filter, PageRequest request);

    void deleteById(UUID id);

    List<LancamentoHistoryItem> findHistory(UUID id);
}
