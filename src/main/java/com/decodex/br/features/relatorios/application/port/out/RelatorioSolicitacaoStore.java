package com.decodex.br.features.relatorios.application.port.out;

import java.util.Optional;
import java.util.UUID;

import com.decodex.br.features.relatorios.application.model.RelatorioSolicitacao;

public interface RelatorioSolicitacaoStore {

    RelatorioSolicitacao save(RelatorioSolicitacao solicitacao);

    Optional<RelatorioSolicitacao> findById(UUID protocolo);
}
