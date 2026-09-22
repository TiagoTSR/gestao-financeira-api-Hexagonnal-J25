package com.decodex.br.domain.validations;

public class CategoriaValidation {

    public String validarNome(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " não pode ser vazio");
        }
        return valor;
    }

    public String validarNome(String valor) {
        return validarNome(valor, "Nome");
    }
}
