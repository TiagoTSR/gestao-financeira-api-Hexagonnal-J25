package com.decodex.br.features.relatorios.application.port.in;

import java.util.UUID;

import com.decodex.br.features.relatorios.application.model.RelatorioFile;
import com.decodex.br.features.relatorios.application.model.RelatorioRequestResult;
import com.decodex.br.features.relatorios.application.model.RelatorioStatusView;
import com.decodex.br.features.relatorios.application.model.SolicitarRelatorioCommand;

public interface RelatorioAsyncUseCase {

    RelatorioRequestResult solicitar(SolicitarRelatorioCommand command);

    RelatorioStatusView consultarStatus(UUID protocolo);

    RelatorioFile baixar(UUID protocolo);
}
