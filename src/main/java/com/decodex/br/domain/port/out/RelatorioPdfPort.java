package com.decodex.br.domain.port.out;

import com.decodex.br.domain.model.LancamentoEstatisticaPessoa;
import java.time.LocalDate;
import java.util.List;

public interface RelatorioPdfPort {
    byte[] gerarRelatorioLancamentosPorPessoa(List<LancamentoEstatisticaPessoa> dados, LocalDate inicio, LocalDate fim);
}