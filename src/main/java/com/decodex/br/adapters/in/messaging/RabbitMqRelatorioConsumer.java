package com.decodex.br.adapters.in.messaging;

import com.decodex.br.application.dto.relatorio.RelatorioSolicitacaoMsg;
import com.decodex.br.config.multitenancy.TenantContext;
import com.decodex.br.config.rabbitmq.RabbitMqConfig;
import com.decodex.br.features.relatorios.application.model.ProcessarRelatorioCommand;
import com.decodex.br.features.relatorios.application.port.in.ProcessarRelatorioUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqRelatorioConsumer {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqRelatorioConsumer.class);

    private final ProcessarRelatorioUseCase processarRelatorioUseCase;

    public RabbitMqRelatorioConsumer(ProcessarRelatorioUseCase processarRelatorioUseCase) {
        this.processarRelatorioUseCase = processarRelatorioUseCase;
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE_RELATORIOS)
    public void processarSolicitacao(RelatorioSolicitacaoMsg msg) {
        log.info("Iniciando processamento assíncrono do relatório. Protocolo: {}, Formato: {}, Tenant: {}",
                msg.protocolo(), msg.tipoFormato(), msg.tenantId());

        TenantContext.setTenantId(msg.tenantId());
        try {
            boolean processado = processarRelatorioUseCase.processar(new ProcessarRelatorioCommand(
                    msg.protocolo(),
                    msg.tenantId(),
                    msg.tipoFormato(),
                    msg.dataInicio(),
                    msg.dataFim()));
            if (!processado) {
                log.warn("Solicitação não encontrada no banco para protocolo: {}", msg.protocolo());
            }
        } finally {
            TenantContext.clear();
        }
    }
}
