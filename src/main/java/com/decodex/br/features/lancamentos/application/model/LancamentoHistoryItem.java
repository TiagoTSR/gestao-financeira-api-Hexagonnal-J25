package com.decodex.br.features.lancamentos.application.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.decodex.br.domain.model.StatusLancamento;
import com.decodex.br.domain.model.TipoLancamento;

public record LancamentoHistoryItem(
        Long revisao,
        LocalDateTime dataRevisao,
        String usuario,
        String operacao,
        UUID id,
        String descricao,
        BigDecimal valor,
        TipoLancamento tipo,
        StatusLancamento status,
        LocalDate dataVencimento,
        LocalDate dataPagamento,
        BigDecimal valorPago,
        Integer numeroParcela,
        Integer totalParcelas) {
}
