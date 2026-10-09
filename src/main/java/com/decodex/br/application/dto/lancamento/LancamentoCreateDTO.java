package com.decodex.br.application.dto.lancamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.decodex.br.domain.model.StatusLancamento;
import com.decodex.br.domain.model.TipoLancamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LancamentoCreateDTO(
    @NotBlank
    String descricao,

    @NotNull
    @JsonAlias({"data_vencimento", "dataVencimento"})
    LocalDate dataVencimento,

    @JsonAlias({"data_pagamento", "dataPagamento"})
    LocalDate dataPagamento,

    @NotNull
    BigDecimal valor,

    String observacao,

    @NotNull
    TipoLancamento tipo,

    @NotNull
    @JsonAlias({"categoria_id", "categoriaId"})
    UUID categoriaId,

    @NotNull
    @JsonAlias({"pessoa_id", "pessoaId"})
    UUID pessoaId,

    StatusLancamento status,

    @JsonAlias({"valor_pago", "valorPago"})
    BigDecimal valorPago,

    @JsonAlias({"numero_parcela", "numeroParcela"})
    Integer numeroParcela,

    @JsonAlias({"total_parcelas", "totalParcelas"})
    Integer totalParcelas
) {
    public LancamentoCreateDTO(
            String descricao,
            LocalDate dataVencimento,
            LocalDate dataPagamento,
            BigDecimal valor,
            String observacao,
            TipoLancamento tipo,
            UUID categoriaId,
            UUID pessoaId) {
        this(descricao, dataVencimento, dataPagamento, valor, observacao, tipo, categoriaId, pessoaId, null, null, 1, 1);
    }
}
