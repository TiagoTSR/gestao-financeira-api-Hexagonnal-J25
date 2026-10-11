package com.decodex.br.testesunitarios.adapters.in.web;

import com.decodex.br.adapters.in.web.RelatorioController;
import com.decodex.br.application.dto.relatorio.RelatorioDownloadDTO;
import com.decodex.br.application.dto.relatorio.RelatorioProtocoloResponseDTO;
import com.decodex.br.application.dto.relatorio.RelatorioSolicitacaoRequestDTO;
import com.decodex.br.application.dto.relatorio.RelatorioStatusResponseDTO;
import com.decodex.br.domain.model.StatusRelatorio;
import com.decodex.br.domain.port.in.GerarRelatorioEstatisticaInputPort;
import com.decodex.br.domain.port.in.RelatorioAsyncInputPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - RelatorioController")
class RelatorioControllerTest {

    @Mock
    private GerarRelatorioEstatisticaInputPort gerarRelatorioInputPort;

    @Mock
    private RelatorioAsyncInputPort relatorioAsyncInputPort;

    @InjectMocks
    private RelatorioController controller;

    @Test
    @DisplayName("Deve retornar ResponseEntity com PDF e headers apropriados")
    void deveRetornarRelatorioPdf() {
        LocalDate inicio = LocalDate.of(2026, 10, 1);
        LocalDate fim = LocalDate.of(2026, 10, 31);
        byte[] pdfBytes = "%PDF-1.4 test".getBytes();

        when(gerarRelatorioInputPort.executarPorPessoa(inicio, fim)).thenReturn(pdfBytes);

        ResponseEntity<byte[]> response = controller.relatorioPorPessoa(inicio, fim);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(MediaType.APPLICATION_PDF_VALUE, response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
        assertTrue(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION).contains("filename=relatorio-lancamentos_2026-10-01_2026-10-31.pdf"));
        assertArrayEquals(pdfBytes, response.getBody());
    }

    @Test
    @DisplayName("Deve retornar ResponseEntity com XLSX e headers apropriados")
    void deveRetornarRelatorioExcel() {
        LocalDate inicio = LocalDate.of(2026, 10, 1);
        LocalDate fim = LocalDate.of(2026, 10, 31);
        byte[] excelBytes = new byte[]{1, 2, 3, 4};

        when(gerarRelatorioInputPort.executarPorPessoaExcel(inicio, fim)).thenReturn(excelBytes);

        ResponseEntity<byte[]> response = controller.relatorioPorPessoaExcel(inicio, fim);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
        assertTrue(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION).contains("filename=relatorio-lancamentos_2026-10-01_2026-10-31.xlsx"));
        assertArrayEquals(excelBytes, response.getBody());
    }

    @Test
    @DisplayName("Deve aceitar solicitação assíncrona e retornar 202 com protocolo")
    void deveSolicitarRelatorioAssincrono() {
        UUID protocolo = UUID.randomUUID();
        RelatorioSolicitacaoRequestDTO request = new RelatorioSolicitacaoRequestDTO("PDF", LocalDate.now(), LocalDate.now());
        RelatorioProtocoloResponseDTO dto = new RelatorioProtocoloResponseDTO(protocolo, StatusRelatorio.PENDENTE, "Solicitado com sucesso");

        when(relatorioAsyncInputPort.solicitar(request)).thenReturn(dto);

        ResponseEntity<RelatorioProtocoloResponseDTO> response = controller.solicitarRelatorio(request);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(protocolo, response.getBody().protocolo());
    }

    @Test
    @DisplayName("Deve consultar status do relatório por protocolo")
    void deveConsultarStatusDoRelatorio() {
        UUID protocolo = UUID.randomUUID();
        RelatorioStatusResponseDTO statusDTO = new RelatorioStatusResponseDTO(
                protocolo, "PDF", StatusRelatorio.CONCLUIDO, null, LocalDateTime.now(), LocalDateTime.now()
        );

        when(relatorioAsyncInputPort.consultarStatus(protocolo)).thenReturn(statusDTO);

        ResponseEntity<RelatorioStatusResponseDTO> response = controller.consultarStatus(protocolo);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(StatusRelatorio.CONCLUIDO, response.getBody().status());
    }

    @Test
    @DisplayName("Deve realizar download do arquivo do relatório por protocolo")
    void deveBaixarRelatorioPorProtocolo() {
        UUID protocolo = UUID.randomUUID();
        byte[] bytes = new byte[]{10, 20, 30};
        RelatorioDownloadDTO downloadDTO = new RelatorioDownloadDTO(bytes, "relatorio.pdf", "application/pdf");

        when(relatorioAsyncInputPort.baixar(protocolo)).thenReturn(downloadDTO);

        ResponseEntity<byte[]> response = controller.baixarRelatorio(protocolo);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("application/pdf", response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
        assertTrue(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION).contains("relatorio.pdf"));
        assertArrayEquals(bytes, response.getBody());
    }

    @Test
    @DisplayName("Deve renderizar página HTML do painel visual")
    void deveRetornarPainelVisualHtml() {
        ResponseEntity<String> response = controller.painelVisual();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Geração Assíncrona de Relatórios"));
        assertTrue(response.getBody().contains("RabbitMQ"));
        assertTrue(response.getBody().contains("relatorios.dlq"));
    }
}
