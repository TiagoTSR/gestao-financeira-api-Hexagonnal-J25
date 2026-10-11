package com.decodex.br.adapters.out.persistence.repository;

import com.decodex.br.adapters.out.persistence.entity.RelatorioSolicitacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RelatorioSolicitacaoRepository extends JpaRepository<RelatorioSolicitacaoEntity, UUID> {
}
