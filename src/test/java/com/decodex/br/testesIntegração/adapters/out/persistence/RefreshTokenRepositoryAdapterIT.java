package com.decodex.br.testesIntegração.adapters.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import com.decodex.br.adapters.out.persistence.adapter.RefreshTokenRepositoryAdapter;
import com.decodex.br.adapters.out.persistence.adapter.UsuarioRepositoryAdapter;
import com.decodex.br.adapters.out.persistence.mapper.RefreshTokenMapper;
import com.decodex.br.adapters.out.persistence.mapper.UsuarioMapper;
import com.decodex.br.adapters.out.persistence.repository.RefreshTokenRepository;
import com.decodex.br.adapters.out.persistence.repository.UsuarioRepository;
import com.decodex.br.domain.model.RefreshToken;
import com.decodex.br.domain.model.Usuario;
import com.decodex.br.testesIntegração.PostgresIntegrationBase;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
    RefreshTokenRepositoryAdapter.class,
    RefreshTokenMapper.class,
    UsuarioRepositoryAdapter.class,
    UsuarioMapper.class
})
@DisplayName("Testes de Integração - RefreshTokenRepositoryAdapter (Testcontainers)")
class RefreshTokenRepositoryAdapterIT extends PostgresIntegrationBase {

    @Autowired
    private RefreshTokenRepositoryAdapter adapter;

    @Autowired
    private UsuarioRepositoryAdapter usuarioAdapter;

    @Autowired
    private RefreshTokenRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario savedUser;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        usuarioRepository.deleteAll();

        Usuario user = new Usuario(null, "testuser", "password123", "testuser@email.com");
        savedUser = usuarioAdapter.save(user);
    }

    @Test
    @DisplayName("Deve salvar refresh token e recuperá-lo por token string")
    void deveSalvarERecuperarToken() {
        RefreshToken token = new RefreshToken(null, "uuid-random-token", savedUser, Instant.now().plusSeconds(3600));

        RefreshToken saved = adapter.save(token);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getToken()).isEqualTo("uuid-random-token");

        Optional<RefreshToken> found = adapter.findByToken("uuid-random-token");

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getUsuario().getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Deve deletar refresh token pelo token string")
    void deveDeletarPeloTokenString() {
        RefreshToken token = new RefreshToken(null, "token-to-delete", savedUser, Instant.now().plusSeconds(3600));
        adapter.save(token);

        adapter.deleteByToken("token-to-delete");

        Optional<RefreshToken> found = adapter.findByToken("token-to-delete");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Deve deletar todos os tokens de um determinado usuário")
    void deveDeletarPeloUsuario() {
        RefreshToken token1 = new RefreshToken(null, "token-1", savedUser, Instant.now().plusSeconds(3600));
        RefreshToken token2 = new RefreshToken(null, "token-2", savedUser, Instant.now().plusSeconds(3600));
        adapter.save(token1);
        adapter.save(token2);

        adapter.deleteByUsuario(savedUser);

        assertThat(adapter.findByToken("token-1")).isEmpty();
        assertThat(adapter.findByToken("token-2")).isEmpty();
    }
}
