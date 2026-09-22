package com.decodex.br.domain.model;

import com.decodex.br.domain.validations.EnderecoValidation;

import jakarta.persistence.Embeddable;

@Embeddable
public class Endereco {

    private String logradouro;

    private String numero;

    private String complemento;

    private String bairro;

    private String cep;

    private String cidade;

    private String estado;

    private final transient EnderecoValidation validation = new EnderecoValidation();

    protected Endereco() {
    }

    public Endereco(String logradouro, String numero, String complemento, 
                    String bairro, String cep, String cidade, String estado) {
        this.logradouro = validation.validarCampoNaoNulo(logradouro, "logradouro");
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = validation.validarCampoNaoNulo(bairro, "bairro");
        this.cep = validation.validarCampoNaoNulo(cep, "cep");
        this.cidade = validation.validarCampoNaoNulo(cidade, "cidade");
        this.estado = validation.validarCampoNaoNulo(estado, "estado");
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
}