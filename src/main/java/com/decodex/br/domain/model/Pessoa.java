package com.decodex.br.domain.model;

public class Pessoa {

	private Long id;
	
    private String nome;

    private Endereco endereco;

    private Boolean ativo;

	public Pessoa(Long id, String nome, Endereco endereco, Boolean ativo) {
		super();
		this.id = id;
		this.nome = validarNomePessoa(nome, "Nome");
		this.endereco = validarEndereco(endereco);
		this.ativo = validarAtivo(ativo);
	}
	
	private String validarNomePessoa(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " não pode ser vazio");
        }
        return valor;
    }
	
	private Endereco validarEndereco(Endereco endereco) {
	    if (endereco == null) {
	        throw new IllegalArgumentException("Endereço não pode ser nulo");
	    }
	    return endereco;
	}
	
	private Boolean validarAtivo(Boolean ativo) {
        if (ativo == null) {
            throw new IllegalArgumentException("Ativo não pode ser vazio (deve ser true ou false)");
        }
        return ativo;
    }
	
	public void alterarNome(String novoNome) {
	       this.nome = validarNomePessoa(novoNome, novoNome);
	}
	
	public void alterarEndereço(Endereco novaEndereço) {
	       this.endereco = novaEndereço;
	}
	
	public void alterarAtivo(Boolean alteracaoAtivo) {
	       this.ativo = validarAtivo(alteracaoAtivo);
	}
	
	public void atualizarCampos(Pessoa novosDados) {
		alterarNome(novosDados.nome);
		alterarEndereço(novosDados.endereco);
		alterarAtivo(novosDados.ativo);
    }

	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public Boolean getAtivo() {
		return ativo;
	}
	
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    
	    Pessoa pessoa = (Pessoa) o;
	    
	    return id != null && id.equals(pessoa.id);
	}

	@Override
	public int hashCode() {
	    return getClass().hashCode();
	}
    
 }