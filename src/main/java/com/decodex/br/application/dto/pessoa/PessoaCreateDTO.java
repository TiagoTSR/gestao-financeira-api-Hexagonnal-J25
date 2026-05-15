package com.decodex.br.application.dto.pessoa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PessoaCreateDTO(
		
		@NotBlank String nome,
	    @NotBlank String logradouro,
	    String numero,        
	    String complemento,
	    @NotBlank String bairro,
	    @NotBlank String cep,
	    @NotBlank String cidade,
	    @NotBlank String estado,
	    @NotNull Boolean ativo
) {}
