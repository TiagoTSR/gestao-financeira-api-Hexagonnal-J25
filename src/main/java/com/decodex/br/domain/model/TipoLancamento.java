package com.decodex.br.domain.model;

import java.math.BigDecimal;

public enum TipoLancamento {
    RECEITA("Receita", 1) {
        @Override
        public StatusLancamento getStatusQuitado() {
            return StatusLancamento.RECEBIDO;
        }

        @Override
        public boolean isCompativelCom(StatusLancamento status) {
            return status != StatusLancamento.PAGO;
        }
    },
    DESPESA("Despesa", -1) {
        @Override
        public StatusLancamento getStatusQuitado() {
            return StatusLancamento.PAGO;
        }

        @Override
        public boolean isCompativelCom(StatusLancamento status) {
            return status != StatusLancamento.RECEBIDO;
        }
    };

    private final String descricao;
    private final int fatorSinal;

    TipoLancamento(String descricao, int fatorSinal) {
        this.descricao = descricao;
        this.fatorSinal = fatorSinal;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isReceita() {
        return this == RECEITA;
    }

    public boolean isDespesa() {
        return this == DESPESA;
    }

    public abstract StatusLancamento getStatusQuitado();

    public abstract boolean isCompativelCom(StatusLancamento status);

    public BigDecimal calcularImpacto(BigDecimal valor) {
        if (valor == null) {
            return BigDecimal.ZERO;
        }
        return valor.multiply(BigDecimal.valueOf(this.fatorSinal));
    }
}