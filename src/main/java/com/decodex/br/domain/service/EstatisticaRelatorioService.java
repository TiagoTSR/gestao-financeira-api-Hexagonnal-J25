package com.decodex.br.domain.service;

import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.LancamentoEstatisticaPessoa;
import com.decodex.br.domain.port.in.GerarRelatorioEstatisticaInputPort;
import com.decodex.br.domain.port.out.LancamentoEstatisticaPort;
import com.decodex.br.domain.port.out.RelatorioExcelPort;
import com.decodex.br.domain.port.out.RelatorioPdfPort;

import java.time.LocalDate;
import java.util.List;

public class EstatisticaRelatorioService implements GerarRelatorioEstatisticaInputPort {

    private final LancamentoEstatisticaPort estatisticaPort;
    private final RelatorioPdfPort relatorioPdfPort;
    private final RelatorioExcelPort relatorioExcelPort;

    public EstatisticaRelatorioService(
            LancamentoEstatisticaPort estatisticaPort,
            RelatorioPdfPort relatorioPdfPort,
            RelatorioExcelPort relatorioExcelPort) {
        this.estatisticaPort = estatisticaPort;
        this.relatorioPdfPort = relatorioPdfPort;
        this.relatorioExcelPort = relatorioExcelPort;
    }

    @Override
    public byte[] executarPorPessoa(LocalDate inicio, LocalDate fim) {
        List<LancamentoEstatisticaPessoa> dados = estatisticaPort.porPessoa(inicio, fim);
        List<Lancamento> lancamentos = estatisticaPort.listarPorPeriodo(inicio, fim);
        return relatorioPdfPort.gerarRelatorioLancamentosPorPessoa(dados, lancamentos, inicio, fim);
    }

    @Override
    public byte[] executarPorPessoaExcel(LocalDate inicio, LocalDate fim) {
        List<LancamentoEstatisticaPessoa> dados = estatisticaPort.porPessoa(inicio, fim);
        List<Lancamento> lancamentos = estatisticaPort.listarPorPeriodo(inicio, fim);
        return relatorioExcelPort.gerarRelatorioLancamentosExcel(dados, lancamentos, inicio, fim);
    }
}