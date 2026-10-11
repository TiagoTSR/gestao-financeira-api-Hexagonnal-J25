package com.decodex.br.application.dto.lancamento;

import com.decodex.br.domain.model.StatusLancamento;
import com.decodex.br.domain.model.TipoLancamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record LancamentoHistoricoDTO(
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
        Integer totalParcelas
) {
}
