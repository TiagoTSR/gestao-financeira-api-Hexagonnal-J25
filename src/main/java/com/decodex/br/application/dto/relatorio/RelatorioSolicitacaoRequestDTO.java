package com.decodex.br.application.dto.relatorio;

import java.time.LocalDate;

public record RelatorioSolicitacaoRequestDTO(
        String tipo,
        LocalDate inicio,
        LocalDate fim
) {
}
