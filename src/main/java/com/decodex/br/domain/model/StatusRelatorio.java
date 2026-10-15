package com.decodex.br.domain.model;

public enum StatusRelatorio {
    PENDENTE,
    PROCESSANDO,
    CONCLUIDO,
    ERRO;

    public boolean isTerminal() {
        return this == CONCLUIDO || this == ERRO;
    }

    public boolean isEmAndamento() {
        return this == PENDENTE || this == PROCESSANDO;
    }

    public boolean isSucesso() {
        return this == CONCLUIDO;
    }
}
