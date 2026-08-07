package com.decodex.br.testesunitarios.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.decodex.br.domain.model.Usuario;

@DisplayName("Testes unitários para Usuario")
class UsuarioTest {

    @Test
    @DisplayName("Deve criar usuario com dados válidos")
    void deveCriarUsuarioValido() {
        Usuario usuario = new Usuario(1L, "admin", "hash123", "admin@decodex.com");

        assertThat(usuario.getId()).isEqualTo(1L);
        assertThat(usuario.getUsername()).isEqualTo("admin");
        assertThat(usuario.getPassword()).isEqualTo("hash123");
        assertThat(usuario.getEmail()).isEqualTo("admin@decodex.com");
    }

    @Test
    @DisplayName("Deve lançar exceção quando o username for inválido")
    void deveLancarExcecaoQuandoUsernameInvalido() {
        assertThatThrownBy(() -> new Usuario(1L, "  ", "hash123", "admin@decodex.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O nome de usuário não pode ser vazio.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando o email for inválido")
    void deveLancarExcecaoQuandoEmailInvalido() {
        assertThatThrownBy(() -> new Usuario(1L, "admin", "hash123", "emailSemArroba"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("E-mail inválido.");
    }

    @Test
    @DisplayName("Deve alterar email com sucesso")
    void deveAlterarEmail() {
        Usuario usuario = new Usuario(1L, "admin", "hash123", "admin@decodex.com");
        usuario.alterarEmail("novo@decodex.com");

        assertThat(usuario.getEmail()).isEqualTo("novo@decodex.com");
    }

    @Test
    @DisplayName("Deve alterar senha com sucesso")
    void deveAlterarSenha() {
        Usuario usuario = new Usuario(1L, "admin", "hash123", "admin@decodex.com");
        usuario.alterarSenha("novoHash");

        assertThat(usuario.getPassword()).isEqualTo("novoHash");
    }

    @Test
    @DisplayName("Deve comparar dois usuários baseados no ID")
    void deveCompararUsuariosEquals() {
        Usuario u1 = new Usuario(1L, "admin", "hash", "a@a.com");
        Usuario u2 = new Usuario(1L, "outro", "outroHash", "b@b.com");

        assertThat(u1).isEqualTo(u2);
        assertThat(u1.hashCode()).isEqualTo(u2.hashCode());
    }
}
