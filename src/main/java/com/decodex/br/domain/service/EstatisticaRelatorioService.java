package com.decodex.br.domain.service;

import com.decodex.br.domain.model.LancamentoEstatisticaPessoa;
import com.decodex.br.domain.port.in.GerarRelatorioEstatisticaUseCase;
import com.decodex.br.domain.port.out.LancamentoEstatisticaPort;
import com.decodex.br.domain.port.out.RelatorioPdfPort;

import java.time.LocalDate;
import java.util.List;

public class EstatisticaRelatorioService implements GerarRelatorioEstatisticaUseCase {

    private final LancamentoEstatisticaPort estatisticaPort;
    private final RelatorioPdfPort relatorioPdfPort;

    public EstatisticaRelatorioService(LancamentoEstatisticaPort estatisticaPort, RelatorioPdfPort relatorioPdfPort) {
        this.estatisticaPort = estatisticaPort;
        this.relatorioPdfPort = relatorioPdfPort;
    }

    @Override
    public byte[] executarPorPessoa(LocalDate inicio, LocalDate fim) {
        List<LancamentoEstatisticaPessoa> dados = estatisticaPort.porPessoa(inicio, fim);
        return relatorioPdfPort.gerarRelatorioLancamentosPorPessoa(dados, inicio, fim);
    }
}