package com.decodex.br.features.relatorios.application.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.decodex.br.domain.model.StatusRelatorio;

public record RelatorioSolicitacao(
        UUID protocolo,
        UUID tenantId,
        String tipoFormato,
        LocalDate dataInicio,
        LocalDate dataFim,
        StatusRelatorio status,
        byte[] conteudo,
        String nomeArquivo,
        String contentType,
        String mensagemErro,
        LocalDateTime dataSolicitacao,
        LocalDateTime dataConclusao) {
}
