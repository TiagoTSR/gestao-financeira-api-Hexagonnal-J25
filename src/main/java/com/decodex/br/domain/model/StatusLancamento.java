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

    public boolean isQuitado() {
        return this == PAGO || this == RECEBIDO;
    }

    public boolean isPendente() {
        return this == PENDENTE;
    }

    public boolean isCancelado() {
        return this == CANCELADO;
    }

    public boolean permiteEdicao() {
        return this != CANCELADO;
    }

    public boolean podeTransitarPara(StatusLancamento novoStatus) {
        if (novoStatus == null || this == novoStatus) {
            return true;
        }

        return switch (this) {
            case PENDENTE -> novoStatus == PAGO || novoStatus == RECEBIDO || novoStatus == CANCELADO;
            case PAGO, RECEBIDO -> novoStatus == PENDENTE;
            case CANCELADO -> novoStatus == PENDENTE;
        };
    }
}
