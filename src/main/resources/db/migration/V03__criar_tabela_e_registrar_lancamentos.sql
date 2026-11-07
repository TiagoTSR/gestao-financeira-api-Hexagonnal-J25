CREATE TABLE lancamento (
    id BIGSERIAL PRIMARY KEY,
    descricao VARCHAR(80) NOT NULL,
    data_vencimento DATE NOT NULL,
    data_pagamento DATE,
    valor DECIMAL(15,2) NOT NULL,
    observacao VARCHAR(255),
    tipo VARCHAR(7) NOT NULL,
    categoria_id BIGINT NOT NULL,
    pessoa_id BIGINT NOT NULL,

    CONSTRAINT fk_lancamento_categoria
        FOREIGN KEY (categoria_id)
        REFERENCES categoria(id),

    CONSTRAINT fk_lancamento_pessoa
        FOREIGN KEY (pessoa_id)
        REFERENCES pessoa(id)
);

INSERT INTO lancamento
(descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_id, pessoa_id)
VALUES
    ('Salário mensal', '2022-06-10', null, 6500.00, 'Distribuição de lucros', 'RECEITA', 1, 1),
    ('Bahamas', '2022-02-10', '2022-02-10', 100.32, null, 'DESPESA', 2, 2),
    ('Top Club', '2022-06-10', null, 120.00, null, 'RECEITA', 3, 3),
    ('CEMIG', '2022-02-10', '2022-02-10', 110.44, 'Geração', 'RECEITA', 3, 4),
    ('DMAE', '2022-06-10', null, 200.30, null, 'DESPESA', 3, 5),
    ('Extra', '2022-03-10', '2022-03-10', 1010.32, null, 'RECEITA', 4, 6),
    ('Bahamas', '2022-06-10', null, 500.00, null, 'RECEITA', 1, 7),
    ('Top Club', '2022-03-10', '2022-03-10', 400.32, null, 'DESPESA', 4, 8),
    ('Despachante', '2022-06-10', null, 123.64, 'Multas', 'DESPESA', 3, 9),
    ('Pneus', '2022-04-10', '2022-04-10', 665.33, null, 'RECEITA', 5, 10),
    ('Café', '2022-06-10', null, 8.32, null, 'DESPESA', 1, 5),
    ('Eletrônicos', '2022-04-10', '2022-04-10', 2100.32, null, 'DESPESA', 5, 4),
    ('Instrumentos', '2022-06-10', null, 1040.32, null, 'DESPESA', 4, 3),
    ('Café', '2022-04-10', '2022-04-10', 4.32, null, 'DESPESA', 4, 2),
    ('Lanche', '2022-06-10', null, 10.20, null, 'DESPESA', 4, 1);