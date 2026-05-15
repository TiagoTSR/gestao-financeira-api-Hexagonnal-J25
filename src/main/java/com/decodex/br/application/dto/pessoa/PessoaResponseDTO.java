package com.decodex.br.application.dto.pessoa;

public record PessoaResponseDTO(
	
		Long id,
	    String nome,
	    String logradouro,
	    String numero,
	    String complemento,
	    String bairro,
	    String cep,
	    String cidade,
	    String estado,
	    Boolean ativo

) {}
