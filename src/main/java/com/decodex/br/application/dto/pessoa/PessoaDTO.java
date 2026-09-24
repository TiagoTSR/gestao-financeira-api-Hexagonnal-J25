package com.decodex.br.application.dto.pessoa;

import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Pessoa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public sealed interface PessoaDTO {

    record Create(
        @NotBlank String nome,
        @NotBlank String logradouro,
        String numero,
        String complemento,
        @NotBlank String bairro,
        @NotBlank String cep,
        @NotBlank String cidade,
        @NotBlank String estado,
        @NotNull Boolean ativo
    ) implements PessoaDTO {
        public Pessoa toDomain() {
            Endereco endereco = new Endereco(logradouro, numero, complemento, bairro, cep, cidade, estado);
            return new Pessoa(null, nome, endereco, ativo);
        }
    }

    record Update(
        @NotBlank String nome,
        @NotBlank String logradouro,
        String numero,
        String complemento,
        @NotBlank String bairro,
        @NotBlank String cep,
        @NotBlank String cidade,
        @NotBlank String estado,
        @NotNull Boolean ativo
    ) implements PessoaDTO {
        public Pessoa toDomain() {
            Endereco endereco = new Endereco(logradouro, numero, complemento, bairro, cep, cidade, estado);
            return new Pessoa(null, nome, endereco, ativo);
        }
    }

    record Response(
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
    ) implements PessoaDTO {
        public static Response from(Pessoa p) {
            if (p == null) return null;
            boolean temEndereco = p.getEndereco() != null;
            return new Response(
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

    record Resumo(
        Long id,
        String nome
    ) implements PessoaDTO {
        public static Resumo from(Pessoa pessoa) {
            if (pessoa == null) return null;
            return new Resumo(pessoa.getId(), pessoa.getNome());
        }
    }
}
