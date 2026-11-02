package com.decodex.br.features.relatorios.infrastructure.messaging;

import org.springframework.stereotype.Component;

import com.decodex.br.application.dto.relatorio.RelatorioSolicitacaoMsg;
import com.decodex.br.domain.port.out.RelatorioProducerPort;
import com.decodex.br.features.relatorios.application.model.RelatorioRequestMessage;
import com.decodex.br.features.relatorios.application.port.out.RelatorioRequestPublisher;

@Component
public class LegacyRelatorioRequestPublisherAdapter implements RelatorioRequestPublisher {

    private final RelatorioProducerPort producer;

    public LegacyRelatorioRequestPublisherAdapter(RelatorioProducerPort producer) {
        this.producer = producer;
    }

    @Override
    public void publicar(RelatorioRequestMessage message) {
        producer.publicarSolicitacao(new RelatorioSolicitacaoMsg(
                message.protocolo(),
                message.tenantId(),
                message.tipoFormato(),
                message.dataInicio(),
                message.dataFim(),
                message.usuario()));
    }
}
