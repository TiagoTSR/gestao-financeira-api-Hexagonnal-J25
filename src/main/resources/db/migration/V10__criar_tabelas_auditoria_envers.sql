-- 1. Tabela de informacoes de revisao do Hibernate Envers
CREATE TABLE IF NOT EXISTS revinfo (
    rev BIGSERIAL PRIMARY KEY,
    revtstmp BIGINT NOT NULL,
    usuario VARCHAR(100),
    tenant_id UUID
);

-- 2. Tabela de auditoria para historico de lancamentos (lancamento_aud)
CREATE TABLE IF NOT EXISTS lancamento_aud (
    id UUID NOT NULL,
    rev BIGINT NOT NULL,
    revtype SMALLINT NOT NULL,
    tenant_id UUID,
    descricao VARCHAR(80),
    data_vencimento DATE,
    data_pagamento DATE,
    valor NUMERIC(15,2),
    observacao VARCHAR(255),
    tipo VARCHAR(20),
    status VARCHAR(20),
    valor_pago NUMERIC(15,2),
    numero_parcela INTEGER,
    total_parcelas INTEGER,
    categoria_id UUID,
    pessoa_id UUID,
    PRIMARY KEY (id, rev),
    CONSTRAINT fk_lancamento_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

-- 3. Indices para otimizacao de busca no historico de auditoria
CREATE INDEX IF NOT EXISTS idx_lancamento_aud_id ON lancamento_aud(id);
CREATE INDEX IF NOT EXISTS idx_lancamento_aud_rev ON lancamento_aud(rev);
