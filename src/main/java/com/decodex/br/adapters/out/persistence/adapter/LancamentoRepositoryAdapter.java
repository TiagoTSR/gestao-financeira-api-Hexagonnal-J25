package com.decodex.br.adapters.out.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.decodex.br.adapters.out.persistence.mapper.LancamentoMapper;
import com.decodex.br.adapters.out.persistence.repository.LancamentoRepository;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.port.out.LancamentoRepositoryPort;

@Component
public class LancamentoRepositoryAdapter implements LancamentoRepositoryPort {

    private final LancamentoRepository lancamentoRepository;
    private final LancamentoMapper lancamentoMapper;
    
    public LancamentoRepositoryAdapter(
            LancamentoRepository lancamentoRepository,
            LancamentoMapper lancamentoMapper) {

        this.lancamentoRepository = lancamentoRepository;
        this.lancamentoMapper = lancamentoMapper;
    }

    @Override
    public Lancamento save(Lancamento lancamento) {
        return lancamentoMapper.toDomain(
            lancamentoRepository.save(lancamentoMapper.toEntity(lancamento))
        );
    }

    @Override
    public Optional<Lancamento> findById(Long id) {
        return lancamentoRepository.findById(id)
            .map(lancamentoMapper::toDomain);
    }

    @Override
    public List<Lancamento> findAll() {
        return lancamentoRepository.findAll()
            .stream()
            .map(lancamentoMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        lancamentoRepository.deleteById(id);
    }
}