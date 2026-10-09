package com.decodex.br.application.dto.pessoa;

import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Pessoa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PessoaCreateDTO(
    @NotBlank String nome,
    String logradouro,
    String numero,
    String complemento,
    String bairro,
    String cep,
    String cidade,
    String estado,
    @NotNull Boolean ativo,
    EnderecoInputDTO endereco
) {
    public record EnderecoInputDTO(
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cep,
        String cidade,
        String estado
    ) {}

    public PessoaCreateDTO(
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
        this(nome, logradouro, numero, complemento, bairro, cep, cidade, estado, ativo, null);
    }

    public Pessoa toDomain() {
        String log = endereco != null && endereco.logradouro() != null ? endereco.logradouro() : logradouro;
        String num = endereco != null && endereco.numero() != null ? endereco.numero() : numero;
        String comp = endereco != null && endereco.complemento() != null ? endereco.complemento() : complemento;
        String bai = endereco != null && endereco.bairro() != null ? endereco.bairro() : bairro;
        String c = endereco != null && endereco.cep() != null ? endereco.cep() : cep;
        String cid = endereco != null && endereco.cidade() != null ? endereco.cidade() : cidade;
        String est = endereco != null && endereco.estado() != null ? endereco.estado() : estado;

        Endereco end = null;
        if (log != null || c != null || cid != null || est != null || bai != null) {
            end = new Endereco(log, num, comp, bai, c, cid, est);
        }

        return new Pessoa(null, nome, end, ativo);
    }
}
