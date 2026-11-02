package com.decodex.br.features.relatorios.application.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.decodex.br.domain.model.StatusRelatorio;

public record RelatorioStatusView(
        UUID protocolo,
        String tipoFormato,
        StatusRelatorio status,
        String mensagemErro,
        LocalDateTime dataSolicitacao,
        LocalDateTime dataConclusao) {
}
