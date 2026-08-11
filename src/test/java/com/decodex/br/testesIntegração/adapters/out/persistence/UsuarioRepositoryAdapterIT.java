package com.decodex.br.testesIntegração.adapters.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import com.decodex.br.adapters.out.persistence.adapter.UsuarioRepositoryAdapter;
import com.decodex.br.adapters.out.persistence.entity.UsuarioEntity;
import com.decodex.br.adapters.out.persistence.mapper.UsuarioMapper;
import com.decodex.br.adapters.out.persistence.repository.UsuarioRepository;
import com.decodex.br.domain.model.Usuario;
import com.decodex.br.testesIntegração.PostgresIntegrationBase;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({UsuarioRepositoryAdapter.class, UsuarioMapper.class})
@DisplayName("Testes de Integração - UsuarioRepositoryAdapter (Testcontainers)")
class UsuarioRepositoryAdapterIT extends PostgresIntegrationBase {

    @Autowired
    private UsuarioRepositoryAdapter adapter;

    @Autowired
    private UsuarioRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Deve salvar usuário no banco de dados e recuperá-lo por username")
    void deveSalvarERecuperarUsuario() {
        Usuario usuario = new Usuario(null, "johndoe", "pass123", "john@email.com");

        Usuario saved = adapter.save(usuario);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("johndoe");

        Optional<Usuario> found = adapter.findByUsername("johndoe");

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getUsername()).isEqualTo("johndoe");
        assertThat(found.get().getEmail()).isEqualTo("john@email.com");
    }

    @Test
    @DisplayName("Deve retornar vazio para username inexistente")
    void deveRetornarVazioParaUsuarioInexistente() {
        Optional<Usuario> found = adapter.findByUsername("inexistente");
        assertThat(found).isEmpty();
    }
}
