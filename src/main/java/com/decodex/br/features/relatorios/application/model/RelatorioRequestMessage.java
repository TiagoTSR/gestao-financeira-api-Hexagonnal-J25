package com.decodex.br.features.relatorios.application.model;

import java.time.LocalDate;
import java.util.UUID;

public record RelatorioRequestMessage(
        UUID protocolo,
        UUID tenantId,
        String tipoFormato,
        LocalDate dataInicio,
        LocalDate dataFim,
        String usuario) {
}
