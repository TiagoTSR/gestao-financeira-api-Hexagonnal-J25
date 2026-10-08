-- 1. Adicionar colunas UUID temporárias com gen_random_uuid()
ALTER TABLE categoria ADD COLUMN uuid_id UUID DEFAULT gen_random_uuid() NOT NULL;
ALTER TABLE pessoa ADD COLUMN uuid_id UUID DEFAULT gen_random_uuid() NOT NULL;
ALTER TABLE usuario ADD COLUMN uuid_id UUID DEFAULT gen_random_uuid() NOT NULL;

ALTER TABLE lancamento ADD COLUMN uuid_id UUID DEFAULT gen_random_uuid() NOT NULL;
ALTER TABLE lancamento ADD COLUMN categoria_uuid UUID;
ALTER TABLE lancamento ADD COLUMN pessoa_uuid UUID;

ALTER TABLE refresh_token ADD COLUMN uuid_id UUID DEFAULT gen_random_uuid() NOT NULL;
ALTER TABLE refresh_token ADD COLUMN usuario_uuid UUID;

-- 2. Migrar os relacionamentos de chave estrangeira existentes
UPDATE lancamento l
SET categoria_uuid = c.uuid_id
FROM categoria c
WHERE l.categoria_id = c.id;

UPDATE lancamento l
SET pessoa_uuid = p.uuid_id
FROM pessoa p
WHERE l.pessoa_id = p.id;

UPDATE refresh_token rt
SET usuario_uuid = u.uuid_id
FROM usuario u
WHERE rt.usuario_id = u.id;

-- 3. Remover restrições antigas de chave estrangeira
ALTER TABLE lancamento DROP CONSTRAINT fk_lancamento_categoria;
ALTER TABLE lancamento DROP CONSTRAINT fk_lancamento_pessoa;
ALTER TABLE refresh_token DROP CONSTRAINT fk_refresh_token_usuario;

-- 4. Remover chaves primárias antigas
ALTER TABLE lancamento DROP CONSTRAINT lancamento_pkey;
ALTER TABLE categoria DROP CONSTRAINT categoria_pkey;
ALTER TABLE pessoa DROP CONSTRAINT pessoa_pkey;
ALTER TABLE refresh_token DROP CONSTRAINT refresh_token_pkey;
ALTER TABLE usuario DROP CONSTRAINT usuario_pkey;

-- 5. Remover colunas BIGINT antigas
ALTER TABLE lancamento DROP COLUMN id;
ALTER TABLE lancamento DROP COLUMN categoria_id;
ALTER TABLE lancamento DROP COLUMN pessoa_id;

ALTER TABLE categoria DROP COLUMN id;
ALTER TABLE pessoa DROP COLUMN id;

ALTER TABLE refresh_token DROP COLUMN id;
ALTER TABLE refresh_token DROP COLUMN usuario_id;

ALTER TABLE usuario DROP COLUMN id;

-- 6. Renomear as novas colunas UUID para os nomes oficiais
ALTER TABLE categoria RENAME COLUMN uuid_id TO id;
ALTER TABLE pessoa RENAME COLUMN uuid_id TO id;
ALTER TABLE usuario RENAME COLUMN uuid_id TO id;

ALTER TABLE lancamento RENAME COLUMN uuid_id TO id;
ALTER TABLE lancamento RENAME COLUMN categoria_uuid TO categoria_id;
ALTER TABLE lancamento RENAME COLUMN pessoa_uuid TO pessoa_id;

ALTER TABLE refresh_token RENAME COLUMN uuid_id TO id;
ALTER TABLE refresh_token RENAME COLUMN usuario_uuid TO usuario_id;

-- 7. Definir NOT NULL nas chaves estrangeiras
ALTER TABLE lancamento ALTER COLUMN categoria_id SET NOT NULL;
ALTER TABLE lancamento ALTER COLUMN pessoa_id SET NOT NULL;
ALTER TABLE refresh_token ALTER COLUMN usuario_id SET NOT NULL;

-- 8. Recriar as chaves primárias e estrangeiras com UUID
ALTER TABLE categoria ADD CONSTRAINT categoria_pkey PRIMARY KEY (id);
ALTER TABLE pessoa ADD CONSTRAINT pessoa_pkey PRIMARY KEY (id);
ALTER TABLE usuario ADD CONSTRAINT usuario_pkey PRIMARY KEY (id);
ALTER TABLE lancamento ADD CONSTRAINT lancamento_pkey PRIMARY KEY (id);
ALTER TABLE refresh_token ADD CONSTRAINT refresh_token_pkey PRIMARY KEY (id);

ALTER TABLE lancamento ADD CONSTRAINT fk_lancamento_categoria
    FOREIGN KEY (categoria_id) REFERENCES categoria(id);

ALTER TABLE lancamento ADD CONSTRAINT fk_lancamento_pessoa
    FOREIGN KEY (pessoa_id) REFERENCES pessoa(id);

ALTER TABLE refresh_token ADD CONSTRAINT fk_refresh_token_usuario
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE;
