package com.decodex.br.adapters.out.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.decodex.br.adapters.out.persistence.entity.LancamentoEntity;
import com.decodex.br.adapters.out.persistence.mapper.LancamentoMapper;
import com.decodex.br.adapters.out.persistence.repository.LancamentoRepository;
import com.decodex.br.adapters.out.persistence.specification.LancamentoSpecification;
import com.decodex.br.application.dto.lancamento.LancamentoFilter;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.domain.port.out.LancamentoRepositoryPort;

@Component
public class LancamentoRepositoryAdapter implements LancamentoRepositoryPort {

    private static final Set<String> CAMPOS_ORDENAVEIS = Set.of(
            "id", "descricao", "dataVencimento", "dataPagamento", "valor", "tipo"
    );

    private final LancamentoRepository repository;
    private final LancamentoMapper mapper;

    public LancamentoRepositoryAdapter(
            LancamentoRepository repository,
            LancamentoMapper mapper) {

        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Lancamento save(Lancamento lancamento) {
        return mapper.toDomain(
            repository.save(mapper.toEntity(lancamento))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Lancamento> findById(java.util.UUID id) {
        return repository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Lancamento> findAll(LancamentoFilter filter, PageRequest request) {

        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                request.page(),
                request.size(),
                resolverSort(request)
        );

        Specification<LancamentoEntity> spec = LancamentoSpecification.fromFilter(filter);

        Page<LancamentoEntity> page = repository.findAll(spec, pageable);

        List<Lancamento> domainContent = page.getContent().stream()
                .map(mapper::toDomain)
                .toList();

        return new PageResult<>(
                domainContent,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lancamento> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(java.util.UUID id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.decodex.br.application.dto.lancamento.LancamentoHistoricoDTO> findHistorico(java.util.UUID id) {
        org.springframework.data.history.Revisions<Long, LancamentoEntity> revisions = repository.findRevisions(id);
        if (revisions.isEmpty()) {
            return List.of();
        }

        java.util.List<com.decodex.br.application.dto.lancamento.LancamentoHistoricoDTO> historico = new java.util.ArrayList<>();
        for (org.springframework.data.history.Revision<Long, LancamentoEntity> rev : revisions) {
            LancamentoEntity entity = rev.getEntity();
            Long revNumber = rev.getRequiredRevisionNumber();
            java.time.Instant instant = rev.getRequiredRevisionInstant();
            java.time.LocalDateTime dataRevisao = java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault());

            String usuario = "SISTEMA";
            Object delegate = rev.getMetadata().getDelegate();
            if (delegate instanceof com.decodex.br.config.audit.AuditRevisionEntity auditEntity && auditEntity.getUsuario() != null) {
                usuario = auditEntity.getUsuario();
            }

            String operacao = switch (rev.getMetadata().getRevisionType()) {
                case INSERT -> "CRIACAO";
                case UPDATE -> "ATUALIZACAO";
                case DELETE -> "EXCLUSAO";
                default -> "DESCONHECIDO";
            };

            historico.add(new com.decodex.br.application.dto.lancamento.LancamentoHistoricoDTO(
                    revNumber,
                    dataRevisao,
                    usuario,
                    operacao,
                    entity.getId(),
                    entity.getDescricao(),
                    entity.getValor(),
                    entity.getTipo(),
                    entity.getStatus(),
                    entity.getDataVencimento(),
                    entity.getDataPagamento(),
                    entity.getValorPago(),
                    entity.getNumeroParcela(),
                    entity.getTotalParcelas()
            ));
        }
        return historico;
    }

    private Sort resolverSort(PageRequest pageRequest) {
        if (pageRequest == null) {
            return Sort.by(Sort.Direction.ASC, "dataVencimento");
        }
        String campo = pageRequest.getSortProperty().orElse(null);
        if (campo == null || !CAMPOS_ORDENAVEIS.contains(campo)) {
            return Sort.by(Sort.Direction.ASC, "dataVencimento");
        }
        Sort.Direction direcao = pageRequest.isDesc() ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direcao, campo);
    }
}