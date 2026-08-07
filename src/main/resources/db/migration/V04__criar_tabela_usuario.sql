CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);

-- Insere usuário admin com senha 123456 criptografada com BCrypt
INSERT INTO usuario (username, password, email)
VALUES ('admin', '$2a$10$8.2qPhGWyGAf.2oMzHN4y.G6tUq76wW1S70H.Kx1S6g1gG9XgGzGa', 'admin@decodex.com');
