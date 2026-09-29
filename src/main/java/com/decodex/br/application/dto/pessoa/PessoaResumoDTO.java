package com.decodex.br.application.dto.pessoa;

import com.decodex.br.domain.model.Pessoa;

public record PessoaResumoDTO(
    Long id,
    String nome
) {
    public static PessoaResumoDTO from(Pessoa pessoa) {
        if (pessoa == null) return null;
        return new PessoaResumoDTO(pessoa.getId(), pessoa.getNome());
    }
}
