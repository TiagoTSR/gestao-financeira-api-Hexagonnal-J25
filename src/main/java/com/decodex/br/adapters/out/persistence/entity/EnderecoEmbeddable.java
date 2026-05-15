package com.decodex.br.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class EnderecoEmbeddable {

	@Column(name = "logradouro", length = 100, nullable = false)
	private String logradouro;

	@Column(name = "numero", length = 20)
	private String numero;

	@Column(name = "complemento", length = 60)
	private String complemento;

	@Column(name = "bairro", length = 60, nullable = false)
	private String bairro;

	@Column(name = "cep", length = 10, nullable = false)
	private String cep;

	@Column(name = "cidade", length = 60, nullable = false)
	private String cidade;

	@Column(name = "estado", length = 2, nullable = false)
	private String estado;

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
    
}