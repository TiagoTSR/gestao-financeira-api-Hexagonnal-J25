package com.decodex.br.domain.port.out;

import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.LancamentoEstatisticaPessoa;

import java.time.LocalDate;
import java.util.List;

public interface RelatorioExcelPort {
    byte[] gerarRelatorioLancamentosExcel(
            List<LancamentoEstatisticaPessoa> estatisticas,
            List<Lancamento> lancamentos,
            LocalDate inicio,
            LocalDate fim);
}
