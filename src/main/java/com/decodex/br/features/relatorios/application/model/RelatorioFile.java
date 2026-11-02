package com.decodex.br.features.relatorios.application.model;

public record RelatorioFile(byte[] conteudo, String nomeArquivo, String contentType) {
}
