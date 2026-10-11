package com.decodex.br.testesunitarios.adapters.in.messaging;

import com.decodex.br.adapters.in.messaging.RabbitMqRelatorioConsumer;
import com.decodex.br.adapters.out.persistence.entity.RelatorioSolicitacaoEntity;
import com.decodex.br.adapters.out.persistence.repository.RelatorioSolicitacaoRepository;
import com.decodex.br.application.dto.relatorio.RelatorioSolicitacaoMsg;
import com.decodex.br.config.multitenancy.TenantContext;
import com.decodex.br.domain.model.StatusRelatorio;
import com.decodex.br.domain.port.in.GerarRelatorioEstatisticaInputPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - RabbitMqRelatorioConsumer")
class RabbitMqRelatorioConsumerTest {

    @Mock
    private RelatorioSolicitacaoRepository relatorioRepository;

    @Mock
    private GerarRelatorioEstatisticaInputPort gerarRelatorioInputPort;

    @InjectMocks
    private RabbitMqRelatorioConsumer consumer;

    @Test
    @DisplayName("Deve processar mensagem e gerar relatório PDF com sucesso")
    void deveProcessarMensagemPdfComSucesso() {
        UUID protocolo = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fim = LocalDate.of(2026, 1, 31);
        byte[] pdfBytes = "%PDF-relatorio".getBytes();

        RelatorioSolicitacaoMsg msg = new RelatorioSolicitacaoMsg(
                protocolo, tenantId, "PDF", inicio, fim, "usuario"
        );

        RelatorioSolicitacaoEntity entity = new RelatorioSolicitacaoEntity();
        entity.setId(protocolo);
        entity.setTenantId(tenantId);
        entity.setStatus(StatusRelatorio.PENDENTE);

        when(relatorioRepository.findById(protocolo)).thenReturn(Optional.of(entity));
        when(gerarRelatorioInputPort.executarPorPessoa(inicio, fim)).thenReturn(pdfBytes);

        consumer.processarSolicitacao(msg);

        // Verifica que o status final foi CONCLUIDO e salvou o conteúdo
        ArgumentCaptor<RelatorioSolicitacaoEntity> captor = ArgumentCaptor.forClass(RelatorioSolicitacaoEntity.class);
        verify(relatorioRepository, atLeastOnce()).save(captor.capture());

        RelatorioSolicitacaoEntity finalEntity = captor.getValue();
        assertEquals(StatusRelatorio.CONCLUIDO, finalEntity.getStatus());
        assertArrayEquals(pdfBytes, finalEntity.getConteudo());
        assertEquals(MediaType.APPLICATION_PDF_VALUE, finalEntity.getContentType());
        assertTrue(finalEntity.getNomeArquivo().endsWith(".pdf"));
        assertNotNull(finalEntity.getDataConclusao());

        // Garante que o TenantContext foi limpo ao final
        assertEquals(TenantContext.DEFAULT_TENANT_ID, TenantContext.getTenantId());
    }

    @Test
    @DisplayName("Deve processar mensagem e gerar relatório Excel com sucesso")
    void deveProcessarMensagemExcelComSucesso() {
        UUID protocolo = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fim = LocalDate.of(2026, 1, 31);
        byte[] xlsxBytes = new byte[]{1, 2, 3};

        RelatorioSolicitacaoMsg msg = new RelatorioSolicitacaoMsg(
                protocolo, tenantId, "EXCEL", inicio, fim, "usuario"
        );

        RelatorioSolicitacaoEntity entity = new RelatorioSolicitacaoEntity();
        entity.setId(protocolo);
        entity.setTenantId(tenantId);
        entity.setStatus(StatusRelatorio.PENDENTE);

        when(relatorioRepository.findById(protocolo)).thenReturn(Optional.of(entity));
        when(gerarRelatorioInputPort.executarPorPessoaExcel(inicio, fim)).thenReturn(xlsxBytes);

        consumer.processarSolicitacao(msg);

        ArgumentCaptor<RelatorioSolicitacaoEntity> captor = ArgumentCaptor.forClass(RelatorioSolicitacaoEntity.class);
        verify(relatorioRepository, atLeastOnce()).save(captor.capture());

        RelatorioSolicitacaoEntity finalEntity = captor.getValue();
        assertEquals(StatusRelatorio.CONCLUIDO, finalEntity.getStatus());
        assertArrayEquals(xlsxBytes, finalEntity.getConteudo());
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", finalEntity.getContentType());
        assertTrue(finalEntity.getNomeArquivo().endsWith(".xlsx"));
    }

    @Test
    @DisplayName("Deve ignorar e retornar silenciosamente se entidade não for encontrada")
    void deveIgnorarQuandoEntidadeNaoEncontrada() {
        UUID protocolo = UUID.randomUUID();
        RelatorioSolicitacaoMsg msg = new RelatorioSolicitacaoMsg(
                protocolo, UUID.randomUUID(), "PDF", LocalDate.now(), LocalDate.now(), "user"
        );

        when(relatorioRepository.findById(protocolo)).thenReturn(Optional.empty());

        consumer.processarSolicitacao(msg);

        verify(gerarRelatorioInputPort, never()).executarPorPessoa(any(), any());
        verify(gerarRelatorioInputPort, never()).executarPorPessoaExcel(any(), any());
    }

    @Test
    @DisplayName("Deve atualizar status para ERRO e relançar exceção para acionar retentativas e DLQ")
    void deveAtualizarParaErroERelancarQuandoOcorrerFalha() {
        UUID protocolo = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        LocalDate inicio = LocalDate.now();
        LocalDate fim = LocalDate.now();

        RelatorioSolicitacaoMsg msg = new RelatorioSolicitacaoMsg(
                protocolo, tenantId, "PDF", inicio, fim, "user"
        );

        RelatorioSolicitacaoEntity entity = new RelatorioSolicitacaoEntity();
        entity.setId(protocolo);
        entity.setStatus(StatusRelatorio.PENDENTE);

        when(relatorioRepository.findById(protocolo)).thenReturn(Optional.of(entity));
        when(gerarRelatorioInputPort.executarPorPessoa(inicio, fim)).thenThrow(new RuntimeException("Simulated generation error"));

        assertThrows(RuntimeException.class, () -> consumer.processarSolicitacao(msg));

        ArgumentCaptor<RelatorioSolicitacaoEntity> captor = ArgumentCaptor.forClass(RelatorioSolicitacaoEntity.class);
        verify(relatorioRepository, atLeast(2)).save(captor.capture());

        RelatorioSolicitacaoEntity erroEntity = captor.getValue();
        assertEquals(StatusRelatorio.ERRO, erroEntity.getStatus());
        assertTrue(erroEntity.getMensagemErro().contains("Simulated generation error"));
    }
}
