package com.decodex.br.domain.validations;

public class EnderecoValidation {

    public String validarCampoNaoNulo(String valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException(campo + " não pode ser nulo");
        }
        return valor;
    }
}
