-- Tabela para rastreamento de relatorios assincronos processados via fila RabbitMQ
CREATE TABLE IF NOT EXISTS relatorio_solicitacao (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    tipo_formato VARCHAR(20) NOT NULL,
    data_inicio DATE,
    data_fim DATE,
    status VARCHAR(20) NOT NULL,
    conteudo BYTEA,
    nome_arquivo VARCHAR(150),
    content_type VARCHAR(100),
    mensagem_erro VARCHAR(500),
    data_solicitacao TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    data_conclusao TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_relatorio_solicitacao_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id)
);

CREATE INDEX IF NOT EXISTS idx_relatorio_solicitacao_tenant ON relatorio_solicitacao(tenant_id);
CREATE INDEX IF NOT EXISTS idx_relatorio_solicitacao_status ON relatorio_solicitacao(status);
