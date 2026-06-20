package com.decodex.br.domain.port.out;

import com.decodex.br.domain.model.LancamentoEstatisticaPessoa;
import java.time.LocalDate;
import java.util.List;

public interface LancamentoEstatisticaPort {
    List<LancamentoEstatisticaPessoa> porPessoa(LocalDate inicio, LocalDate fim);
}