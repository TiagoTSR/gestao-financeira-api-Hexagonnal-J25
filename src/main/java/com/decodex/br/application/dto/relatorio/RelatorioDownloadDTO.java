package com.decodex.br.application.dto.relatorio;

public record RelatorioDownloadDTO(
        byte[] conteudo,
        String nomeArquivo,
        String contentType
) {
}
