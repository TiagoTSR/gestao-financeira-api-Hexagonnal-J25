package com.decodex.br.domain.port.out;

import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.LancamentoEstatisticaPessoa;
import java.time.LocalDate;
import java.util.List;

public interface LancamentoEstatisticaPort {
    List<LancamentoEstatisticaPessoa> porPessoa(LocalDate inicio, LocalDate fim);
    List<Lancamento> listarPorPeriodo(LocalDate inicio, LocalDate fim);
}