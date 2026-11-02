package com.decodex.br.testesunitarios.features.relatorios;

import com.decodex.br.domain.model.StatusRelatorio;
import com.decodex.br.features.relatorios.application.RelatorioProcessingApplicationService;
import com.decodex.br.features.relatorios.application.model.ProcessarRelatorioCommand;
import com.decodex.br.features.relatorios.application.model.RelatorioSolicitacao;
import com.decodex.br.features.relatorios.application.port.out.RelatorioFileGenerator;
import com.decodex.br.features.relatorios.application.port.out.RelatorioSolicitacaoStore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários - RelatorioProcessingApplicationService")
class RelatorioProcessingApplicationServiceTest {

    @Mock
    private RelatorioSolicitacaoStore relatorioStore;

    @Mock
    private RelatorioFileGenerator fileGenerator;

    @InjectMocks
    private RelatorioProcessingApplicationService service;

    private final UUID protocolo = UUID.randomUUID();
    private final UUID tenantId = UUID.randomUUID();
    private final LocalDate inicio = LocalDate.of(2026, 1, 1);
    private final LocalDate fim = LocalDate.of(2026, 1, 31);

    @Test
    @DisplayName("Deve gerar PDF e concluir solicitação")
    void deveProcessarPdfComSucesso() {
        byte[] conteudo = "%PDF-relatorio".getBytes();
        when(relatorioStore.findById(protocolo)).thenReturn(Optional.of(solicitacao("PDF")));
        when(fileGenerator.gerarPdf(inicio, fim)).thenReturn(conteudo);

        assertTrue(service.processar(comando("PDF")));

        ArgumentCaptor<RelatorioSolicitacao> captor = ArgumentCaptor.forClass(RelatorioSolicitacao.class);
        verify(relatorioStore, times(2)).save(captor.capture());
        assertEquals(StatusRelatorio.PROCESSANDO, captor.getAllValues().get(0).status());
        RelatorioSolicitacao concluida = captor.getAllValues().get(1);
        assertEquals(StatusRelatorio.CONCLUIDO, concluida.status());
        assertArrayEquals(conteudo, concluida.conteudo());
        assertEquals("application/pdf", concluida.contentType());
        assertEquals("relatorio-lancamentos_2026-01-01_2026-01-31.pdf", concluida.nomeArquivo());
        assertNotNull(concluida.dataConclusao());
        verify(fileGenerator).gerarPdf(inicio, fim);
        verify(fileGenerator, never()).gerarExcel(any(), any());
    }

    @Test
    @DisplayName("Deve gerar Excel e concluir solicitação")
    void deveProcessarExcelComSucesso() {
        byte[] conteudo = {1, 2, 3};
        when(relatorioStore.findById(protocolo)).thenReturn(Optional.of(solicitacao("EXCEL")));
        when(fileGenerator.gerarExcel(inicio, fim)).thenReturn(conteudo);

        assertTrue(service.processar(comando("EXCEL")));

        ArgumentCaptor<RelatorioSolicitacao> captor = ArgumentCaptor.forClass(RelatorioSolicitacao.class);
        verify(relatorioStore, times(2)).save(captor.capture());
        RelatorioSolicitacao concluida = captor.getAllValues().get(1);
        assertEquals(StatusRelatorio.CONCLUIDO, concluida.status());
        assertArrayEquals(conteudo, concluida.conteudo());
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                concluida.contentType());
        assertTrue(concluida.nomeArquivo().endsWith(".xlsx"));
        verify(fileGenerator).gerarExcel(inicio, fim);
        verify(fileGenerator, never()).gerarPdf(any(), any());
    }

    @Test
    @DisplayName("Deve ignorar protocolo inexistente sem gerar arquivo")
    void deveRetornarFalseQuandoSolicitacaoNaoExiste() {
        when(relatorioStore.findById(protocolo)).thenReturn(Optional.empty());

        assertFalse(service.processar(comando("PDF")));

        verify(relatorioStore, never()).save(any());
        verifyNoInteractions(fileGenerator);
    }

    @Test
    @DisplayName("Deve registrar falha e relançar exceção para retry e DLQ")
    void deveMarcarErroERelancarFalha() {
        RuntimeException failure = new RuntimeException("Falha simulada");
        when(relatorioStore.findById(protocolo)).thenReturn(Optional.of(solicitacao("PDF")));
        when(fileGenerator.gerarPdf(inicio, fim)).thenThrow(failure);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> service.processar(comando("PDF")));

        assertSame(failure, thrown);
        ArgumentCaptor<RelatorioSolicitacao> captor = ArgumentCaptor.forClass(RelatorioSolicitacao.class);
        verify(relatorioStore, times(2)).save(captor.capture());
        RelatorioSolicitacao erro = captor.getAllValues().get(1);
        assertEquals(StatusRelatorio.ERRO, erro.status());
        assertEquals("Falha simulada", erro.mensagemErro());
        assertNotNull(erro.dataConclusao());
    }

    private ProcessarRelatorioCommand comando(String formato) {
        return new ProcessarRelatorioCommand(protocolo, tenantId, formato, inicio, fim);
    }

    private RelatorioSolicitacao solicitacao(String formato) {
        return new RelatorioSolicitacao(
                protocolo,
                tenantId,
                formato,
                inicio,
                fim,
                StatusRelatorio.PENDENTE,
                null,
                null,
                null,
                null,
                LocalDateTime.now(),
                null);
    }
}
