package com.decodex.br.application.dto.pessoa;

import com.decodex.br.domain.model.Endereco;

public record PessoaResponseDTO(
	
    Long id,
	
    String nome,

    Endereco endereco,

    Boolean ativo

) {}
