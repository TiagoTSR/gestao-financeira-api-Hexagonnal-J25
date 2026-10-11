package com.decodex.br.domain.model;

import java.util.UUID;

import com.decodex.br.domain.annotation.Default;
import static com.decodex.br.domain.validations.UsuarioValidation.validarEmail;
import static com.decodex.br.domain.validations.UsuarioValidation.validarUsername;

public class Usuario {

    private UUID id;
    private String username;
    private Senha password;
    private String email;
    private UUID tenantId;

    public Usuario(UUID id, String username, String password, String email, UUID tenantId) {
        this.id = id;
        this.username = validarUsername(username);
        this.password = new Senha(password);
        this.email = validarEmail(email);
        this.tenantId = tenantId != null ? tenantId : UUID.fromString("00000000-0000-0000-0000-000000000001");
    }

    @Default
    public Usuario(UUID id, String username, String password, String email) {
        this(id, username, password, email, UUID.fromString("00000000-0000-0000-0000-000000000001"));
    }

    public Usuario(String username, String password, String email) {
        this(null, username, password, email, UUID.fromString("00000000-0000-0000-0000-000000000001"));
    }

    public void alterarSenha(String novoHash) {
        this.password = new Senha(novoHash);
    }

    public void alterarEmail(String novoEmail) {
        this.email = validarEmail(novoEmail);
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password.getHash();
    }

    public String getEmail() {
        return email;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return id != null && id.equals(usuario.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
