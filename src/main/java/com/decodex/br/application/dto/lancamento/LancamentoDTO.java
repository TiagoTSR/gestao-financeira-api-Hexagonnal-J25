package com.decodex.br.application.dto.lancamento;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.decodex.br.application.dto.categoria.CategoriaDTO;
import com.decodex.br.application.dto.pessoa.PessoaDTO;
import com.decodex.br.domain.model.TipoLancamento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public sealed interface LancamentoDTO {

    record Create(
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
        Long categoriaId,

        @NotNull
        Long pessoaId
    ) implements LancamentoDTO {}

    record Update(
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
        Long categoriaId,

        @NotNull
        Long pessoaId
    ) implements LancamentoDTO {}

    record Response(
        Long id,
        String descricao,
        LocalDate dataVencimento,
        LocalDate dataPagamento,
        BigDecimal valor,
        String observacao,
        TipoLancamento tipo,
        CategoriaDTO.Response categoria,
        PessoaDTO.Resumo pessoa
    ) implements LancamentoDTO {}
}
