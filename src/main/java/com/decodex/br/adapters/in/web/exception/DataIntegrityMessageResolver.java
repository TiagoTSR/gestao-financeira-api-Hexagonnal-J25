package com.decodex.br.adapters.in.web.exception;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DataIntegrityMessageResolver {

    private static final Map<String, String> CONSTRAINT_MESSAGES = Map.ofEntries(
            Map.entry("uk_categoria_tenant_nome", "Já existe uma categoria com este nome."),
            Map.entry("uk_categoria_nome",        "Já existe uma categoria com este nome."),
            Map.entry("categoria_nome_key",       "Já existe uma categoria com este nome."),
            Map.entry("uk_usuario_email",         "Já existe um usuário com este e-mail."),
            Map.entry("usuario_email_key",        "Já existe um usuário com este e-mail."),
            Map.entry("uk_usuario_username",      "Já existe um usuário com este nome de usuário."),
            Map.entry("usuario_username_key",     "Já existe um usuário com este nome de usuário."),
            Map.entry("fk_lancamento_pessoa",     "Pessoa possui lançamentos vinculados e não pode ser excluída."),
            Map.entry("lancamento_pessoa_id",     "Pessoa possui lançamentos vinculados e não pode ser excluída."),
            Map.entry("fk_lancamento_categoria",  "Categoria possui lançamentos vinculados e não pode ser excluída."),
            Map.entry("lancamento_categoria_id",  "Categoria possui lançamentos vinculados e não pode ser excluída.")
    );

    public String resolve(String causeMessage) {
        if (causeMessage == null || causeMessage.isBlank()) {
            return null;
        }
        return CONSTRAINT_MESSAGES.entrySet().stream()
                .filter(e -> causeMessage.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }
}