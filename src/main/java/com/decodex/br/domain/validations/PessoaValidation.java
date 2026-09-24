package com.decodex.br.domain.validations;

import com.decodex.br.domain.exeption.RegraDeNegocioException;
import com.decodex.br.domain.model.Endereco;

public final class PessoaValidation {

    private PessoaValidation() {
    }

    public static Long validarId(Long id) {
        if (id == null) {
            throw new RegraDeNegocioException("Id não pode ser nulo");
        }
        return id;
    }

    public static String validarNome(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new RegraDeNegocioException(campo + " não pode ser vazio");
        }
        return valor;
    }

    public static String validarNome(String valor) {
        return validarNome(valor, "Nome");
    }

    public static Endereco validarEndereco(Endereco endereco) {
        if (endereco == null) {
            throw new RegraDeNegocioException("Endereço não pode ser nulo");
        }
        return endereco;
    }

    public static Boolean validarAtivo(Boolean ativo) {
        if (ativo == null) {
            throw new RegraDeNegocioException("Ativo não pode ser vazio (deve ser true ou false)");
        }
        return ativo;
    }
}
