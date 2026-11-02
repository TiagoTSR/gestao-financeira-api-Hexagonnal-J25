package com.decodex.br.features.relatorios.application.model;

import java.time.LocalDate;
import java.util.UUID;

public record ProcessarRelatorioCommand(
        UUID protocolo,
        UUID tenantId,
        String tipoFormato,
        LocalDate dataInicio,
        LocalDate dataFim) {
}
