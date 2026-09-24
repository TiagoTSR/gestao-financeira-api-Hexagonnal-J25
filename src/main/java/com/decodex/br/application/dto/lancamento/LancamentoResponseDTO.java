package com.decodex.br.application.dto.lancamento;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.decodex.br.application.dto.categoria.CategoriaDTO;
import com.decodex.br.application.dto.pessoa.PessoaResumoResponse;
import com.decodex.br.domain.model.TipoLancamento;

public record LancamentoResponseDTO(
		
	    Long id,

	    String descricao,

	    LocalDate dataVencimento,

	    LocalDate dataPagamento,

	    BigDecimal valor,

	    String observacao,

	    TipoLancamento tipo,

	    CategoriaDTO.Response categoria,

	    PessoaResumoResponse  pessoa
) {}
