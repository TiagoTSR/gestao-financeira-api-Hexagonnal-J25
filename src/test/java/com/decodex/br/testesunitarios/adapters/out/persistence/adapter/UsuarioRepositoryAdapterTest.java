package com.decodex.br.testesunitarios.adapters.out.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.decodex.br.adapters.out.persistence.adapter.UsuarioRepositoryAdapter;
import com.decodex.br.adapters.out.persistence.entity.UsuarioEntity;
import com.decodex.br.adapters.out.persistence.mapper.UsuarioMapper;
import com.decodex.br.adapters.out.persistence.repository.UsuarioRepository;
import com.decodex.br.domain.model.Usuario;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários - UsuarioRepositoryAdapter")
class UsuarioRepositoryAdapterTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private UsuarioMapper mapper;

    @InjectMocks
    private UsuarioRepositoryAdapter adapter;

    @Test
    @DisplayName("Deve retornar Usuario pelo nome de usuário com sucesso")
    void findByUsername_DeveRetornarUsuario() {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setUsername("admin");
        Usuario domain = new Usuario(UUID.randomUUID(), "admin", "hash", "admin@email.com");

        when(repository.findByUsername("admin")).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<Usuario> result = adapter.findByUsername("admin");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("admin");
        verify(repository).findByUsername("admin");
        verify(mapper).toDomain(entity);
    }

    @Test
    @DisplayName("Deve salvar Usuario com sucesso e retornar o domínio correspondente")
    void save_DeveSalvarUsuario() {
        UUID savedId = UUID.randomUUID();
        Usuario domainInput = new Usuario(null, "novo", "hash", "novo@email.com");
        UsuarioEntity entityInput = new UsuarioEntity();
        UsuarioEntity entitySaved = new UsuarioEntity();
        entitySaved.setId(savedId);
        Usuario domainOutput = new Usuario(savedId, "novo", "hash", "novo@email.com");

        when(mapper.toEntity(domainInput)).thenReturn(entityInput);
        when(repository.saveAndFlush(entityInput)).thenReturn(entitySaved);
        when(mapper.toDomain(entitySaved)).thenReturn(domainOutput);

        Usuario result = adapter.save(domainInput);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedId);
        assertThat(result.getUsername()).isEqualTo("novo");
        verify(mapper).toEntity(domainInput);
        verify(repository).saveAndFlush(entityInput);
        verify(mapper).toDomain(entitySaved);
    }

    @Test
    @DisplayName("Deve traduzir DataIntegrityViolationException para RegraDeNegocioException de email duplicado")
    void save_QuandoEmailDuplicado_DeveLancarRegraDeNegocioException() {
        Usuario domainInput = new Usuario(null, "usuario", "hash", "duplicado@email.com");
        UsuarioEntity entityInput = new UsuarioEntity();

        when(mapper.toEntity(domainInput)).thenReturn(entityInput);
        when(repository.saveAndFlush(entityInput))
                .thenThrow(new org.springframework.dao.DataIntegrityViolationException("uk_usuario_email"));

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> adapter.save(domainInput))
                .isInstanceOf(com.decodex.br.domain.exception.RegraDeNegocioException.class)
                .hasMessageContaining("Já existe um usuário cadastrado com o e-mail: duplicado@email.com");
    }

    @Test
    @DisplayName("Deve traduzir DataIntegrityViolationException para RegraDeNegocioException de username duplicado")
    void save_QuandoUsernameDuplicado_DeveLancarRegraDeNegocioException() {
        Usuario domainInput = new Usuario(null, "admin", "hash", "outro@email.com");
        UsuarioEntity entityInput = new UsuarioEntity();

        when(mapper.toEntity(domainInput)).thenReturn(entityInput);
        when(repository.saveAndFlush(entityInput))
                .thenThrow(new org.springframework.dao.DataIntegrityViolationException("uk_usuario_username"));

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> adapter.save(domainInput))
                .isInstanceOf(com.decodex.br.domain.exception.RegraDeNegocioException.class)
                .hasMessageContaining("Já existe um usuário cadastrado com o username: admin");
    }

    @Test
    @DisplayName("Deve retornar Usuario por username ou email")
    void findByUsernameOrEmail_DeveRetornarUsuario() {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setEmail("admin@email.com");
        Usuario domain = new Usuario(UUID.randomUUID(), "admin", "hash", "admin@email.com");

        when(repository.findByUsernameOrEmail("admin@email.com", "admin@email.com")).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<Usuario> result = adapter.findByUsernameOrEmail("admin@email.com", "admin@email.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("admin@email.com");
    }
}
