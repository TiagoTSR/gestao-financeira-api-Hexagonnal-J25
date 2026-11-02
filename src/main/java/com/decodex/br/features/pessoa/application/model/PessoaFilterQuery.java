package com.decodex.br.features.pessoa.application.model;

public record PessoaFilterQuery(
        String nome,
        Boolean ativo,
        String cidade,
        String estado,
        String bairro,
        String cep) {
}
