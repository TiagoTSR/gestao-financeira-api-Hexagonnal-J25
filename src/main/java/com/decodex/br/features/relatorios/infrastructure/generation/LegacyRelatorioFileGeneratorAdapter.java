package com.decodex.br.features.relatorios.infrastructure.generation;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.decodex.br.domain.port.in.GerarRelatorioEstatisticaInputPort;
import com.decodex.br.features.relatorios.application.port.out.RelatorioFileGenerator;

@Component
public class LegacyRelatorioFileGeneratorAdapter implements RelatorioFileGenerator {

    private final GerarRelatorioEstatisticaInputPort legacyGenerator;

    public LegacyRelatorioFileGeneratorAdapter(GerarRelatorioEstatisticaInputPort legacyGenerator) {
        this.legacyGenerator = legacyGenerator;
    }

    @Override
    public byte[] gerarPdf(LocalDate inicio, LocalDate fim) {
        return legacyGenerator.executarPorPessoa(inicio, fim);
    }

    @Override
    public byte[] gerarExcel(LocalDate inicio, LocalDate fim) {
        return legacyGenerator.executarPorPessoaExcel(inicio, fim);
    }
}
