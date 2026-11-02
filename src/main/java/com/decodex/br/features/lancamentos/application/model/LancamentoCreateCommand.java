package com.decodex.br.features.lancamentos.application.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.decodex.br.domain.model.StatusLancamento;
import com.decodex.br.domain.model.TipoLancamento;

public record LancamentoCreateCommand(
        String descricao,
        LocalDate dataVencimento,
        LocalDate dataPagamento,
        BigDecimal valor,
        String observacao,
        TipoLancamento tipo,
        UUID categoriaId,
        UUID pessoaId,
        StatusLancamento status,
        BigDecimal valorPago,
        Integer numeroParcela,
        Integer totalParcelas) {

    public LancamentoCreateCommand(
            String descricao,
            LocalDate dataVencimento,
            LocalDate dataPagamento,
            BigDecimal valor,
            String observacao,
            TipoLancamento tipo,
            UUID categoriaId,
            UUID pessoaId) {
        this(descricao, dataVencimento, dataPagamento, valor, observacao, tipo,
                categoriaId, pessoaId, null, null, 1, 1);
    }
}
