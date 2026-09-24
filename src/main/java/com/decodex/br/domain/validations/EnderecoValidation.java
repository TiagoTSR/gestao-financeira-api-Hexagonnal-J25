package com.decodex.br.domain.validations;

import com.decodex.br.domain.exeption.RegraDeNegocioException;

public final class EnderecoValidation {

    private EnderecoValidation() {
    }

    public static String validarCampoNaoNulo(String valor, String campo) {
        if (valor == null) {
            throw new RegraDeNegocioException(campo + " não pode ser nulo");
        }
        return valor;
    }
}
