package com.decodex.br.features.relatorios.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.decodex.br.adapters.out.persistence.entity.RelatorioSolicitacaoEntity;
import com.decodex.br.adapters.out.persistence.repository.RelatorioSolicitacaoRepository;
import com.decodex.br.features.relatorios.application.model.RelatorioSolicitacao;
import com.decodex.br.features.relatorios.application.port.out.RelatorioSolicitacaoStore;

@Component
public class RelatorioSolicitacaoPersistenceAdapter implements RelatorioSolicitacaoStore {

    private final RelatorioSolicitacaoRepository repository;

    public RelatorioSolicitacaoPersistenceAdapter(RelatorioSolicitacaoRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public RelatorioSolicitacao save(RelatorioSolicitacao solicitacao) {
        return toModel(repository.save(toEntity(solicitacao)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RelatorioSolicitacao> findById(UUID protocolo) {
        return repository.findById(protocolo).map(this::toModel);
    }

    private RelatorioSolicitacaoEntity toEntity(RelatorioSolicitacao solicitacao) {
        RelatorioSolicitacaoEntity entity = new RelatorioSolicitacaoEntity();
        entity.setId(solicitacao.protocolo());
        entity.setTenantId(solicitacao.tenantId());
        entity.setTipoFormato(solicitacao.tipoFormato());
        entity.setDataInicio(solicitacao.dataInicio());
        entity.setDataFim(solicitacao.dataFim());
        entity.setStatus(solicitacao.status());
        entity.setConteudo(solicitacao.conteudo());
        entity.setNomeArquivo(solicitacao.nomeArquivo());
        entity.setContentType(solicitacao.contentType());
        entity.setMensagemErro(solicitacao.mensagemErro());
        entity.setDataSolicitacao(solicitacao.dataSolicitacao());
        entity.setDataConclusao(solicitacao.dataConclusao());
        return entity;
    }

    private RelatorioSolicitacao toModel(RelatorioSolicitacaoEntity entity) {
        return new RelatorioSolicitacao(
                entity.getId(),
                entity.getTenantId(),
                entity.getTipoFormato(),
                entity.getDataInicio(),
                entity.getDataFim(),
                entity.getStatus(),
                entity.getConteudo(),
                entity.getNomeArquivo(),
                entity.getContentType(),
                entity.getMensagemErro(),
                entity.getDataSolicitacao(),
                entity.getDataConclusao());
    }
}
