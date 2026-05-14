package com.decodex.br.application.dto.pessoa;

import com.decodex.br.domain.model.Endereco;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PessoaUpdateDTO(
		
		@NotBlank
	    String nome,

	    Endereco endereco,
	    
	    @NotNull
	    Boolean ativo

) {}

