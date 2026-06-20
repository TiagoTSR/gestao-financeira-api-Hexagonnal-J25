package com.decodex.br.domain.port.in;

import java.time.LocalDate;

public interface GerarRelatorioEstatisticaUseCase {
    byte[] executarPorPessoa(LocalDate inicio, LocalDate fim);
}