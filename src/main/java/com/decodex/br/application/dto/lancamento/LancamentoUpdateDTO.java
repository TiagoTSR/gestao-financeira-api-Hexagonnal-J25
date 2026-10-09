package com.decodex.br.application.dto.lancamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.decodex.br.domain.model.TipoLancamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LancamentoUpdateDTO(
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
    UUID pessoaId
) {}
