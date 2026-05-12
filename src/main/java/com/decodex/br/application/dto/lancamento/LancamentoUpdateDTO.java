package com.decodex.br.application.dto.lancamento;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.model.TipoLancamento;

public record LancamentoUpdateDTO(
		
		String descricao,

	    LocalDate dataVencimento,

	    LocalDate dataPagamento,

	    BigDecimal valor,

	    String observacao,

	    TipoLancamento tipo,

	    Categoria categoria,

	    Pessoa pessoa
) {}
