package com.decodex.br.adapters.out.persistence.adapter;

import java.util.Optional;
import org.springframework.stereotype.Component;
import com.decodex.br.adapters.out.persistence.mapper.UsuarioMapper;
import com.decodex.br.adapters.out.persistence.repository.UsuarioRepository;
import com.decodex.br.domain.model.Usuario;
import com.decodex.br.domain.port.out.UsuarioRepositoryPort;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioRepository repository;
    private final UsuarioMapper mapper;

    public UsuarioRepositoryAdapter(UsuarioRepository repository, UsuarioMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Usuario> findByUsername(String username) {
        return repository.findByUsername(username)
                .map(mapper::toDomain);
    }

    @Override
    public Usuario save(Usuario usuario) {
        var entity = mapper.toEntity(usuario);
        var savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }
}
