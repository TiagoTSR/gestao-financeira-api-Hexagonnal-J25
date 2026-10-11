package com.decodex.br.adapters.out.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.decodex.br.adapters.out.persistence.entity.LancamentoEntity;

import org.springframework.data.repository.history.RevisionRepository;

public interface LancamentoRepository extends JpaRepository<LancamentoEntity, UUID>,
        JpaSpecificationExecutor<LancamentoEntity>,
        RevisionRepository<LancamentoEntity, UUID, Long> {
}
