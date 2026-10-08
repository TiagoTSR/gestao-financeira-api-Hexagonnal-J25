package com.decodex.br.application.dto.lancamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.decodex.br.application.dto.categoria.CategoriaResponseDTO;
import com.decodex.br.application.dto.pessoa.PessoaResumoDTO;
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
    PessoaResumoDTO pessoa
) {}
