package com.decodex.br.features.relatorios.application;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.decodex.br.domain.exception.RegraDeNegocioException;
import com.decodex.br.domain.exception.ResourceNotFoundException;
import com.decodex.br.domain.model.StatusRelatorio;
import com.decodex.br.features.relatorios.application.model.RelatorioFile;
import com.decodex.br.features.relatorios.application.model.RelatorioRequestMessage;
import com.decodex.br.features.relatorios.application.model.RelatorioRequestResult;
import com.decodex.br.features.relatorios.application.model.RelatorioStatusView;
import com.decodex.br.features.relatorios.application.model.RelatorioSolicitacao;
import com.decodex.br.features.relatorios.application.model.SolicitarRelatorioCommand;
import com.decodex.br.features.relatorios.application.port.in.RelatorioAsyncUseCase;
import com.decodex.br.features.relatorios.application.port.out.RelatorioSolicitacaoStore;
import com.decodex.br.features.relatorios.application.port.out.RelatorioRequestPublisher;

public class RelatorioAsyncApplicationService implements RelatorioAsyncUseCase {

    private final RelatorioSolicitacaoStore relatorioStore;
    private final RelatorioRequestPublisher relatorioRequestPublisher;

    public RelatorioAsyncApplicationService(
            RelatorioSolicitacaoStore relatorioStore,
            RelatorioRequestPublisher relatorioRequestPublisher) {
        this.relatorioStore = relatorioStore;
        this.relatorioRequestPublisher = relatorioRequestPublisher;
    }

    @Override
    public RelatorioRequestResult solicitar(SolicitarRelatorioCommand command) {
        LocalDate hoje = LocalDate.now();
        LocalDate inicio = command.inicio() != null ? command.inicio() : hoje.withDayOfMonth(1);
        LocalDate fim = command.fim() != null ? command.fim() : hoje;

        if (inicio.isAfter(fim)) {
            throw new RegraDeNegocioException("A data de início não pode ser posterior à data de fim.");
        }

        String formato = command.tipo() != null
                && (command.tipo().equalsIgnoreCase("EXCEL") || command.tipo().equalsIgnoreCase("XLSX"))
                        ? "EXCEL"
                        : "PDF";
        UUID protocolo = UUID.randomUUID();

        RelatorioSolicitacao solicitacao = new RelatorioSolicitacao(
                protocolo,
                command.tenantId(),
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
        relatorioStore.save(solicitacao);

        RelatorioRequestMessage mensagem = new RelatorioRequestMessage(
                protocolo,
                command.tenantId(),
                formato,
                inicio,
                fim,
                command.usuario());
        relatorioRequestPublisher.publicar(mensagem);

        return new RelatorioRequestResult(
                protocolo,
                StatusRelatorio.PENDENTE,
                "Relatório solicitado com sucesso. Acompanhe o processamento utilizando o protocolo fornecido.");
    }

    @Override
    public RelatorioStatusView consultarStatus(UUID protocolo) {
        RelatorioSolicitacao solicitacao = buscarSolicitacao(protocolo);
        return new RelatorioStatusView(
                solicitacao.protocolo(),
                solicitacao.tipoFormato(),
                solicitacao.status(),
                solicitacao.mensagemErro(),
                solicitacao.dataSolicitacao(),
                solicitacao.dataConclusao());
    }

    @Override
    public RelatorioFile baixar(UUID protocolo) {
        RelatorioSolicitacao solicitacao = buscarSolicitacao(protocolo);

        if (solicitacao.status() != null && solicitacao.status().isEmAndamento()) {
            throw new RegraDeNegocioException("O relatório ainda está em processamento. Consulte novamente em instantes.");
        }

        if (solicitacao.status() == StatusRelatorio.ERRO) {
            throw new RegraDeNegocioException("Não foi possível gerar o relatório: "
                    + (solicitacao.mensagemErro() != null
                            ? solicitacao.mensagemErro()
                            : "Erro interno durante a geração"));
        }

        if (solicitacao.conteudo() == null || solicitacao.conteudo().length == 0) {
            throw new RegraDeNegocioException("Arquivo de relatório não encontrado para download.");
        }

        return new RelatorioFile(
                solicitacao.conteudo(),
                solicitacao.nomeArquivo(),
                solicitacao.contentType());
    }

    private RelatorioSolicitacao buscarSolicitacao(UUID protocolo) {
        return relatorioStore.findById(protocolo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Solicitação de relatório não encontrada para o protocolo: " + protocolo));
    }
}
