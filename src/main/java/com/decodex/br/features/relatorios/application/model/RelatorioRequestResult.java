package com.decodex.br.features.relatorios.application.model;

import java.util.UUID;

import com.decodex.br.domain.model.StatusRelatorio;

public record RelatorioRequestResult(UUID protocolo, StatusRelatorio status, String mensagem) {
}
