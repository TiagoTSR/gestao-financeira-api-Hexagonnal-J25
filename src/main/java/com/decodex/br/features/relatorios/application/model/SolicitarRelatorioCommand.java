package com.decodex.br.features.relatorios.application.model;

import java.time.LocalDate;
import java.util.UUID;

public record SolicitarRelatorioCommand(
        String tipo,
        LocalDate inicio,
        LocalDate fim,
        UUID tenantId,
        String usuario) {
}
