CREATE TABLE categoria (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(50) NOT NULL
);

INSERT INTO categoria (nome) VALUES
    ('Lazer'),
    ('Alimentação'),
    ('Supermercado'),
    ('Farmácia'),
    ('Outros');