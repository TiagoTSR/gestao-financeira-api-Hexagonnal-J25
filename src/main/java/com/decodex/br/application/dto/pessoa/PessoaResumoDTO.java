package com.decodex.br.application.dto.pessoa;

import java.util.UUID;

import com.decodex.br.domain.model.Pessoa;

public record PessoaResumoDTO(
    UUID id,
    String nome
) {
    public static PessoaResumoDTO from(Pessoa pessoa) {
        if (pessoa == null) return null;
        return new PessoaResumoDTO(pessoa.getId(), pessoa.getNome());
    }
}
