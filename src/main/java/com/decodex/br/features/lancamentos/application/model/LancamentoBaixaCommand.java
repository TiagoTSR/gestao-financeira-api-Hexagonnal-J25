package com.decodex.br.features.lancamentos.application.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LancamentoBaixaCommand(LocalDate dataPagamento, BigDecimal valorPago) {
}
