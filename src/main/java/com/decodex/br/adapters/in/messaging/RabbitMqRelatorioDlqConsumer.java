package com.decodex.br.adapters.in.messaging;

import com.decodex.br.application.dto.relatorio.RelatorioSolicitacaoMsg;
import com.decodex.br.config.rabbitmq.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqRelatorioDlqConsumer {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqRelatorioDlqConsumer.class);

    @RabbitListener(queues = RabbitMqConfig.DLQ_RELATORIOS)
    public void processarDlq(RelatorioSolicitacaoMsg msg) {
        log.error("Dead Letter Queue (DLQ) alert: Mensagem de relatório não pôde ser processada após retentativas. Protocolo: {}, Formato: {}, Tenant: {}, Usuário: {}",
                msg.protocolo(), msg.tipoFormato(), msg.tenantId(), msg.usuario());
    }
}
