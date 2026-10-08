package com.decodex.br.domain.port.in;

import com.decodex.br.application.dto.lancamento.LancamentoCreateDTO;
import com.decodex.br.application.dto.lancamento.LancamentoFilter;
import com.decodex.br.application.dto.lancamento.LancamentoUpdateDTO;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;

public interface LancamentoInputPort {
	
	PageResult<Lancamento> findAll(LancamentoFilter filter, PageRequest pageRequest);

    Lancamento findById(Long id);

    Lancamento create(LancamentoCreateDTO dto);

    Lancamento create(Lancamento lancamento);

    Lancamento update(Long id, LancamentoUpdateDTO dto);

    Lancamento update(Long id, Lancamento lancamento);

    void delete(Long id);

}
