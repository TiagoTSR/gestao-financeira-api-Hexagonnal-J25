package com.decodex.br.application.dto.relatorio;

import com.decodex.br.domain.model.StatusRelatorio;

import java.util.UUID;

public record RelatorioProtocoloResponseDTO(
        UUID protocolo,
        StatusRelatorio status,
        String mensagem
) {
}
