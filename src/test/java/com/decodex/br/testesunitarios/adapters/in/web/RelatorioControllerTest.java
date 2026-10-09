package com.decodex.br.testesunitarios.adapters.in.web;

import com.decodex.br.adapters.in.web.RelatorioController;
import com.decodex.br.domain.port.in.GerarRelatorioEstatisticaInputPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - RelatorioController")
class RelatorioControllerTest {

    @Mock
    private GerarRelatorioEstatisticaInputPort gerarRelatorioInputPort;

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
}
