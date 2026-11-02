package com.decodex.br.features.relatorios.application.port.out;

import java.time.LocalDate;

public interface RelatorioFileGenerator {

    byte[] gerarPdf(LocalDate inicio, LocalDate fim);

    byte[] gerarExcel(LocalDate inicio, LocalDate fim);
}
