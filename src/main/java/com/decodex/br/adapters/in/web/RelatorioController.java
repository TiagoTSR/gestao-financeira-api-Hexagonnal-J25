package com.decodex.br.adapters.in.web;

import com.decodex.br.domain.port.in.GerarRelatorioEstatisticaUseCase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import com.decodex.br.adapters.in.web.documentation.RelatorioControllerDoc;

@RestController
@RequestMapping("/api/relatorios")
@CrossOrigin("http://localhost:4200")
public class RelatorioController implements RelatorioControllerDoc {

    private final GerarRelatorioEstatisticaUseCase gerarRelatorioUseCase;

    public RelatorioController(GerarRelatorioEstatisticaUseCase gerarRelatorioUseCase) {
        this.gerarRelatorioUseCase = gerarRelatorioUseCase;
    }

    @GetMapping("/lancamentos-por-pessoa")
    public ResponseEntity<byte[]> relatorioPorPessoa(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        byte[] relatorio = gerarRelatorioUseCase.executarPorPessoa(inicio, fim);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=lancamentos-pessoa.pdf")
                .body(relatorio);
    }
}