package com.decodex.br.testesunitarios.adapters.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.decodex.br.adapters.in.web.TokenResponseAssembler;
import com.decodex.br.application.dto.auth.TokenResponseDTO;
import com.decodex.br.domain.model.Usuario;
import com.decodex.br.domain.port.out.UsuarioRepositoryPort;

@DisplayName("Testes unitários para TokenResponseAssembler")
class TokenResponseAssemblerTest {

    @Test
    @DisplayName("Deve montar TokenResponseDTO com email encontrado no repositório e permissões mapeadas")
    void deveMontarTokenResponseComEmailERoles() {
        UsuarioRepositoryPort repo = mock(UsuarioRepositoryPort.class);
        Usuario usuario = new Usuario(UUID.randomUUID(), "carlos", "hash", "carlos@empresa.com");
        when(repo.findByUsernameOrEmail("carlos", "carlos")).thenReturn(Optional.of(usuario));

        TokenResponseAssembler assembler = new TokenResponseAssembler(repo);

        UserDetails userDetails = new User("carlos", "pass", List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_USER")
        ));

        TokenResponseDTO response = assembler.montar(userDetails, "jwt-access", "jwt-refresh");

        assertThat(response.token()).isEqualTo("jwt-access");
        assertThat(response.accessToken()).isEqualTo("jwt-access");
        assertThat(response.refreshToken()).isEqualTo("jwt-refresh");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(900L);
        assertThat(response.nome()).isEqualTo("carlos");
        assertThat(response.email()).isEqualTo("carlos@empresa.com");
        assertThat(response.permissoes()).containsExactly("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    @DisplayName("Deve usar username como email quando repositório não encontrar o usuário")
    void deveUsarUsernameComoEmailQuandoNaoEncontrado() {
        UsuarioRepositoryPort repo = mock(UsuarioRepositoryPort.class);
        when(repo.findByUsernameOrEmail("maria", "maria")).thenReturn(Optional.empty());

        TokenResponseAssembler assembler = new TokenResponseAssembler(repo);
        UserDetails userDetails = new User("maria", "pass", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        TokenResponseDTO response = assembler.montar(userDetails, "access", "refresh");

        assertThat(response.email()).isEqualTo("maria");
    }

    @Test
    @DisplayName("Deve aplicar permissão padrão ROLE_USER quando authorities estiver vazio e repositório nulo")
    void deveAplicarRoleUserQuandoAuthoritiesVazio() {
        TokenResponseAssembler assembler = new TokenResponseAssembler(null);
        UserDetails userDetails = new User("jose", "pass", List.of());

        TokenResponseDTO response = assembler.montar(userDetails, "access", "refresh");

        assertThat(response.nome()).isEqualTo("jose");
        assertThat(response.email()).isEqualTo("jose");
        assertThat(response.permissoes()).containsExactly("ROLE_USER");
    }
}
