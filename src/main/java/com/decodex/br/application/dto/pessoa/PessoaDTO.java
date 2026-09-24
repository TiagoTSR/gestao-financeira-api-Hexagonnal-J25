package com.decodex.br.application.dto.pessoa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public sealed interface PessoaDTO {

    record Create(
        @NotBlank String nome,
        @NotBlank String logradouro,
        String numero,
        String complemento,
        @NotBlank String bairro,
        @NotBlank String cep,
        @NotBlank String cidade,
        @NotBlank String estado,
        @NotNull Boolean ativo
    ) implements PessoaDTO {}

    record Update(
        @NotBlank String nome,
        @NotBlank String logradouro,
        String numero,
        String complemento,
        @NotBlank String bairro,
        @NotBlank String cep,
        @NotBlank String cidade,
        @NotBlank String estado,
        @NotNull Boolean ativo
    ) implements PessoaDTO {}

    record Response(
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
    ) implements PessoaDTO {}

    record Resumo(
        Long id,
        String nome
    ) implements PessoaDTO {}
}
