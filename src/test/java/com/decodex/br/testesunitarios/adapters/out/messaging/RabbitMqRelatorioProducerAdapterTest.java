package com.decodex.br.testesunitarios.adapters.out.messaging;

import com.decodex.br.adapters.out.messaging.RabbitMqRelatorioProducerAdapter;
import com.decodex.br.application.dto.relatorio.RelatorioSolicitacaoMsg;
import com.decodex.br.config.rabbitmq.RabbitMqConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - RabbitMqRelatorioProducerAdapter")
class RabbitMqRelatorioProducerAdapterTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RabbitMqRelatorioProducerAdapter adapter;

    @Test
    @DisplayName("Deve publicar mensagem no exchange e routing key corretos do RabbitMQ")
    void devePublicarMensagemNoRabbitMq() {
        RelatorioSolicitacaoMsg msg = new RelatorioSolicitacaoMsg(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "PDF",
                LocalDate.now(),
                LocalDate.now(),
                "admin@teste.com"
        );

        adapter.publicarSolicitacao(msg);

        verify(rabbitTemplate).convertAndSend(
                RabbitMqConfig.EXCHANGE_RELATORIOS,
                RabbitMqConfig.ROUTING_KEY_RELATORIOS,
                msg
        );
    }
}
