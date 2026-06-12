package com.decodex.br.adapters.out.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.decodex.br.adapters.out.persistence.entity.LancamentoEntity;
import com.decodex.br.adapters.out.persistence.mapper.LancamentoMapper;
import com.decodex.br.adapters.out.persistence.repository.LancamentoRepository;
import com.decodex.br.adapters.out.persistence.specification.LancamentoSpecification;
import com.decodex.br.domain.filter.LancamentoFilter;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.domain.port.out.LancamentoRepositoryPort;

@Component
public class LancamentoRepositoryAdapter implements LancamentoRepositoryPort {

    private final LancamentoRepository repository;
    private final LancamentoMapper mapper;
    
    public LancamentoRepositoryAdapter(
            LancamentoRepository repository,
            LancamentoMapper mapper) {

        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Lancamento save(Lancamento lancamento) {
        return mapper.toDomain(
            repository.save(mapper.toEntity(lancamento))
        );
    }

    @Override
    public Optional<Lancamento> findById(Long id) {
        return repository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public PageResult<Lancamento> findAll(LancamentoFilter filter,PageRequest request) {

    	Pageable pageable = org.springframework.data.domain.PageRequest.of(
                request.page(),
                request.size()
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
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}