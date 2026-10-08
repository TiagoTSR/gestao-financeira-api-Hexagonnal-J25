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

import com.decodex.br.adapters.out.persistence.entity.PessoaEntity;
import com.decodex.br.adapters.out.persistence.mapper.PessoaMapper;
import com.decodex.br.adapters.out.persistence.repository.PessoaRepository;
import com.decodex.br.adapters.out.persistence.specification.PessoaSpecification;
import com.decodex.br.application.dto.pessoa.PessoaFilter;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.domain.port.out.PessoaRepositoryPort;

@Component
public class PessoaRepositoryAdapter implements PessoaRepositoryPort {

    private static final Set<String> CAMPOS_ORDENAVEIS = Set.of("id", "nome", "ativo");

    private final PessoaRepository repository;
    private final PessoaMapper mapper;

    public PessoaRepositoryAdapter(
            PessoaRepository repository,
            PessoaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Pessoa save(Pessoa pessoa) {
        return mapper.toDomain(
            repository.save(mapper.toEntity(pessoa))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pessoa> findById(java.util.UUID id) {
        return repository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Pessoa> findAll(PessoaFilter filter, PageRequest request) {

        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                request.page(),
                request.size(),
                resolverSort(request)
        );

        Specification<PessoaEntity> spec = PessoaSpecification.fromFilter(filter);

        Page<PessoaEntity> page = repository.findAll(spec, pageable);

        List<Pessoa> domainContent = page.getContent().stream()
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
    public void deleteById(java.util.UUID id) {
        repository.deleteById(id);
    }

    private Sort resolverSort(PageRequest pageRequest) {
        if (pageRequest == null) {
            return Sort.by(Sort.Direction.ASC, "nome");
        }
        String campo = pageRequest.getSortProperty().orElse(null);
        if (campo == null || !CAMPOS_ORDENAVEIS.contains(campo)) {
            return Sort.by(Sort.Direction.ASC, "nome");
        }
        Sort.Direction direcao = pageRequest.isDesc() ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direcao, campo);
    }
}