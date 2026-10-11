package com.decodex.br.adapters.out.messaging;

import com.decodex.br.application.dto.relatorio.RelatorioSolicitacaoMsg;
import com.decodex.br.config.rabbitmq.RabbitMqConfig;
import com.decodex.br.domain.port.out.RelatorioProducerPort;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqRelatorioProducerAdapter implements RelatorioProducerPort {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMqRelatorioProducerAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publicarSolicitacao(RelatorioSolicitacaoMsg mensagem) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_RELATORIOS,
                RabbitMqConfig.ROUTING_KEY_RELATORIOS,
                mensagem
        );
    }
}
