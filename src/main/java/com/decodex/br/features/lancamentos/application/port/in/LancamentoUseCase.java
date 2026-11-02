package com.decodex.br.features.lancamentos.application.port.in;

import java.util.UUID;

import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.features.lancamentos.application.model.LancamentoBaixaCommand;
import com.decodex.br.features.lancamentos.application.model.LancamentoCreateCommand;
import com.decodex.br.features.lancamentos.application.model.LancamentoFilterQuery;
import com.decodex.br.features.lancamentos.application.model.LancamentoHistoryItem;
import com.decodex.br.features.lancamentos.application.model.LancamentoUpdateCommand;

public interface LancamentoUseCase {
	
	PageResult<Lancamento> findAll(LancamentoFilterQuery filter, PageRequest pageRequest);

    Lancamento findById(UUID id);

    Lancamento create(LancamentoCreateCommand command);

    Lancamento create(Lancamento lancamento);

    Lancamento update(UUID id, LancamentoUpdateCommand command);

    Lancamento update(UUID id, Lancamento lancamento);

    void delete(UUID id);

    Lancamento quitar(UUID id, LancamentoBaixaCommand baixaCommand);

    Lancamento cancelar(UUID id);

    Lancamento reabrir(UUID id);

    java.util.List<LancamentoHistoryItem> findHistorico(UUID id);
}
