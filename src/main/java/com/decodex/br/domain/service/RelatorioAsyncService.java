package com.decodex.br.domain.service;

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
import com.decodex.br.domain.port.in.RelatorioAsyncInputPort;
import com.decodex.br.domain.port.out.RelatorioProducerPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RelatorioAsyncService implements RelatorioAsyncInputPort {

    private final RelatorioSolicitacaoRepository relatorioRepository;
    private final RelatorioProducerPort relatorioProducerPort;

    public RelatorioAsyncService(RelatorioSolicitacaoRepository relatorioRepository,
                                 RelatorioProducerPort relatorioProducerPort) {
        this.relatorioRepository = relatorioRepository;
        this.relatorioProducerPort = relatorioProducerPort;
    }

    @Override
    @Transactional
    public RelatorioProtocoloResponseDTO solicitar(RelatorioSolicitacaoRequestDTO request) {
        LocalDate inicio = request.inicio() != null ? request.inicio() : LocalDate.now().withDayOfMonth(1);
        LocalDate fim = request.fim() != null ? request.fim() : LocalDate.now();

        if (inicio.isAfter(fim)) {
            throw new RegraDeNegocioException("A data de início não pode ser posterior à data de fim.");
        }

        String formato = (request.tipo() != null &&
                (request.tipo().equalsIgnoreCase("EXCEL") || request.tipo().equalsIgnoreCase("XLSX")))
                ? "EXCEL" : "PDF";

        UUID protocolo = UUID.randomUUID();
        UUID tenantId = TenantContext.getTenantId();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuario = (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName()))
                ? auth.getName() : "SISTEMA";

        RelatorioSolicitacaoEntity entity = new RelatorioSolicitacaoEntity();
        entity.setId(protocolo);
        entity.setTenantId(tenantId);
        entity.setTipoFormato(formato);
        entity.setDataInicio(inicio);
        entity.setDataFim(fim);
        entity.setStatus(StatusRelatorio.PENDENTE);
        entity.setDataSolicitacao(LocalDateTime.now());

        relatorioRepository.save(entity);

        RelatorioSolicitacaoMsg msg = new RelatorioSolicitacaoMsg(
                protocolo,
                tenantId,
                formato,
                inicio,
                fim,
                usuario
        );
        relatorioProducerPort.publicarSolicitacao(msg);

        return new RelatorioProtocoloResponseDTO(
                protocolo,
                StatusRelatorio.PENDENTE,
                "Relatório solicitado com sucesso. Acompanhe o processamento utilizando o protocolo fornecido."
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RelatorioStatusResponseDTO consultarStatus(UUID protocolo) {
        RelatorioSolicitacaoEntity entity = relatorioRepository.findById(protocolo)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação de relatório não encontrada para o protocolo: " + protocolo));

        return new RelatorioStatusResponseDTO(
                entity.getId(),
                entity.getTipoFormato(),
                entity.getStatus(),
                entity.getMensagemErro(),
                entity.getDataSolicitacao(),
                entity.getDataConclusao()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RelatorioDownloadDTO baixar(UUID protocolo) {
        RelatorioSolicitacaoEntity entity = relatorioRepository.findById(protocolo)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitação de relatório não encontrada para o protocolo: " + protocolo));

        if (entity.getStatus() != null && entity.getStatus().isEmAndamento()) {
            throw new RegraDeNegocioException("O relatório ainda está em processamento. Consulte novamente em instantes.");
        }

        if (entity.getStatus() == StatusRelatorio.ERRO) {
            throw new RegraDeNegocioException("Não foi possível gerar o relatório: " +
                    (entity.getMensagemErro() != null ? entity.getMensagemErro() : "Erro interno durante a geração"));
        }

        if (entity.getConteudo() == null || entity.getConteudo().length == 0) {
            throw new RegraDeNegocioException("Arquivo de relatório não encontrado para download.");
        }

        return new RelatorioDownloadDTO(
                entity.getConteudo(),
                entity.getNomeArquivo(),
                entity.getContentType()
        );
    }
}
