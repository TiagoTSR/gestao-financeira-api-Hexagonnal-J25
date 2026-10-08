package com.decodex.br.adapters.out.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.decodex.br.adapters.out.persistence.entity.CategoriaEntity;

public interface CategoriaRepository extends JpaRepository<CategoriaEntity, UUID>,
JpaSpecificationExecutor<CategoriaEntity> {
}
