package com.decodex.br.features.relatorios.infrastructure.transaction;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.decodex.br.application.dto.relatorio.RelatorioDownloadDTO;
import com.decodex.br.application.dto.relatorio.RelatorioProtocoloResponseDTO;
import com.decodex.br.application.dto.relatorio.RelatorioStatusResponseDTO;
import com.decodex.br.features.relatorios.application.RelatorioAsyncApplicationService;
import com.decodex.br.features.relatorios.application.model.SolicitarRelatorioCommand;
import com.decodex.br.features.relatorios.application.port.in.RelatorioAsyncUseCase;

@Component
public class TransactionalRelatorioAsyncUseCase implements RelatorioAsyncUseCase {

    private final RelatorioAsyncApplicationService applicationService;

    public TransactionalRelatorioAsyncUseCase(RelatorioAsyncApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Override
    @Transactional
    public RelatorioProtocoloResponseDTO solicitar(SolicitarRelatorioCommand command) {
        return applicationService.solicitar(command);
    }

    @Override
    @Transactional(readOnly = true)
    public RelatorioStatusResponseDTO consultarStatus(UUID protocolo) {
        return applicationService.consultarStatus(protocolo);
    }

    @Override
    @Transactional(readOnly = true)
    public RelatorioDownloadDTO baixar(UUID protocolo) {
        return applicationService.baixar(protocolo);
    }
}
