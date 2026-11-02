package com.decodex.br.features.lancamentos.application.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.decodex.br.domain.model.TipoLancamento;

public record LancamentoUpdateCommand(
        String descricao,
        LocalDate dataVencimento,
        LocalDate dataPagamento,
        BigDecimal valor,
        String observacao,
        TipoLancamento tipo,
        UUID categoriaId,
        UUID pessoaId) {
}
