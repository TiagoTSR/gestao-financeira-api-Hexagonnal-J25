package com.decodex.br.domain.port.in;

import com.decodex.br.application.dto.relatorio.RelatorioDownloadDTO;
import com.decodex.br.application.dto.relatorio.RelatorioProtocoloResponseDTO;
import com.decodex.br.application.dto.relatorio.RelatorioSolicitacaoRequestDTO;
import com.decodex.br.application.dto.relatorio.RelatorioStatusResponseDTO;

import java.util.UUID;

public interface RelatorioAsyncInputPort {
    RelatorioProtocoloResponseDTO solicitar(RelatorioSolicitacaoRequestDTO request);
    RelatorioStatusResponseDTO consultarStatus(UUID protocolo);
    RelatorioDownloadDTO baixar(UUID protocolo);
}
