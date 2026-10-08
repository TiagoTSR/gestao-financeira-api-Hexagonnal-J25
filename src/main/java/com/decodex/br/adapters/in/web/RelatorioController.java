package com.decodex.br.adapters.in.web;

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
import com.decodex.br.adapters.in.web.documentation.RelatorioControllerDoc;

@RestController
@RequestMapping("/api/relatorios")
@CrossOrigin("http://localhost:4200")
public class RelatorioController implements RelatorioControllerDoc {

    private final GerarRelatorioEstatisticaInputPort gerarRelatorioInputPort;

    public RelatorioController(GerarRelatorioEstatisticaInputPort gerarRelatorioInputPort) {
        this.gerarRelatorioInputPort = gerarRelatorioInputPort;
    }

    @GetMapping("/lancamentos-por-pessoa")
    public ResponseEntity<byte[]> relatorioPorPessoa(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        byte[] relatorio = gerarRelatorioInputPort.executarPorPessoa(inicio, fim);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=lancamentos-pessoa.pdf")
                .body(relatorio);
    }
}