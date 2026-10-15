package com.decodex.br.domain.model;

import static com.decodex.br.domain.validations.EnderecoValidation.validarCampoNaoNulo;

import java.util.Objects;

public class Endereco {

    private final String logradouro;
    private final String numero;
    private final String complemento;
    private final String bairro;
    private final String cep;
    private final String cidade;
    private final String estado;

    public Endereco(String logradouro, String numero, String complemento, 
                    String bairro, String cep, String cidade, String estado) {
        this.logradouro = validarCampoNaoNulo(logradouro, "logradouro");
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = validarCampoNaoNulo(bairro, "bairro");
        this.cep = validarCampoNaoNulo(cep, "cep");
        this.cidade = validarCampoNaoNulo(cidade, "cidade");
        this.estado = validarCampoNaoNulo(estado, "estado");
    }

    public String getLogradouro() {
        return logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public String getCep() {
        return cep;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEstado() {
        return estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Endereco other)) return false;
        return Objects.equals(logradouro, other.logradouro) &&
               Objects.equals(numero, other.numero) &&
               Objects.equals(complemento, other.complemento) &&
               Objects.equals(bairro, other.bairro) &&
               Objects.equals(cep, other.cep) &&
               Objects.equals(cidade, other.cidade) &&
               Objects.equals(estado, other.estado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logradouro, numero, complemento, bairro, cep, cidade, estado);
    }

    @Override
    public String toString() {
        return String.format("%s, %s%s, %s - %s/%s, CEP %s",
                logradouro,
                numero != null ? numero : "S/N",
                complemento != null ? " (" + complemento + ")" : "",
                bairro,
                cidade,
                estado,
                cep);
    }
}