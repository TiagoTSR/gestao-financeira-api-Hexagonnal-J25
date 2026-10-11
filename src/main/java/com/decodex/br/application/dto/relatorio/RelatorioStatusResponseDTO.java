package com.decodex.br.application.dto.relatorio;

import com.decodex.br.domain.model.StatusRelatorio;

import java.time.LocalDateTime;
import java.util.UUID;

public record RelatorioStatusResponseDTO(
        UUID protocolo,
        String tipoFormato,
        StatusRelatorio status,
        String mensagemErro,
        LocalDateTime dataSolicitacao,
        LocalDateTime dataConclusao
) {
}
