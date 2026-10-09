package com.decodex.br.application.dto.pessoa;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.decodex.br.domain.model.Pessoa;

public record PessoaResponseDTO(
    UUID id,
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
    public record EnderecoDTO(
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cep,
        String cidade,
        String estado
    ) {}

    @JsonProperty("endereco")
    public EnderecoDTO getEndereco() {
        if (logradouro == null && cep == null && cidade == null && estado == null) {
            return null;
        }
        return new EnderecoDTO(logradouro, numero, complemento, bairro, cep, cidade, estado);
    }

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
