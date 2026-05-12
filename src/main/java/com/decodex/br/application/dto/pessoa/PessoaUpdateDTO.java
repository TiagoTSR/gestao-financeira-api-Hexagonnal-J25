package com.decodex.br.application.dto.pessoa;

import com.decodex.br.domain.model.Endereco;

public record PessoaUpdateDTO(
		
	    String nome,

	    Endereco endereco,

	    Boolean ativo

) {}

