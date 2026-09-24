package com.decodex.br.application.mapper;

import com.decodex.br.application.dto.pessoa.PessoaDTO;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Pessoa;

public class PessoaDTOMapper {

    public static Pessoa toDomain(PessoaDTO.Create dto) {
        if (dto == null) return null;

        Endereco endereco = new Endereco(
            dto.logradouro(),
            dto.numero(),
            dto.complemento(),
            dto.bairro(),
            dto.cep(),
            dto.cidade(),
            dto.estado()
        );
        return new Pessoa(
            null,
            dto.nome(),
            endereco,
            dto.ativo()
        );
    }

    public static Pessoa toDomain(PessoaDTO.Update dto) {
        if (dto == null) return null;

        Endereco endereco = new Endereco(
            dto.logradouro(),
            dto.numero(),
            dto.complemento(),
            dto.bairro(),
            dto.cep(),
            dto.cidade(),
            dto.estado()
        );
        return new Pessoa(
            null,
            dto.nome(),
            endereco,
            dto.ativo()
        );
    }

    public static PessoaDTO.Response toDTO(Pessoa p) {
        if (p == null) return null;

        boolean temEndereco = p.getEndereco() != null;

        return new PessoaDTO.Response(
            p.getId(),
            p.getNome(),
            temEndereco ? p.getEndereco().getLogradouro() : null,
            temEndereco ? p.getEndereco().getNumero() : null,
            temEndereco ? p.getEndereco().getComplemento() : null,
            temEndereco ? p.getEndereco().getBairro() : null,
            temEndereco ? p.getEndereco().getCep() : null,
            temEndereco ? p.getEndereco().getCidade() : null,
            temEndereco ? p.getEndereco().getEstado() : null,
            p.getAtivo()
        );
    }
}