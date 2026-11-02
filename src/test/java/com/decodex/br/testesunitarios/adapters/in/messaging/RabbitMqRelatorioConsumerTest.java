package com.decodex.br.testesunitarios.adapters.in.messaging;

import com.decodex.br.adapters.in.messaging.RabbitMqRelatorioConsumer;
import com.decodex.br.application.dto.relatorio.RelatorioSolicitacaoMsg;
import com.decodex.br.config.multitenancy.TenantContext;
import com.decodex.br.features.relatorios.application.model.ProcessarRelatorioCommand;
import com.decodex.br.features.relatorios.application.port.in.ProcessarRelatorioUseCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - RabbitMqRelatorioConsumer")
class RabbitMqRelatorioConsumerTest {

    @Mock
    private ProcessarRelatorioUseCase processarRelatorioUseCase;

    @AfterEach
    void limparTenantContext() {
        TenantContext.clear();
    }

    @Test
    @DisplayName("Deve delegar o processamento com os dados da mensagem e limpar o tenant")
    void deveDelegarProcessamentoEPropagarTenant() {
        UUID protocolo = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fim = LocalDate.of(2026, 1, 31);
        RelatorioSolicitacaoMsg mensagem = new RelatorioSolicitacaoMsg(
                protocolo, tenantId, "PDF", inicio, fim, "usuario");
        RabbitMqRelatorioConsumer consumer = new RabbitMqRelatorioConsumer(processarRelatorioUseCase);

        when(processarRelatorioUseCase.processar(any())).thenAnswer(invocation -> {
            assertEquals(tenantId, TenantContext.getTenantId());
            return true;
        });

        consumer.processarSolicitacao(mensagem);

        verify(processarRelatorioUseCase).processar(
                new ProcessarRelatorioCommand(protocolo, tenantId, "PDF", inicio, fim));
        assertEquals(TenantContext.DEFAULT_TENANT_ID, TenantContext.getTenantId());
    }

    @Test
    @DisplayName("Deve relançar falhas de processamento e limpar o tenant")
    void deveRelancarFalhaELimparTenant() {
        UUID tenantId = UUID.randomUUID();
        RelatorioSolicitacaoMsg mensagem = new RelatorioSolicitacaoMsg(
                UUID.randomUUID(), tenantId, "PDF", LocalDate.now(), LocalDate.now(), "usuario");
        RabbitMqRelatorioConsumer consumer = new RabbitMqRelatorioConsumer(processarRelatorioUseCase);
        RuntimeException failure = new RuntimeException("Falha simulada");
        when(processarRelatorioUseCase.processar(any())).thenThrow(failure);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> consumer.processarSolicitacao(mensagem));

        assertEquals(failure, thrown);
        assertEquals(TenantContext.DEFAULT_TENANT_ID, TenantContext.getTenantId());
    }
}
