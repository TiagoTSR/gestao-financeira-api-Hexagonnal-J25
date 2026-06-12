package com.decodex.br.adapters.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.decodex.br.adapters.out.persistence.entity.LancamentoEntity;

public interface LancamentoRepository extends JpaRepository<LancamentoEntity, Long>,
JpaSpecificationExecutor<LancamentoEntity> {
}
