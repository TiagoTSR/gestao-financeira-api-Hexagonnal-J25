package com.decodex.br.application.mapper;

import com.decodex.br.application.dto.pessoa.PessoaDTO;
import com.decodex.br.domain.model.Pessoa;

public class PessoaResumoMapper {

    public static PessoaDTO.Resumo toDTO(Pessoa pessoa) {
        if (pessoa == null) return null;

        return new PessoaDTO.Resumo(
            pessoa.getId(),
            pessoa.getNome()
        );
    }
}