package com.decodex.br.application.mapper;

import com.decodex.br.application.dto.pessoa.PessoaResumoResponse;
import com.decodex.br.domain.model.Pessoa;

public class PessoaResumoMapper {

    public static PessoaResumoResponse toDTO(Pessoa pessoa) {
        if (pessoa == null) return null;

        return new PessoaResumoResponse(
            pessoa.getId(),
            pessoa.getNome()
        );
    }
}