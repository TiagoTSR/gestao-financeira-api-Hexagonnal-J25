package com.decodex.br.features.relatorios.application.port.in;

import com.decodex.br.features.relatorios.application.model.ProcessarRelatorioCommand;

public interface ProcessarRelatorioUseCase {

    boolean processar(ProcessarRelatorioCommand command);
}
