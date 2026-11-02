package com.decodex.br.testesunitarios.features.relatorios;

import com.decodex.br.adapters.out.persistence.entity.RelatorioSolicitacaoEntity;
import com.decodex.br.adapters.out.persistence.repository.RelatorioSolicitacaoRepository;
import com.decodex.br.domain.model.StatusRelatorio;
import com.decodex.br.features.relatorios.application.model.RelatorioSolicitacao;
import com.decodex.br.features.relatorios.infrastructure.persistence.RelatorioSolicitacaoPersistenceAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RelatorioSolicitacaoPersistenceAdapterTest {

    @Mock
    private RelatorioSolicitacaoRepository repository;

    @InjectMocks
    private RelatorioSolicitacaoPersistenceAdapter adapter;

    @Test
    void devePersistirSolicitacaoConvertendoParaEntidade() {
        UUID protocolo = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        byte[] conteudo = {1, 2, 3};
        LocalDateTime solicitadaEm = LocalDateTime.now();
        RelatorioSolicitacao solicitacao = new RelatorioSolicitacao(
                protocolo,
                tenantId,
                "PDF",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31),
                StatusRelatorio.CONCLUIDO,
                conteudo,
                "relatorio.pdf",
                "application/pdf",
                null,
                solicitadaEm,
                solicitadaEm.plusMinutes(1));
        when(repository.save(any(RelatorioSolicitacaoEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RelatorioSolicitacao saved = adapter.save(solicitacao);

        ArgumentCaptor<RelatorioSolicitacaoEntity> captor = ArgumentCaptor.forClass(RelatorioSolicitacaoEntity.class);
        verify(repository).save(captor.capture());
        RelatorioSolicitacaoEntity entity = captor.getValue();
        assertEquals(protocolo, entity.getId());
        assertEquals(tenantId, entity.getTenantId());
        assertEquals("PDF", entity.getTipoFormato());
        assertEquals(conteudo, entity.getConteudo());
        assertEquals("relatorio.pdf", entity.getNomeArquivo());
        assertEquals(StatusRelatorio.CONCLUIDO, saved.status());
        assertEquals(solicitadaEm.plusMinutes(1), saved.dataConclusao());
    }

    @Test
    void deveMapearSolicitacaoEncontradaParaModeloDaAplicacao() {
        UUID protocolo = UUID.randomUUID();
        RelatorioSolicitacaoEntity entity = new RelatorioSolicitacaoEntity();
        entity.setId(protocolo);
        entity.setTenantId(UUID.randomUUID());
        entity.setTipoFormato("EXCEL");
        entity.setDataInicio(LocalDate.of(2026, 3, 1));
        entity.setDataFim(LocalDate.of(2026, 3, 31));
        entity.setStatus(StatusRelatorio.PROCESSANDO);
        entity.setDataSolicitacao(LocalDateTime.now());
        when(repository.findById(protocolo)).thenReturn(Optional.of(entity));

        Optional<RelatorioSolicitacao> result = adapter.findById(protocolo);

        assertTrue(result.isPresent());
        assertEquals(protocolo, result.get().protocolo());
        assertEquals(entity.getTenantId(), result.get().tenantId());
        assertEquals("EXCEL", result.get().tipoFormato());
        assertEquals(entity.getDataInicio(), result.get().dataInicio());
        assertEquals(StatusRelatorio.PROCESSANDO, result.get().status());
    }
}
