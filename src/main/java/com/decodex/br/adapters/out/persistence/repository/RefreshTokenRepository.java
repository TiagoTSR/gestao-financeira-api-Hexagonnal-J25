package com.decodex.br.adapters.out.persistence.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.decodex.br.adapters.out.persistence.entity.RefreshTokenEntity;
import com.decodex.br.adapters.out.persistence.entity.UsuarioEntity;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    void deleteByToken(String token);

    @Modifying
    @Query("DELETE FROM RefreshTokenEntity r WHERE r.usuario = :usuario")
    void deleteByUsuario(@Param("usuario") UsuarioEntity usuario);
}
