package com.decodex.br.application.dto.lancamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.decodex.br.domain.model.TipoLancamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LancamentoUpdateDTO(
    @NotBlank
    String descricao,

    @NotNull
    LocalDate dataVencimento,

    LocalDate dataPagamento,

    @NotNull
    BigDecimal valor,

    String observacao,

    @NotNull
    TipoLancamento tipo,

    @NotNull
    UUID categoriaId,

    @NotNull
    UUID pessoaId
) {}
