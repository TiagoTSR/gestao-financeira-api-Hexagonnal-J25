package com.decodex.br.testesunitarios.domain.service;

import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.LancamentoEstatisticaPessoa;
import com.decodex.br.domain.port.out.LancamentoEstatisticaPort;
import com.decodex.br.domain.port.out.RelatorioExcelPort;
import com.decodex.br.domain.port.out.RelatorioPdfPort;
import com.decodex.br.domain.service.EstatisticaRelatorioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - EstatisticaRelatorioService")
class EstatisticaRelatorioServiceTest {

    @Mock
    private LancamentoEstatisticaPort estatisticaPort;

    @Mock
    private RelatorioPdfPort relatorioPdfPort;

    @Mock
    private RelatorioExcelPort relatorioExcelPort;

    @InjectMocks
    private EstatisticaRelatorioService service;

    @Test
    @DisplayName("Deve delegar execucao de relatorio PDF detalhado para as portas corretas")
    void deveExecutarPorPessoaPdf() {
        LocalDate inicio = LocalDate.of(2026, 10, 1);
        LocalDate fim = LocalDate.of(2026, 10, 31);
        List<LancamentoEstatisticaPessoa> dados = List.of();
        List<Lancamento> lancamentos = List.of();
        byte[] expectedPdf = new byte[]{1, 2, 3};

        when(estatisticaPort.porPessoa(inicio, fim)).thenReturn(dados);
        when(estatisticaPort.listarPorPeriodo(inicio, fim)).thenReturn(lancamentos);
        when(relatorioPdfPort.gerarRelatorioLancamentosPorPessoa(dados, lancamentos, inicio, fim)).thenReturn(expectedPdf);

        byte[] result = service.executarPorPessoa(inicio, fim);

        assertArrayEquals(expectedPdf, result);
        verify(estatisticaPort).porPessoa(inicio, fim);
        verify(estatisticaPort).listarPorPeriodo(inicio, fim);
        verify(relatorioPdfPort).gerarRelatorioLancamentosPorPessoa(dados, lancamentos, inicio, fim);
    }

    @Test
    @DisplayName("Deve delegar execucao de relatorio Excel para as portas corretas")
    void deveExecutarPorPessoaExcel() {
        LocalDate inicio = LocalDate.of(2026, 10, 1);
        LocalDate fim = LocalDate.of(2026, 10, 31);
        List<LancamentoEstatisticaPessoa> dados = List.of();
        List<Lancamento> lancamentos = List.of();
        byte[] expectedExcel = new byte[]{4, 5, 6};

        when(estatisticaPort.porPessoa(inicio, fim)).thenReturn(dados);
        when(estatisticaPort.listarPorPeriodo(inicio, fim)).thenReturn(lancamentos);
        when(relatorioExcelPort.gerarRelatorioLancamentosExcel(dados, lancamentos, inicio, fim)).thenReturn(expectedExcel);

        byte[] result = service.executarPorPessoaExcel(inicio, fim);

        assertArrayEquals(expectedExcel, result);
        verify(estatisticaPort).porPessoa(inicio, fim);
        verify(estatisticaPort).listarPorPeriodo(inicio, fim);
        verify(relatorioExcelPort).gerarRelatorioLancamentosExcel(dados, lancamentos, inicio, fim);
    }
}
