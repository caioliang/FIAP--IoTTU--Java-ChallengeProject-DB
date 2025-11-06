-- Insere o primeiro usuário como ADMIN
-- Email: admin@iottu.com.br
-- Senha: admin123
INSERT INTO TB_USUARIO (nome_usuario, email_usuario, senha_usuario, role)
SELECT 'Administrador', 'admin@iottu.com.br', '$2a$12$RJbKWxHj3.gsFZgvTT8M4OwnHFReJdf7C2sW9M1P6fO9Y486FfjWG', 'ADMIN'
WHERE NOT EXISTS (
    SELECT 1 FROM TB_USUARIO WHERE role = 'ADMIN'
);
