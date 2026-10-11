package com.decodex.br.domain.service;

import com.decodex.br.application.dto.lancamento.LancamentoCreateDTO;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.Pessoa;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class ParcelamentoFactory {

    private ParcelamentoFactory() {
    }

    public static List<Lancamento> gerarLancamentos(LancamentoCreateDTO dto, Categoria categoria, Pessoa pessoa) {
        int total = (dto.totalParcelas() != null && dto.totalParcelas() > 1) ? dto.totalParcelas() : 1;

        if (total <= 1) {
            return List.of(criarLancamentoUnico(dto, categoria, pessoa));
        }

        return criarLancamentosParcelados(dto, categoria, pessoa, total);
    }

    private static Lancamento criarLancamentoUnico(LancamentoCreateDTO dto, Categoria categoria, Pessoa pessoa) {
        BigDecimal valorPago = (dto.valorPago() != null)
                ? dto.valorPago()
                : (dto.dataPagamento() != null ? dto.valor() : null);

        int numeroParcela = (dto.numeroParcela() != null) ? dto.numeroParcela() : 1;

        return new Lancamento(
                dto.descricao(),
                dto.dataVencimento(),
                dto.dataPagamento(),
                dto.valor(),
                dto.observacao(),
                dto.tipo(),
                categoria,
                pessoa,
                dto.status(),
                valorPago,
                numeroParcela,
                1
        );
    }

    private static List<Lancamento> criarLancamentosParcelados(
            LancamentoCreateDTO dto, Categoria categoria, Pessoa pessoa, int total) {

        BigDecimal valorTotal = dto.valor();
        BigDecimal parcelaBase = valorTotal.divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_EVEN);
        BigDecimal diferencaCentavos = valorTotal.subtract(parcelaBase.multiply(BigDecimal.valueOf(total)));

        List<Lancamento> parcelas = new ArrayList<>(total);

        for (int i = 1; i <= total; i++) {
            BigDecimal valorDestaParcela = (i == 1) ? parcelaBase.add(diferencaCentavos) : parcelaBase;
            String descricaoParcela = String.format("%s (%d/%d)", dto.descricao(), i, total);
            LocalDate vencimentoParcela = dto.dataVencimento().plusMonths(i - 1);
            LocalDate pagamentoParcela = (i == 1) ? dto.dataPagamento() : null;
            BigDecimal valorPago = (pagamentoParcela != null) ? valorDestaParcela : null;

            Lancamento parcela = new Lancamento(
                    descricaoParcela,
                    vencimentoParcela,
                    pagamentoParcela,
                    valorDestaParcela,
                    dto.observacao(),
                    dto.tipo(),
                    categoria,
                    pessoa,
                    dto.status(),
                    valorPago,
                    i,
                    total
            );
            parcelas.add(parcela);
        }

        return parcelas;
    }
}
