package com.decodex.br.features.relatorios.application.model;

import java.util.UUID;

public record RelatorioRequester(UUID tenantId, String usuario) {
}
