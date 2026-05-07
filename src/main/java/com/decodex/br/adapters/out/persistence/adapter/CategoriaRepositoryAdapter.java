package com.decodex.br.adapters.out.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.decodex.br.adapters.out.persistence.mapper.CategoriaMapper;
import com.decodex.br.adapters.out.persistence.repository.CategoriaRepository;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.port.out.CategoriaRepositoryPort;

@Component
public class CategoriaRepositoryAdapter implements CategoriaRepositoryPort {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaRepositoryAdapter(
            CategoriaRepository categoriaRepository,
            CategoriaMapper categoriaMapper) {

        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    @Override
    public Categoria save(Categoria categoria) {
        return categoriaMapper.toDomain(
                categoriaRepository.save(
                        categoriaMapper.toEntity(categoria)
                )
        );
    }

    @Override
    public Optional<Categoria> findById(Long id) {
        return categoriaRepository.findById(id)
                .map(categoriaMapper::toDomain);
    }

    @Override
    public List<Categoria> findAll() {
        return categoriaRepository.findAll()
                .stream()
                .map(categoriaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        categoriaRepository.deleteById(id);
    }
}