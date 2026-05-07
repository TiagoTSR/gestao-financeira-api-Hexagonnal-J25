package com.decodex.br.adapters.out.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.decodex.br.adapters.out.persistence.mapper.PessoaMapper;
import com.decodex.br.adapters.out.persistence.repository.PessoaRepository;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.port.out.PessoaRepositoryPort;

@Component
public class PessoaRepositoryAdapter implements PessoaRepositoryPort {

    private final PessoaRepository pessoaRepository;
    private final PessoaMapper pessoaMapper;
    
    public PessoaRepositoryAdapter(
            PessoaRepository pessoaRepository,
            PessoaMapper pessoaMapper) {

        this.pessoaRepository = pessoaRepository;
        this.pessoaMapper = pessoaMapper;
    }

    @Override
    public Pessoa save(Pessoa pessoa) {
        return pessoaMapper.toDomain(
            pessoaRepository.save(pessoaMapper.toEntity(pessoa))
        );
    }

    @Override
    public Optional<Pessoa> findById(Long id) {
        return pessoaRepository.findById(id)
            .map(pessoaMapper::toDomain);
    }

    @Override
    public List<Pessoa> findAll() {
        return pessoaRepository.findAll()
            .stream()
            .map(pessoaMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        pessoaRepository.deleteById(id);
    }
}