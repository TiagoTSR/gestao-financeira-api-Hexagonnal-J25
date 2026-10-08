package com.decodex.br.adapters.out.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.decodex.br.adapters.out.persistence.entity.PessoaEntity;

public interface PessoaRepository extends JpaRepository<PessoaEntity, UUID>,
JpaSpecificationExecutor<PessoaEntity> {
}