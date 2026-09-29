package com.decodex.br.adapters.out.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.decodex.br.adapters.out.persistence.entity.CategoriaEntity;
import com.decodex.br.adapters.out.persistence.mapper.CategoriaMapper;
import com.decodex.br.adapters.out.persistence.repository.CategoriaRepository;
import com.decodex.br.adapters.out.persistence.specification.CategoriaSpecification;
import com.decodex.br.application.dto.categoria.CategoriaFilter;
import com.decodex.br.domain.exception.RegraDeNegocioException;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.domain.port.out.CategoriaRepositoryPort;

@Component
public class CategoriaRepositoryAdapter implements CategoriaRepositoryPort {

    private static final Set<String> CAMPOS_ORDENAVEIS = Set.of("id", "nome");

    private final CategoriaRepository repository;
    private final CategoriaMapper mapper;

    public CategoriaRepositoryAdapter(
            CategoriaRepository repository, CategoriaMapper mapper) {

        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Categoria save(Categoria categoria) {
        try {
            return mapper.toDomain(
                    repository.saveAndFlush(
                            mapper.toEntity(categoria)
                    )
            );
        } catch (DataIntegrityViolationException ex) {
            throw new RegraDeNegocioException("Já existe uma categoria cadastrada com o nome: " + categoria.getNome(), ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Categoria> findAll(CategoriaFilter filter, PageRequest request) {

        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                request.page(),
                request.size(),
                resolverSort(request)
        );

        Specification<CategoriaEntity> spec = CategoriaSpecification.fromFilter(filter);

        Page<CategoriaEntity> page = repository.findAll(spec, pageable);

        List<Categoria> domainContent = page.getContent().stream()
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