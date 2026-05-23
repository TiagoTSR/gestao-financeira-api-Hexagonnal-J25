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
VALUES ('Salário mensal', '2022-06-10', null, 6500.00, 'Distribuição de lucros', 'RECEITA', 1, 1);

INSERT INTO lancamento
(descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_id, pessoa_id)
VALUES ('Bahamas', '2022-02-10', '2022-02-10', 100.32, null, 'DESPESA', 2, 2);

INSERT INTO lancamento
(descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_id, pessoa_id)
VALUES ('Top Club', '2022-06-10', null, 120.00, null, 'RECEITA', 3, 3);

INSERT INTO lancamento
(descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_id, pessoa_id)
VALUES ('CEMIG', '2022-02-10', '2022-02-10', 110.44, 'Geração', 'RECEITA', 3, 4);

INSERT INTO lancamento
(descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_id, pessoa_id)
VALUES ('DMAE', '2022-06-10', null, 200.30, null, 'DESPESA', 3, 5);

INSERT INTO lancamento
(descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_id, pessoa_id)
VALUES ('Extra', '2022-03-10', '2022-03-10', 1010.32, null, 'RECEITA', 4, 6);

INSERT INTO lancamento
(descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_id, pessoa_id)
VALUES ('Bahamas', '2022-06-10', null, 500.00, null, 'RECEITA', 1, 7);

INSERT INTO lancamento
(descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_id, pessoa_id)
VALUES ('Top Club', '2022-03-10', '2022-03-10', 400.32, null, 'DESPESA', 4, 8);

INSERT INTO lancamento
(descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_id, pessoa_id)
VALUES ('Despachante', '2022-06-10', null, 123.64, 'Multas', 'DESPESA', 3, 9);

INSERT INTO lancamento
(descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_id, pessoa_id)
VALUES ('Pneus', '2022-04-10', '2022-04-10', 665.33, null, 'RECEITA', 5, 10);

INSERT INTO lancamento
(descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_id, pessoa_id)
VALUES ('Café', '2022-06-10', null, 8.32, null, 'DESPESA', 1, 5);

INSERT INTO lancamento
(descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_id, pessoa_id)
VALUES ('Eletrônicos', '2022-04-10', '2022-04-10', 2100.32, null, 'DESPESA', 5, 4);

INSERT INTO lancamento
(descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_id, pessoa_id)
VALUES ('Instrumentos', '2022-06-10', null, 1040.32, null, 'DESPESA', 4, 3);

INSERT INTO lancamento
(descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_id, pessoa_id)
VALUES ('Café', '2022-04-10', '2022-04-10', 4.32, null, 'DESPESA', 4, 2);

INSERT INTO lancamento
(descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_id, pessoa_id)
VALUES ('Lanche', '2022-06-10', null, 10.20, null, 'DESPESA', 4, 1);