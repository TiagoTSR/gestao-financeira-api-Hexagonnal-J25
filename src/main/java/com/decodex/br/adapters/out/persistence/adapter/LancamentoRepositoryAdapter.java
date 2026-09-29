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
    public Optional<Lancamento> findById(Long id) {
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
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
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