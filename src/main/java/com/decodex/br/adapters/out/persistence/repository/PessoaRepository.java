package com.decodex.br.adapters.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.decodex.br.adapters.out.persistence.entity.PessoaEntity;

public interface PessoaRepository extends JpaRepository<PessoaEntity, Long>,
JpaSpecificationExecutor<PessoaEntity> {
}