package com.decodex.br.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Tenant {

    private UUID id;
    private String nome;
    private String cnpj;
    private Boolean ativo;
    private LocalDateTime dataCriacao;

    public Tenant(UUID id, String nome, String cnpj, Boolean ativo, LocalDateTime dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.ativo = ativo != null ? ativo : true;
        this.dataCriacao = dataCriacao != null ? dataCriacao : LocalDateTime.now();
    }

    public Tenant(String nome, String cnpj) {
        this(null, nome, cnpj, true, LocalDateTime.now());
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tenant other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
