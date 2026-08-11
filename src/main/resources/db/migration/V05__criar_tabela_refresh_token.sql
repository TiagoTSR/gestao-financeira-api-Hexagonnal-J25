CREATE TABLE refresh_token (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) UNIQUE NOT NULL,
    usuario_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_refresh_token_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);
