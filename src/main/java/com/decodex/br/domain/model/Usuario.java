package com.decodex.br.domain.model;

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

    private String validarUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("O nome de usuário não pode ser vazio.");
        }
        return username;
    }

    private String validarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("O e-mail não pode ser vazio.");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        return email;
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
