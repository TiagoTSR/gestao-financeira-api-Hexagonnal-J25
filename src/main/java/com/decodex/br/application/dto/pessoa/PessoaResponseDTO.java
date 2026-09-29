package com.decodex.br.application.dto.pessoa;

import com.decodex.br.domain.model.Pessoa;

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
) {
    public static PessoaResponseDTO from(Pessoa p) {
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
