package com.decodex.br.adapters.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.decodex.br.adapters.out.persistence.entity.LancamentoEntity;

public interface LancamentoRepository extends JpaRepository<LancamentoEntity, Long> {

}
