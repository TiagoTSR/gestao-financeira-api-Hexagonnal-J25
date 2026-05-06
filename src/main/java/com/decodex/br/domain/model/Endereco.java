package com.decodex.br.domain.model;

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

    protected Endereco() {
    }

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
    
    private String validarCampoNaoNulo(String valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException(campo + " não pode ser nulo");
        }
        return valor;
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