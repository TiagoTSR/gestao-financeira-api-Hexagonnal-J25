package com.decodex.br.application.dto.lancamento;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.model.TipoLancamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LancamentoCreateDTO(
		
		@NotBlank
		String descricao,
		
		@NotNull
	    LocalDate dataVencimento,

	    LocalDate dataPagamento,
	    
	    @NotNull
	    BigDecimal valor,

	    String observacao,
	    
	    @NotNull
	    TipoLancamento tipo,
	    
	    @NotNull
	    Categoria categoria,
	    
	    @NotNull
	    Pessoa pessoa
) {}
