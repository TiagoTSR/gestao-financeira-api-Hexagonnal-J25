package com.decodex.br.domain.model;

public enum StatusLancamento {
    PENDENTE("Pendente"),
    PAGO("Pago"),
    RECEBIDO("Recebido"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusLancamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
