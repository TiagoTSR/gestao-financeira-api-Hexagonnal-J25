package com.decodex.br.adapters.in.messaging;

import com.decodex.br.adapters.out.persistence.entity.RelatorioSolicitacaoEntity;
import com.decodex.br.adapters.out.persistence.repository.RelatorioSolicitacaoRepository;
import com.decodex.br.application.dto.relatorio.RelatorioSolicitacaoMsg;
import com.decodex.br.config.multitenancy.TenantContext;
import com.decodex.br.config.rabbitmq.RabbitMqConfig;
import com.decodex.br.domain.model.StatusRelatorio;
import com.decodex.br.domain.port.in.GerarRelatorioEstatisticaInputPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class RabbitMqRelatorioConsumer {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqRelatorioConsumer.class);
    private static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final RelatorioSolicitacaoRepository relatorioRepository;
    private final GerarRelatorioEstatisticaInputPort gerarRelatorioInputPort;

    public RabbitMqRelatorioConsumer(RelatorioSolicitacaoRepository relatorioRepository,
                                     GerarRelatorioEstatisticaInputPort gerarRelatorioInputPort) {
        this.relatorioRepository = relatorioRepository;
        this.gerarRelatorioInputPort = gerarRelatorioInputPort;
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE_RELATORIOS)
    public void processarSolicitacao(RelatorioSolicitacaoMsg msg) {
        log.info("Iniciando processamento assíncrono do relatório. Protocolo: {}, Formato: {}, Tenant: {}",
                msg.protocolo(), msg.tipoFormato(), msg.tenantId());

        TenantContext.setTenantId(msg.tenantId());
        try {
            Optional<RelatorioSolicitacaoEntity> optEntity = relatorioRepository.findById(msg.protocolo());
            if (optEntity.isEmpty()) {
                log.warn("Solicitação não encontrada no banco para protocolo: {}", msg.protocolo());
                return;
            }

            RelatorioSolicitacaoEntity entity = optEntity.get();
            entity.setStatus(StatusRelatorio.PROCESSANDO);
            relatorioRepository.save(entity);

            byte[] bytes;
            String filename;
            String contentType;

            if ("EXCEL".equalsIgnoreCase(msg.tipoFormato())) {
                bytes = gerarRelatorioInputPort.executarPorPessoaExcel(msg.dataInicio(), msg.dataFim());
                filename = String.format("relatorio-lancamentos_%s_%s.xlsx", msg.dataInicio(), msg.dataFim());
                contentType = EXCEL_CONTENT_TYPE;
            } else {
                bytes = gerarRelatorioInputPort.executarPorPessoa(msg.dataInicio(), msg.dataFim());
                filename = String.format("relatorio-lancamentos_%s_%s.pdf", msg.dataInicio(), msg.dataFim());
                contentType = MediaType.APPLICATION_PDF_VALUE;
            }

            entity.setConteudo(bytes);
            entity.setNomeArquivo(filename);
            entity.setContentType(contentType);
            entity.setStatus(StatusRelatorio.CONCLUIDO);
            entity.setDataConclusao(LocalDateTime.now());
            entity.setMensagemErro(null);
            relatorioRepository.save(entity);

            log.info("Relatório gerado com sucesso para protocolo: {}. Tamanho: {} bytes", msg.protocolo(), bytes.length);
        } catch (Exception ex) {
            log.error("Erro durante o processamento do relatório {}: {}", msg.protocolo(), ex.getMessage(), ex);
            try {
                relatorioRepository.findById(msg.protocolo()).ifPresent(e -> {
                    e.setStatus(StatusRelatorio.ERRO);
                    e.setMensagemErro(ex.getMessage() != null ? ex.getMessage() : "Erro inesperado na geração");
                    e.setDataConclusao(LocalDateTime.now());
                    relatorioRepository.save(e);
                });
            } catch (Exception dbEx) {
                log.error("Falha ao registrar status de ERRO para protocolo {}: {}", msg.protocolo(), dbEx.getMessage());
            }
            throw ex;
        } finally {
            TenantContext.clear();
        }
    }
}
