package com.decodex.br.features.relatorios.application.port.out;

import com.decodex.br.features.relatorios.application.model.RelatorioRequestMessage;

public interface RelatorioRequestPublisher {

    void publicar(RelatorioRequestMessage message);
}
