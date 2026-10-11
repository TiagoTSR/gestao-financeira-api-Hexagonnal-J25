-- 1. Criação da tabela de Tenants (Empresas / Clientes SaaS)
CREATE TABLE IF NOT EXISTS tenant (
    id UUID PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cnpj VARCHAR(18),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- 2. Inserção do Tenant Padrão inicial para compatibilidade com dados existentes
INSERT INTO tenant (id, nome, cnpj, ativo)
VALUES ('00000000-0000-0000-0000-000000000001', 'Empresa Padrão', '00.000.000/0001-00', TRUE)
ON CONFLICT (id) DO NOTHING;

-- 3. Adição da coluna tenant_id nas tabelas com valor default apontando para o tenant inicial
ALTER TABLE usuario ADD COLUMN IF NOT EXISTS tenant_id UUID DEFAULT '00000000-0000-0000-0000-000000000001' NOT NULL;
ALTER TABLE categoria ADD COLUMN IF NOT EXISTS tenant_id UUID DEFAULT '00000000-0000-0000-0000-000000000001' NOT NULL;
ALTER TABLE pessoa ADD COLUMN IF NOT EXISTS tenant_id UUID DEFAULT '00000000-0000-0000-0000-000000000001' NOT NULL;
ALTER TABLE lancamento ADD COLUMN IF NOT EXISTS tenant_id UUID DEFAULT '00000000-0000-0000-0000-000000000001' NOT NULL;
ALTER TABLE refresh_token ADD COLUMN IF NOT EXISTS tenant_id UUID DEFAULT '00000000-0000-0000-0000-000000000001' NOT NULL;

-- 4. Atualização preventiva de dados legados
UPDATE usuario SET tenant_id = '00000000-0000-0000-0000-000000000001' WHERE tenant_id IS NULL;
UPDATE categoria SET tenant_id = '00000000-0000-0000-0000-000000000001' WHERE tenant_id IS NULL;
UPDATE pessoa SET tenant_id = '00000000-0000-0000-0000-000000000001' WHERE tenant_id IS NULL;
UPDATE lancamento SET tenant_id = '00000000-0000-0000-0000-000000000001' WHERE tenant_id IS NULL;
UPDATE refresh_token SET tenant_id = '00000000-0000-0000-0000-000000000001' WHERE tenant_id IS NULL;

-- 5. Criação de restrições de Foreign Key para tenant
ALTER TABLE usuario ADD CONSTRAINT fk_usuario_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id);
ALTER TABLE categoria ADD CONSTRAINT fk_categoria_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id);
ALTER TABLE pessoa ADD CONSTRAINT fk_pessoa_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id);
ALTER TABLE lancamento ADD CONSTRAINT fk_lancamento_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id);
ALTER TABLE refresh_token ADD CONSTRAINT fk_refresh_token_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id);

-- 6. Ajuste de unicidade de categoria isolada por tenant
ALTER TABLE categoria DROP CONSTRAINT IF EXISTS uk_categoria_nome;
ALTER TABLE categoria ADD CONSTRAINT uk_categoria_tenant_nome UNIQUE (tenant_id, nome);

-- 7. Criação de índices para otimização de consultas particionadas por tenant
CREATE INDEX IF NOT EXISTS idx_usuario_tenant ON usuario(tenant_id);
CREATE INDEX IF NOT EXISTS idx_categoria_tenant ON categoria(tenant_id);
CREATE INDEX IF NOT EXISTS idx_pessoa_tenant ON pessoa(tenant_id);
CREATE INDEX IF NOT EXISTS idx_lancamento_tenant ON lancamento(tenant_id);
CREATE INDEX IF NOT EXISTS idx_refresh_token_tenant ON refresh_token(tenant_id);
