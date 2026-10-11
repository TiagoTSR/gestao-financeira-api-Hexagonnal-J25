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

    @Operation(summary = "Solicitar geração assíncrona de relatório", description = "Enfileira a solicitação no RabbitMQ e retorna um protocolo para acompanhamento do processamento.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Solicitação aceita e enfileirada no RabbitMQ"),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos na solicitação")
    })
    ResponseEntity<com.decodex.br.application.dto.relatorio.RelatorioProtocoloResponseDTO> solicitarRelatorio(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados para solicitação do relatório")
            com.decodex.br.application.dto.relatorio.RelatorioSolicitacaoRequestDTO request);

    @Operation(summary = "Consultar status do relatório assíncrono", description = "Consulta o andamento do processamento do relatório pelo número do protocolo.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status do relatório consultado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Protocolo não encontrado")
    })
    ResponseEntity<com.decodex.br.application.dto.relatorio.RelatorioStatusResponseDTO> consultarStatus(
            @Parameter(description = "Protocolo UUID da solicitação", required = true) java.util.UUID protocolo);

    @Operation(summary = "Download do relatório por protocolo", description = "Efetua o download do binário (PDF ou Excel) gerado de forma assíncrona se o status estiver CONCLUIDO.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Download do arquivo realizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Relatório ainda pendente/processando ou com erro"),
        @ApiResponse(responseCode = "404", description = "Protocolo não encontrado")
    })
    ResponseEntity<byte[]> baixarRelatorio(
            @Parameter(description = "Protocolo UUID da solicitação", required = true) java.util.UUID protocolo);

    @Operation(summary = "Painel visual para gestão e download assíncrono", description = "Interface web moderna para solicitação, acompanhamento em tempo real e download de relatórios.")
    @ApiResponse(responseCode = "200", description = "Página HTML do painel carregada")
    ResponseEntity<String> painelVisual();
}

