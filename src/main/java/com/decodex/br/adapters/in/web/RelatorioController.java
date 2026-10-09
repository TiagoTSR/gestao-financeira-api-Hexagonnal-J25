package com.decodex.br.adapters.in.web;

import com.decodex.br.adapters.in.web.documentation.RelatorioControllerDoc;
import com.decodex.br.domain.port.in.GerarRelatorioEstatisticaInputPort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping({"/api/relatorios", "/relatorios", "/lancamentos/relatorios"})
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class RelatorioController implements RelatorioControllerDoc {

    private static final String EXCEL_MEDIA_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final GerarRelatorioEstatisticaInputPort gerarRelatorioInputPort;

    public RelatorioController(GerarRelatorioEstatisticaInputPort gerarRelatorioInputPort) {
        this.gerarRelatorioInputPort = gerarRelatorioInputPort;
    }

    @Override
    @GetMapping({"/lancamentos-por-pessoa", "/por-pessoa"})
    public ResponseEntity<byte[]> relatorioPorPessoa(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        byte[] relatorio = gerarRelatorioInputPort.executarPorPessoa(inicio, fim);

        String filename = String.format("relatorio-lancamentos_%s_%s.pdf", inicio, fim);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(relatorio);
    }

    @Override
    @GetMapping({"/lancamentos-por-pessoa/excel", "/por-pessoa/excel", "/lancamentos-excel"})
    public ResponseEntity<byte[]> relatorioPorPessoaExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        byte[] relatorio = gerarRelatorioInputPort.executarPorPessoaExcel(inicio, fim);

        String filename = String.format("relatorio-lancamentos_%s_%s.xlsx", inicio, fim);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, EXCEL_MEDIA_TYPE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(relatorio);
    }
}