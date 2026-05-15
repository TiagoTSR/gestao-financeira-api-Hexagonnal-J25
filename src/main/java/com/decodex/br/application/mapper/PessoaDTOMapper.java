package com.decodex.br.application.mapper;

import com.decodex.br.application.dto.pessoa.PessoaCreateDTO;
import com.decodex.br.application.dto.pessoa.PessoaResponseDTO;
import com.decodex.br.application.dto.pessoa.PessoaUpdateDTO;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Pessoa;

public class PessoaDTOMapper {

    public static Pessoa toDomain(PessoaCreateDTO dto) {
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

    public static Pessoa toDomain(PessoaUpdateDTO dto) {
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

    public static PessoaResponseDTO toDTO(Pessoa p) {
        if (p == null) return null;

        boolean temEndereco = p.getEndereco() != null;

        return new PessoaResponseDTO(
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