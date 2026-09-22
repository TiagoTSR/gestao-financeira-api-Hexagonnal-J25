package com.decodex.br.domain.validations;

import com.decodex.br.domain.model.Endereco;

public class PessoaValidation {

    public Long validarId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id não pode ser nulo");
        }
        return id;
    }

    public String validarNome(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " não pode ser vazio");
        }
        return valor;
    }

    public String validarNome(String valor) {
        return validarNome(valor, "Nome");
    }

    public Endereco validarEndereco(Endereco endereco) {
        if (endereco == null) {
            throw new IllegalArgumentException("Endereço não pode ser nulo");
        }
        return endereco;
    }

    public Boolean validarAtivo(Boolean ativo) {
        if (ativo == null) {
            throw new IllegalArgumentException("Ativo não pode ser vazio (deve ser true ou false)");
        }
        return ativo;
    }
}
