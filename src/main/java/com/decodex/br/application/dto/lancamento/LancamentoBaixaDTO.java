package com.decodex.br.application.dto.lancamento;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAlias;

public record LancamentoBaixaDTO(
    @JsonAlias({"data_pagamento", "dataPagamento"})
    LocalDate dataPagamento,

    @JsonAlias({"valor_pago", "valorPago"})
    BigDecimal valorPago
) {
    public LancamentoBaixaDTO() {
        this(null, null);
    }
}
