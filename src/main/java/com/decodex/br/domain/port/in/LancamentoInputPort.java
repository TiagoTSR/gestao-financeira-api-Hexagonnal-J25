package com.decodex.br.domain.port.in;

import java.util.UUID;

import com.decodex.br.application.dto.lancamento.LancamentoCreateDTO;
import com.decodex.br.application.dto.lancamento.LancamentoFilter;
import com.decodex.br.application.dto.lancamento.LancamentoUpdateDTO;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;

public interface LancamentoInputPort {
	
	PageResult<Lancamento> findAll(LancamentoFilter filter, PageRequest pageRequest);

    Lancamento findById(UUID id);

    Lancamento create(LancamentoCreateDTO dto);

    Lancamento create(Lancamento lancamento);

    Lancamento update(UUID id, LancamentoUpdateDTO dto);

    Lancamento update(UUID id, Lancamento lancamento);

    void delete(UUID id);

    Lancamento quitar(UUID id, com.decodex.br.application.dto.lancamento.LancamentoBaixaDTO baixaDTO);

    Lancamento cancelar(UUID id);

    Lancamento reabrir(UUID id);
}
