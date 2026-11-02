package com.decodex.br.features.relatorios.application;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.decodex.br.domain.model.StatusRelatorio;
import com.decodex.br.features.relatorios.application.model.ProcessarRelatorioCommand;
import com.decodex.br.features.relatorios.application.model.RelatorioSolicitacao;
import com.decodex.br.features.relatorios.application.port.in.ProcessarRelatorioUseCase;
import com.decodex.br.features.relatorios.application.port.out.RelatorioFileGenerator;
import com.decodex.br.features.relatorios.application.port.out.RelatorioSolicitacaoStore;

public class RelatorioProcessingApplicationService implements ProcessarRelatorioUseCase {

    private static final Logger log = LoggerFactory.getLogger(RelatorioProcessingApplicationService.class);
    private static final String EXCEL_CONTENT_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final RelatorioSolicitacaoStore relatorioStore;
    private final RelatorioFileGenerator relatorioFileGenerator;

    public RelatorioProcessingApplicationService(
            RelatorioSolicitacaoStore relatorioStore,
            RelatorioFileGenerator relatorioFileGenerator) {
        this.relatorioStore = relatorioStore;
        this.relatorioFileGenerator = relatorioFileGenerator;
    }

    @Override
    public boolean processar(ProcessarRelatorioCommand command) {
        try {
            Optional<RelatorioSolicitacao> optionalSolicitacao =
                    relatorioStore.findById(command.protocolo());
            if (optionalSolicitacao.isEmpty()) {
                return false;
            }

            RelatorioSolicitacao solicitacao = optionalSolicitacao.get();
            relatorioStore.save(comStatus(solicitacao, StatusRelatorio.PROCESSANDO));

            boolean excel = "EXCEL".equalsIgnoreCase(command.tipoFormato());
            byte[] conteudo = excel
                    ? relatorioFileGenerator.gerarExcel(command.dataInicio(), command.dataFim())
                    : relatorioFileGenerator.gerarPdf(command.dataInicio(), command.dataFim());
            String extensao = excel ? "xlsx" : "pdf";
            String contentType = excel ? EXCEL_CONTENT_TYPE : "application/pdf";
            String nomeArquivo = String.format(
                    "relatorio-lancamentos_%s_%s.%s",
                    command.dataInicio(),
                    command.dataFim(),
                    extensao);

            relatorioStore.save(new RelatorioSolicitacao(
                    solicitacao.protocolo(),
                    solicitacao.tenantId(),
                    solicitacao.tipoFormato(),
                    solicitacao.dataInicio(),
                    solicitacao.dataFim(),
                    StatusRelatorio.CONCLUIDO,
                    conteudo,
                    nomeArquivo,
                    contentType,
                    null,
                    solicitacao.dataSolicitacao(),
                    LocalDateTime.now()));

            log.info("Relatório gerado com sucesso para protocolo: {}. Tamanho: {} bytes",
                    command.protocolo(), conteudo.length);
            return true;
        } catch (RuntimeException exception) {
            registrarFalha(command, exception);
            throw exception;
        }
    }

    private void registrarFalha(ProcessarRelatorioCommand command, RuntimeException exception) {
        log.error("Erro durante o processamento do relatório {}: {}",
                command.protocolo(), exception.getMessage(), exception);
        try {
            relatorioStore.findById(command.protocolo()).ifPresent(solicitacao ->
                    relatorioStore.save(new RelatorioSolicitacao(
                            solicitacao.protocolo(),
                            solicitacao.tenantId(),
                            solicitacao.tipoFormato(),
                            solicitacao.dataInicio(),
                            solicitacao.dataFim(),
                            StatusRelatorio.ERRO,
                            solicitacao.conteudo(),
                            solicitacao.nomeArquivo(),
                            solicitacao.contentType(),
                            exception.getMessage() != null
                                    ? exception.getMessage()
                                    : "Erro inesperado na geração",
                            solicitacao.dataSolicitacao(),
                            LocalDateTime.now())));
        } catch (RuntimeException persistenceException) {
            log.error("Falha ao registrar status de ERRO para protocolo {}: {}",
                    command.protocolo(), persistenceException.getMessage(), persistenceException);
        }
    }

    private RelatorioSolicitacao comStatus(RelatorioSolicitacao solicitacao, StatusRelatorio status) {
        return new RelatorioSolicitacao(
                solicitacao.protocolo(),
                solicitacao.tenantId(),
                solicitacao.tipoFormato(),
                solicitacao.dataInicio(),
                solicitacao.dataFim(),
                status,
                solicitacao.conteudo(),
                solicitacao.nomeArquivo(),
                solicitacao.contentType(),
                solicitacao.mensagemErro(),
                solicitacao.dataSolicitacao(),
                solicitacao.dataConclusao());
    }
}
