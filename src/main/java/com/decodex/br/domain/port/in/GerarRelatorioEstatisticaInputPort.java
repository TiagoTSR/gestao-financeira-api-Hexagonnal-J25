package com.decodex.br.domain.port.in;

import java.time.LocalDate;

public interface GerarRelatorioEstatisticaInputPort {
    byte[] executarPorPessoa(LocalDate inicio, LocalDate fim);
}
