package com.decodex.br.features.relatorios.application.port.in;

import java.util.UUID;

import com.decodex.br.application.dto.relatorio.RelatorioDownloadDTO;
import com.decodex.br.application.dto.relatorio.RelatorioProtocoloResponseDTO;
import com.decodex.br.application.dto.relatorio.RelatorioStatusResponseDTO;
import com.decodex.br.features.relatorios.application.model.SolicitarRelatorioCommand;

public interface RelatorioAsyncUseCase {

    RelatorioProtocoloResponseDTO solicitar(SolicitarRelatorioCommand command);

    RelatorioStatusResponseDTO consultarStatus(UUID protocolo);

    RelatorioDownloadDTO baixar(UUID protocolo);
}
