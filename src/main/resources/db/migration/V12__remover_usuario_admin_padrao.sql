-- Remove o usuário admin inicial gerado na migration V04 para permitir registro manual pela plataforma
DELETE FROM refresh_token WHERE usuario_id IN (SELECT id FROM usuario WHERE username = 'admin');
DELETE FROM usuario WHERE username = 'admin';
