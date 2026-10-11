package com.decodex.br.testesunitarios.domain.service;

import com.decodex.br.adapters.out.persistence.entity.RelatorioSolicitacaoEntity;
import com.decodex.br.adapters.out.persistence.repository.RelatorioSolicitacaoRepository;
import com.decodex.br.application.dto.relatorio.RelatorioDownloadDTO;
import com.decodex.br.application.dto.relatorio.RelatorioProtocoloResponseDTO;
import com.decodex.br.application.dto.relatorio.RelatorioSolicitacaoMsg;
import com.decodex.br.application.dto.relatorio.RelatorioSolicitacaoRequestDTO;
import com.decodex.br.application.dto.relatorio.RelatorioStatusResponseDTO;
import com.decodex.br.config.multitenancy.TenantContext;
import com.decodex.br.domain.exception.RegraDeNegocioException;
import com.decodex.br.domain.exception.ResourceNotFoundException;
import com.decodex.br.domain.model.StatusRelatorio;
import com.decodex.br.domain.port.out.RelatorioProducerPort;
import com.decodex.br.domain.service.RelatorioAsyncService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários - RelatorioAsyncService")
class RelatorioAsyncServiceTest {

    @Mock
    private RelatorioSolicitacaoRepository relatorioRepository;

    @Mock
    private RelatorioProducerPort relatorioProducerPort;

    @InjectMocks
    private RelatorioAsyncService service;

    private final UUID tenantId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        TenantContext.setTenantId(tenantId);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin@teste.com", "senha", java.util.Collections.emptyList())
        );
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve solicitar relatório PDF com sucesso e publicar no RabbitMQ")
    void deveSolicitarRelatorioPdfComSucesso() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fim = LocalDate.of(2026, 1, 31);
        RelatorioSolicitacaoRequestDTO request = new RelatorioSolicitacaoRequestDTO("PDF", inicio, fim);

        RelatorioProtocoloResponseDTO response = service.solicitar(request);

        assertNotNull(response);
        assertNotNull(response.protocolo());
        assertEquals(StatusRelatorio.PENDENTE, response.status());

        ArgumentCaptor<RelatorioSolicitacaoEntity> entityCaptor = ArgumentCaptor.forClass(RelatorioSolicitacaoEntity.class);
        verify(relatorioRepository).save(entityCaptor.capture());
        RelatorioSolicitacaoEntity savedEntity = entityCaptor.getValue();
        assertEquals(response.protocolo(), savedEntity.getId());
        assertEquals(tenantId, savedEntity.getTenantId());
        assertEquals("PDF", savedEntity.getTipoFormato());
        assertEquals(inicio, savedEntity.getDataInicio());
        assertEquals(fim, savedEntity.getDataFim());
        assertEquals(StatusRelatorio.PENDENTE, savedEntity.getStatus());

        ArgumentCaptor<RelatorioSolicitacaoMsg> msgCaptor = ArgumentCaptor.forClass(RelatorioSolicitacaoMsg.class);
        verify(relatorioProducerPort).publicarSolicitacao(msgCaptor.capture());
        RelatorioSolicitacaoMsg publishedMsg = msgCaptor.getValue();
        assertEquals(response.protocolo(), publishedMsg.protocolo());
        assertEquals(tenantId, publishedMsg.tenantId());
        assertEquals("PDF", publishedMsg.tipoFormato());
        assertEquals("admin@teste.com", publishedMsg.usuario());
    }

    @Test
    @DisplayName("Deve normalizar formato para EXCEL quando tipo for XLSX ou EXCEL")
    void deveSolicitarRelatorioExcelComSucesso() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fim = LocalDate.of(2026, 1, 31);
        RelatorioSolicitacaoRequestDTO request = new RelatorioSolicitacaoRequestDTO("XLSX", inicio, fim);

        RelatorioProtocoloResponseDTO response = service.solicitar(request);

        assertNotNull(response);
        ArgumentCaptor<RelatorioSolicitacaoEntity> entityCaptor = ArgumentCaptor.forClass(RelatorioSolicitacaoEntity.class);
        verify(relatorioRepository).save(entityCaptor.capture());
        assertEquals("EXCEL", entityCaptor.getValue().getTipoFormato());
    }

    @Test
    @DisplayName("Deve preencher datas default quando início e fim forem nulos")
    void devePreencherDatasDefaultQuandoNulas() {
        RelatorioSolicitacaoRequestDTO request = new RelatorioSolicitacaoRequestDTO(null, null, null);

        RelatorioProtocoloResponseDTO response = service.solicitar(request);

        assertNotNull(response);
        ArgumentCaptor<RelatorioSolicitacaoEntity> entityCaptor = ArgumentCaptor.forClass(RelatorioSolicitacaoEntity.class);
        verify(relatorioRepository).save(entityCaptor.capture());
        assertNotNull(entityCaptor.getValue().getDataInicio());
        assertNotNull(entityCaptor.getValue().getDataFim());
        assertEquals("PDF", entityCaptor.getValue().getTipoFormato());
    }

    @Test
    @DisplayName("Deve definir usuário SISTEMA quando SecurityContext não estiver autenticado")
    void deveDefinirUsuarioSistemaQuandoNaoAutenticado() {
        SecurityContextHolder.clearContext();
        RelatorioSolicitacaoRequestDTO request = new RelatorioSolicitacaoRequestDTO("PDF", LocalDate.now(), LocalDate.now());

        RelatorioProtocoloResponseDTO response = service.solicitar(request);

        assertNotNull(response);
        ArgumentCaptor<RelatorioSolicitacaoMsg> msgCaptor = ArgumentCaptor.forClass(RelatorioSolicitacaoMsg.class);
        verify(relatorioProducerPort).publicarSolicitacao(msgCaptor.capture());
        assertEquals("SISTEMA", msgCaptor.getValue().usuario());
    }

    @Test
    @DisplayName("Deve lançar RegraDeNegocioException quando dataInicio for posterior a dataFim")
    void deveLancarExcecaoQuandoDataInicioPosteriorADataFim() {
        LocalDate inicio = LocalDate.of(2026, 5, 10);
        LocalDate fim = LocalDate.of(2026, 5, 1);
        RelatorioSolicitacaoRequestDTO request = new RelatorioSolicitacaoRequestDTO("PDF", inicio, fim);

        assertThrows(RegraDeNegocioException.class, () -> service.solicitar(request));
        verify(relatorioRepository, never()).save(any());
        verify(relatorioProducerPort, never()).publicarSolicitacao(any());
    }

    @Test
    @DisplayName("Deve consultar status de um protocolo existente com sucesso")
    void deveConsultarStatusComSucesso() {
        UUID protocolo = UUID.randomUUID();
        RelatorioSolicitacaoEntity entity = new RelatorioSolicitacaoEntity();
        entity.setId(protocolo);
        entity.setTipoFormato("PDF");
        entity.setStatus(StatusRelatorio.PROCESSANDO);
        entity.setDataSolicitacao(LocalDateTime.now().minusMinutes(1));

        when(relatorioRepository.findById(protocolo)).thenReturn(Optional.of(entity));

        RelatorioStatusResponseDTO statusDTO = service.consultarStatus(protocolo);

        assertNotNull(statusDTO);
        assertEquals(protocolo, statusDTO.protocolo());
        assertEquals(StatusRelatorio.PROCESSANDO, statusDTO.status());
        assertEquals("PDF", statusDTO.tipoFormato());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao consultar status de protocolo inexistente")
    void deveLancarExceptionAoConsultarStatusInexistente() {
        UUID protocolo = UUID.randomUUID();
        when(relatorioRepository.findById(protocolo)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.consultarStatus(protocolo));
    }

    @Test
    @DisplayName("Deve permitir download quando status for CONCLUIDO e com dados")
    void devePermitirDownloadQuandoStatusConcluido() {
        UUID protocolo = UUID.randomUUID();
        byte[] conteudo = new byte[]{10, 20, 30};
        RelatorioSolicitacaoEntity entity = new RelatorioSolicitacaoEntity();
        entity.setId(protocolo);
        entity.setStatus(StatusRelatorio.CONCLUIDO);
        entity.setConteudo(conteudo);
        entity.setNomeArquivo("relatorio.pdf");
        entity.setContentType("application/pdf");

        when(relatorioRepository.findById(protocolo)).thenReturn(Optional.of(entity));

        RelatorioDownloadDTO download = service.baixar(protocolo);

        assertNotNull(download);
        assertArrayEquals(conteudo, download.conteudo());
        assertEquals("relatorio.pdf", download.nomeArquivo());
        assertEquals("application/pdf", download.contentType());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar baixar relatório ainda PENDENTE ou PROCESSANDO")
    void deveLancarExcecaoAoBaixarRelatorioNaoFinalizado() {
        UUID protocolo = UUID.randomUUID();
        RelatorioSolicitacaoEntity entity = new RelatorioSolicitacaoEntity();
        entity.setId(protocolo);
        entity.setStatus(StatusRelatorio.PENDENTE);

        when(relatorioRepository.findById(protocolo)).thenReturn(Optional.of(entity));

        assertThrows(RegraDeNegocioException.class, () -> service.baixar(protocolo));
    }

    @Test
    @DisplayName("Deve lançar exceção com mensagem de erro ao tentar baixar relatório com status ERRO")
    void deveLancarExcecaoAoBaixarRelatorioComStatusErro() {
        UUID protocolo = UUID.randomUUID();
        RelatorioSolicitacaoEntity entity = new RelatorioSolicitacaoEntity();
        entity.setId(protocolo);
        entity.setStatus(StatusRelatorio.ERRO);
        entity.setMensagemErro("Falha na geração do template");

        when(relatorioRepository.findById(protocolo)).thenReturn(Optional.of(entity));

        RegraDeNegocioException ex = assertThrows(RegraDeNegocioException.class, () -> service.baixar(protocolo));
        assertTrue(ex.getMessage().contains("Falha na geração do template"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar baixar relatório sem conteúdo binário")
    void deveLancarExcecaoAoBaixarRelatorioSemConteudo() {
        UUID protocolo = UUID.randomUUID();
        RelatorioSolicitacaoEntity entity = new RelatorioSolicitacaoEntity();
        entity.setId(protocolo);
        entity.setStatus(StatusRelatorio.CONCLUIDO);
        entity.setConteudo(null);

        when(relatorioRepository.findById(protocolo)).thenReturn(Optional.of(entity));

        assertThrows(RegraDeNegocioException.class, () -> service.baixar(protocolo));
    }
}
