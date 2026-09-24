package com.decodex.br.domain.model;

import static com.decodex.br.domain.validations.UsuarioValidation.validarEmail;
import static com.decodex.br.domain.validations.UsuarioValidation.validarUsername;

public class Usuario {

    private Long id;
    private String username;
    private Senha password;
    private String email;

    public Usuario(Long id, String username, String password, String email) {
        this.id = id;
        this.username = validarUsername(username);
        this.password = new Senha(password);
        this.email = validarEmail(email);
    }

    public Usuario(String username, String password, String email) {
        this(null, username, password, email);
    }

    public void alterarSenha(String novoHash) {
        this.password = new Senha(novoHash);
    }

    public void alterarEmail(String novoEmail) {
        this.email = validarEmail(novoEmail);
    }

    public Long getId() {
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
