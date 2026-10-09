package com.decodex.br.application.dto.lancamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.decodex.br.application.dto.categoria.CategoriaResponseDTO;
import com.decodex.br.application.dto.pessoa.PessoaResumoDTO;
import com.decodex.br.domain.model.StatusLancamento;
import com.decodex.br.domain.model.TipoLancamento;

public record LancamentoResponseDTO(
    UUID id,
    String descricao,
    LocalDate dataVencimento,
    LocalDate dataPagamento,
    BigDecimal valor,
    String observacao,
    TipoLancamento tipo,
    CategoriaResponseDTO categoria,
    PessoaResumoDTO pessoa,
    StatusLancamento status,
    BigDecimal valorPago,
    Integer numeroParcela,
    Integer totalParcelas
) {
    public LancamentoResponseDTO(
            UUID id,
            String descricao,
            LocalDate dataVencimento,
            LocalDate dataPagamento,
            BigDecimal valor,
            String observacao,
            TipoLancamento tipo,
            CategoriaResponseDTO categoria,
            PessoaResumoDTO pessoa) {
        this(id, descricao, dataVencimento, dataPagamento, valor, observacao, tipo, categoria, pessoa,
             (dataPagamento != null) ? (tipo == TipoLancamento.RECEITA ? StatusLancamento.RECEBIDO : StatusLancamento.PAGO) : StatusLancamento.PENDENTE,
             dataPagamento != null ? valor : null, 1, 1);
    }

    @JsonProperty("data_vencimento")
    public LocalDate getDataVencimentoSnake() {
        return dataVencimento;
    }

    @JsonProperty("data_pagamento")
    public LocalDate getDataPagamentoSnake() {
        return dataPagamento;
    }

    @JsonProperty("valor_pago")
    public BigDecimal getValorPagoSnake() {
        return valorPago;
    }

    @JsonProperty("numero_parcela")
    public Integer getNumeroParcelaSnake() {
        return numeroParcela;
    }

    @JsonProperty("total_parcelas")
    public Integer getTotalParcelasSnake() {
        return totalParcelas;
    }
}
