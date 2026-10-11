package com.decodex.br.domain.port.out;

import com.decodex.br.application.dto.relatorio.RelatorioSolicitacaoMsg;

public interface RelatorioProducerPort {
    void publicarSolicitacao(RelatorioSolicitacaoMsg mensagem);
}
