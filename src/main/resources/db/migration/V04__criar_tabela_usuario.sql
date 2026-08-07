CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);
-- Insere usuário admin com senha 123456 criptografada com BCrypt
INSERT INTO usuario (username, password, email)
VALUES (
        'admin',
        '$2a$12$Dm14kLcgEsv3QLgBnrNMj.ZDPGvK.TJROmIaMK0nzyp5FD4E79jZO',
        'admin@decodex.com'
    );