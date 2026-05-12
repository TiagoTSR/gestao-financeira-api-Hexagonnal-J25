package com.decodex.br.application.dto.lancamento;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.decodex.br.application.dto.categoria.CategoriaResponseDTO;
import com.decodex.br.application.dto.pessoa.PessoaResponseDTO;
import com.decodex.br.domain.model.TipoLancamento;

public record LancamentoResponseDTO(
		
	    Long id,

	    String descricao,

	    LocalDate dataVencimento,

	    LocalDate dataPagamento,

	    BigDecimal valor,

	    String observacao,

	    TipoLancamento tipo,

	    CategoriaResponseDTO categoria,

	    PessoaResponseDTO  pessoa
) {}
