package com.decodex.br.domain.validations;

import com.decodex.br.domain.exeption.RegraDeNegocioException;

public final class CategoriaValidation {

    private CategoriaValidation() {
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
}
