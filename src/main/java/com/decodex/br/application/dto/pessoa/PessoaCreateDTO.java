package com.decodex.br.application.dto.pessoa;

import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Pessoa;

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
) {
    public Pessoa toDomain() {
        Endereco endereco = new Endereco(logradouro, numero, complemento, bairro, cep, cidade, estado);
        return new Pessoa(null, nome, endereco, ativo);
    }
}
