package com.decodex.br.adapters.in.web.documentation;

import java.time.LocalDate;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Relatórios", description = "Endpoints para geração de relatórios estatísticos e exportação")
public interface RelatorioControllerDoc {

    @Operation(summary = "Gerar relatório detalhado em PDF", description = "Gera um relatório detalhado em PDF com os lançamentos individuais e consolidado por pessoa dentro do período.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Relatório em PDF gerado com sucesso", content = @Content(mediaType = "application/pdf", schema = @Schema(type = "string", format = "binary"))),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content)
    })
    ResponseEntity<byte[]> relatorioPorPessoa(
            @Parameter(description = "Data de início do intervalo", required = true) LocalDate inicio,
            @Parameter(description = "Data de fim do intervalo", required = true) LocalDate fim);

    @Operation(summary = "Exportar relatório detalhado em Excel (.xlsx)", description = "Gera uma planilha Excel (.xlsx) altamente formatada com lançamentos detalhados, indicadores financeiros e consolidação por pessoa.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Planilha Excel gerada com sucesso", content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", schema = @Schema(type = "string", format = "binary"))),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content)
    })
    ResponseEntity<byte[]> relatorioPorPessoaExcel(
            @Parameter(description = "Data de início do intervalo", required = true) LocalDate inicio,
            @Parameter(description = "Data de fim do intervalo", required = true) LocalDate fim);
}
