package com.decodex.br.adapters.out.persistence.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.decodex.br.adapters.out.persistence.entity.UsuarioEntity;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByUsername(String username);
}
