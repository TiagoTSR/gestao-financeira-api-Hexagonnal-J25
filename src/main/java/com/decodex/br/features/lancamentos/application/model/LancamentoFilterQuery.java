package com.decodex.br.features.lancamentos.application.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.decodex.br.domain.model.StatusLancamento;
import com.decodex.br.domain.model.TipoLancamento;

public record LancamentoFilterQuery(
        String descricao,
        LocalDate dataVencimento,
        LocalDate dataVencimentoDe,
        LocalDate dataVencimentoAte,
        LocalDate dataPagamento,
        BigDecimal valor,
        String observacao,
        TipoLancamento tipo,
        StatusLancamento status,
        String nomeCategoria,
        String nomePessoa,
        UUID categoriaId,
        UUID pessoaId) {
}
