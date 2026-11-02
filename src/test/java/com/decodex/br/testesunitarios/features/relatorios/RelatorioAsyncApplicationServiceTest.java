package com.decodex.br.testesunitarios.features.relatorios;

import com.decodex.br.application.dto.relatorio.RelatorioDownloadDTO;
import com.decodex.br.application.dto.relatorio.RelatorioProtocoloResponseDTO;
import com.decodex.br.application.dto.relatorio.RelatorioSolicitacaoMsg;
import com.decodex.br.application.dto.relatorio.RelatorioStatusResponseDTO;
import com.decodex.br.domain.exception.RegraDeNegocioException;
import com.decodex.br.domain.exception.ResourceNotFoundException;
import com.decodex.br.domain.model.StatusRelatorio;
import com.decodex.br.domain.port.out.RelatorioProducerPort;
import com.decodex.br.features.relatorios.application.RelatorioAsyncApplicationService;
import com.decodex.br.features.relatorios.application.model.RelatorioSolicitacao;
import com.decodex.br.features.relatorios.application.model.SolicitarRelatorioCommand;
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
@DisplayName("Testes unitários - RelatorioAsyncApplicationService")
class RelatorioAsyncApplicationServiceTest {

    @Mock
    private RelatorioSolicitacaoStore relatorioStore;

    @Mock
    private RelatorioProducerPort relatorioProducerPort;

    @InjectMocks
    private RelatorioAsyncApplicationService service;

    private final UUID tenantId = UUID.randomUUID();

    @Test
    @DisplayName("Deve solicitar relatório PDF e publicar mensagem com solicitante e tenant")
    void deveSolicitarRelatorioPdfComSucesso() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fim = LocalDate.of(2026, 1, 31);

        RelatorioProtocoloResponseDTO response = service.solicitar(
                new SolicitarRelatorioCommand("PDF", inicio, fim, tenantId, "admin@teste.com"));

        assertNotNull(response.protocolo());
        assertEquals(StatusRelatorio.PENDENTE, response.status());

        ArgumentCaptor<RelatorioSolicitacao> solicitacaoCaptor = ArgumentCaptor.forClass(RelatorioSolicitacao.class);
        verify(relatorioStore).save(solicitacaoCaptor.capture());
        RelatorioSolicitacao saved = solicitacaoCaptor.getValue();
        assertEquals(response.protocolo(), saved.protocolo());
        assertEquals(tenantId, saved.tenantId());
        assertEquals("PDF", saved.tipoFormato());
        assertEquals(inicio, saved.dataInicio());
        assertEquals(fim, saved.dataFim());
        assertEquals(StatusRelatorio.PENDENTE, saved.status());

        ArgumentCaptor<RelatorioSolicitacaoMsg> mensagemCaptor = ArgumentCaptor.forClass(RelatorioSolicitacaoMsg.class);
        verify(relatorioProducerPort).publicarSolicitacao(mensagemCaptor.capture());
        RelatorioSolicitacaoMsg mensagem = mensagemCaptor.getValue();
        assertEquals(response.protocolo(), mensagem.protocolo());
        assertEquals(tenantId, mensagem.tenantId());
        assertEquals("PDF", mensagem.tipoFormato());
        assertEquals("admin@teste.com", mensagem.usuario());
    }

    @Test
    @DisplayName("Deve normalizar formatos XLSX e EXCEL para EXCEL")
    void deveNormalizarFormatoExcel() {
        RelatorioProtocoloResponseDTO response = service.solicitar(
                new SolicitarRelatorioCommand("XLSX", LocalDate.now(), LocalDate.now(), tenantId, "usuario"));

        ArgumentCaptor<RelatorioSolicitacao> captor = ArgumentCaptor.forClass(RelatorioSolicitacao.class);
        verify(relatorioStore).save(captor.capture());
        assertEquals("EXCEL", captor.getValue().tipoFormato());
        verify(relatorioProducerPort).publicarSolicitacao(argThat(msg ->
                msg.protocolo().equals(response.protocolo()) && msg.tipoFormato().equals("EXCEL")));
    }

    @Test
    @DisplayName("Deve preencher datas padrão quando início e fim forem nulos")
    void devePreencherDatasDefaultQuandoNulas() {
        LocalDate hoje = LocalDate.now();
        service.solicitar(new SolicitarRelatorioCommand(null, null, null, tenantId, "usuario"));

        ArgumentCaptor<RelatorioSolicitacao> captor = ArgumentCaptor.forClass(RelatorioSolicitacao.class);
        verify(relatorioStore).save(captor.capture());
        assertEquals(hoje.withDayOfMonth(1), captor.getValue().dataInicio());
        assertEquals(hoje, captor.getValue().dataFim());
        assertEquals("PDF", captor.getValue().tipoFormato());
    }

    @Test
    @DisplayName("Deve rejeitar intervalo de datas inválido sem persistir ou publicar")
    void deveRejeitarDatasInvertidas() {
        LocalDate inicio = LocalDate.of(2026, 5, 10);
        LocalDate fim = LocalDate.of(2026, 5, 1);
        SolicitarRelatorioCommand command = new SolicitarRelatorioCommand("PDF", inicio, fim, tenantId, "usuario");

        assertThrows(RegraDeNegocioException.class, () -> service.solicitar(command));
        verifyNoInteractions(relatorioStore, relatorioProducerPort);
    }

    @Test
    @DisplayName("Deve consultar status de protocolo existente")
    void deveConsultarStatusComSucesso() {
        UUID protocolo = UUID.randomUUID();
        when(relatorioStore.findById(protocolo)).thenReturn(Optional.of(solicitacao(
                protocolo, StatusRelatorio.PROCESSANDO, null, null, null, null)));

        RelatorioStatusResponseDTO status = service.consultarStatus(protocolo);

        assertEquals(protocolo, status.protocolo());
        assertEquals(StatusRelatorio.PROCESSANDO, status.status());
        assertEquals("PDF", status.tipoFormato());
    }

    @Test
    @DisplayName("Deve lançar exceção ao consultar protocolo inexistente")
    void deveLancarExcecaoAoConsultarStatusInexistente() {
        UUID protocolo = UUID.randomUUID();
        when(relatorioStore.findById(protocolo)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.consultarStatus(protocolo));
    }

    @Test
    @DisplayName("Deve permitir download de relatório concluído com conteúdo")
    void devePermitirDownloadQuandoStatusConcluido() {
        UUID protocolo = UUID.randomUUID();
        byte[] conteudo = {10, 20, 30};
        when(relatorioStore.findById(protocolo)).thenReturn(Optional.of(solicitacao(
                protocolo, StatusRelatorio.CONCLUIDO, conteudo, "relatorio.pdf", "application/pdf", null)));

        RelatorioDownloadDTO download = service.baixar(protocolo);

        assertArrayEquals(conteudo, download.conteudo());
        assertEquals("relatorio.pdf", download.nomeArquivo());
        assertEquals("application/pdf", download.contentType());
    }

    @Test
    @DisplayName("Deve rejeitar download enquanto relatório estiver em andamento")
    void deveRejeitarDownloadNaoFinalizado() {
        UUID protocolo = UUID.randomUUID();
        when(relatorioStore.findById(protocolo)).thenReturn(Optional.of(solicitacao(
                protocolo, StatusRelatorio.PENDENTE, null, null, null, null)));

        assertThrows(RegraDeNegocioException.class, () -> service.baixar(protocolo));
    }

    @Test
    @DisplayName("Deve propagar mensagem quando geração do relatório falhar")
    void devePropagarFalhaDoRelatorio() {
        UUID protocolo = UUID.randomUUID();
        when(relatorioStore.findById(protocolo)).thenReturn(Optional.of(solicitacao(
                protocolo, StatusRelatorio.ERRO, null, null, null, "Falha na geração do template")));

        RegraDeNegocioException exception = assertThrows(
                RegraDeNegocioException.class, () -> service.baixar(protocolo));
        assertTrue(exception.getMessage().contains("Falha na geração do template"));
    }

    @Test
    @DisplayName("Deve rejeitar download sem conteúdo")
    void deveRejeitarDownloadSemConteudo() {
        UUID protocolo = UUID.randomUUID();
        when(relatorioStore.findById(protocolo)).thenReturn(Optional.of(solicitacao(
                protocolo, StatusRelatorio.CONCLUIDO, null, null, null, null)));

        assertThrows(RegraDeNegocioException.class, () -> service.baixar(protocolo));
    }

    private RelatorioSolicitacao solicitacao(
            UUID protocolo,
            StatusRelatorio status,
            byte[] conteudo,
            String nomeArquivo,
            String contentType,
            String mensagemErro) {
        return new RelatorioSolicitacao(
                protocolo,
                tenantId,
                "PDF",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31),
                status,
                conteudo,
                nomeArquivo,
                contentType,
                mensagemErro,
                LocalDateTime.now().minusMinutes(1),
                null);
    }
}
