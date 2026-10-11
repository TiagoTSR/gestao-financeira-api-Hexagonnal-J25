package com.decodex.br.testesunitarios.adapters.in.messaging;

import com.decodex.br.adapters.in.messaging.RabbitMqRelatorioDlqConsumer;
import com.decodex.br.application.dto.relatorio.RelatorioSolicitacaoMsg;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - RabbitMqRelatorioDlqConsumer")
class RabbitMqRelatorioDlqConsumerTest {

    @InjectMocks
    private RabbitMqRelatorioDlqConsumer dlqConsumer;

    @Test
    @DisplayName("Deve registrar log e não lançar exceções ao processar mensagem na DLQ")
    void deveProcessarMensagemDlqSemExcecao() {
        RelatorioSolicitacaoMsg msg = new RelatorioSolicitacaoMsg(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "PDF",
                LocalDate.now(),
                LocalDate.now(),
                "admin"
        );

        assertDoesNotThrow(() -> dlqConsumer.processarDlq(msg));
    }
}
