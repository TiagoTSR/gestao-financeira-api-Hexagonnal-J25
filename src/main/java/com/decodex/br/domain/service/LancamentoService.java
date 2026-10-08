package com.decodex.br.domain.service;

import java.util.UUID;

import com.decodex.br.application.dto.lancamento.LancamentoCreateDTO;
import com.decodex.br.application.dto.lancamento.LancamentoFilter;
import com.decodex.br.application.dto.lancamento.LancamentoUpdateDTO;
import com.decodex.br.domain.exception.RegraDeNegocioException;
import com.decodex.br.domain.exception.ResourceNotFoundException;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.domain.port.in.LancamentoInputPort;
import com.decodex.br.domain.port.out.CategoriaRepositoryPort;
import com.decodex.br.domain.port.out.LancamentoRepositoryPort;
import com.decodex.br.domain.port.out.PessoaRepositoryPort;

public class LancamentoService implements LancamentoInputPort {
	
	private final LancamentoRepositoryPort repository;
    private final CategoriaRepositoryPort categoriaRepository;
    private final PessoaRepositoryPort pessoaRepository;

    public LancamentoService(
            LancamentoRepositoryPort repository,
            CategoriaRepositoryPort categoriaRepository,
            PessoaRepositoryPort pessoaRepository) {
        this.repository = repository;
        this.categoriaRepository = categoriaRepository;
        this.pessoaRepository = pessoaRepository;
    }

    public LancamentoService(LancamentoRepositoryPort repository) {
        this(repository, null, null);
    }

    @Override
    public PageResult<Lancamento> findAll(LancamentoFilter filter, PageRequest pageRequest) {
        return repository.findAll(filter, pageRequest);
    }

    @Override
    public Lancamento findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lancamento não encontrado: " + id));
    }

    @Override
    public Lancamento create(LancamentoCreateDTO dto) {
        Categoria categoria = buscarCategoria(dto.categoriaId());
        Pessoa pessoa = buscarPessoa(dto.pessoaId());
        validarPessoaAtiva(pessoa);

        Lancamento lancamento = new Lancamento(
            dto.descricao(),
            dto.dataVencimento(),
            dto.dataPagamento(),
            dto.valor(),
            dto.observacao(),
            dto.tipo(),
            categoria,
            pessoa
        );
        return repository.save(lancamento);
    }

    @Override
    public Lancamento create(Lancamento lancamento) {
        return repository.save(lancamento);
    }

    @Override
    public Lancamento update(UUID id, LancamentoUpdateDTO dto) {
        Lancamento existing = findById(id);
        Categoria categoria = buscarCategoria(dto.categoriaId());
        Pessoa pessoa = buscarPessoa(dto.pessoaId());
        validarPessoaAtiva(pessoa);

        existing.atualizar(
            dto.descricao(),
            dto.dataVencimento(),
            dto.dataPagamento(),
            dto.valor(),
            dto.observacao(),
            dto.tipo(),
            categoria,
            pessoa
        );
        return repository.save(existing);
    }

    @Override
    public Lancamento update(UUID id, Lancamento lancamentoDetails) {
        Lancamento existing = findById(id);
        existing.atualizarCampos(lancamentoDetails);
        return repository.save(existing);
    }

    @Override
    public void delete(UUID id) {
        findById(id);
        repository.deleteById(id);
    }

    private Categoria buscarCategoria(UUID categoriaId) {
        if (categoriaRepository == null) {
            throw new IllegalStateException("CategoriaRepositoryPort não foi configurado em LancamentoService");
        }
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + categoriaId));
    }

    private Pessoa buscarPessoa(UUID pessoaId) {
        if (pessoaRepository == null) {
            throw new IllegalStateException("PessoaRepositoryPort não foi configurado em LancamentoService");
        }
        return pessoaRepository.findById(pessoaId)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada: " + pessoaId));
    }

    private void validarPessoaAtiva(Pessoa pessoa) {
        if (Boolean.FALSE.equals(pessoa.getAtivo())) {
            throw new RegraDeNegocioException("Não é possível salvar lançamento para pessoa inativa.");
        }
    }

}
