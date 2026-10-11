package com.decodex.br.application.dto.relatorio;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public record RelatorioSolicitacaoMsg(
        UUID protocolo,
        UUID tenantId,
        String tipoFormato,
        LocalDate dataInicio,
        LocalDate dataFim,
        String usuario
) implements Serializable {
}
