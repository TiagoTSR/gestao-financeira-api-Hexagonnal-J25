package com.decodex.br.features.lancamentos.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.decodex.br.domain.exception.RegraDeNegocioException;
import com.decodex.br.domain.exception.ResourceNotFoundException;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.domain.port.out.CategoriaRepositoryPort;
import com.decodex.br.domain.port.out.PessoaRepositoryPort;
import com.decodex.br.features.lancamentos.application.model.LancamentoBaixaCommand;
import com.decodex.br.features.lancamentos.application.model.LancamentoCreateCommand;
import com.decodex.br.features.lancamentos.application.model.LancamentoFilterQuery;
import com.decodex.br.features.lancamentos.application.model.LancamentoHistoryItem;
import com.decodex.br.features.lancamentos.application.model.LancamentoUpdateCommand;
import com.decodex.br.features.lancamentos.application.port.out.LancamentoStore;
import com.decodex.br.features.lancamentos.application.port.in.LancamentoUseCase;

public class LancamentoApplicationService implements LancamentoUseCase {
	
	private final LancamentoStore repository;
    private final CategoriaRepositoryPort categoriaRepository;
    private final PessoaRepositoryPort pessoaRepository;

    public LancamentoApplicationService(
            LancamentoStore repository,
            CategoriaRepositoryPort categoriaRepository,
            PessoaRepositoryPort pessoaRepository) {
        this.repository = repository;
        this.categoriaRepository = categoriaRepository;
        this.pessoaRepository = pessoaRepository;
    }

    @Override
    public PageResult<Lancamento> findAll(LancamentoFilterQuery filter, PageRequest pageRequest) {
        return repository.findAll(filter, pageRequest);
    }

    @Override
    public Lancamento findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lancamento não encontrado: " + id));
    }

    @Override
    public Lancamento create(LancamentoCreateCommand dto) {
        Categoria categoria = buscarCategoria(dto.categoriaId());
        Pessoa pessoa = buscarPessoa(dto.pessoaId());
        validarPessoaAtiva(pessoa);

        List<Lancamento> lancamentos = ParcelamentoFactory.gerarLancamentos(dto, categoria, pessoa);

        Lancamento primeiroLancamento = null;
        for (Lancamento lancamento : lancamentos) {
            Lancamento salvo = repository.save(lancamento);
            if (primeiroLancamento == null) {
                primeiroLancamento = salvo;
            }
        }
        return primeiroLancamento;
    }

    @Override
    public Lancamento create(Lancamento lancamento) {
        return repository.save(lancamento);
    }

    @Override
    public Lancamento update(UUID id, LancamentoUpdateCommand dto) {
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

    @Override
    public Lancamento quitar(UUID id, LancamentoBaixaCommand baixaDTO) {
        Lancamento existing = findById(id);
        LocalDate dataPag = (baixaDTO != null && baixaDTO.dataPagamento() != null)
                ? baixaDTO.dataPagamento()
                : LocalDate.now();
        BigDecimal valorPag = (baixaDTO != null && baixaDTO.valorPago() != null)
                ? baixaDTO.valorPago()
                : existing.getValor();

        existing.quitar(dataPag, valorPag);
        return repository.save(existing);
    }

    @Override
    public Lancamento cancelar(UUID id) {
        Lancamento existing = findById(id);
        existing.cancelar();
        return repository.save(existing);
    }

    @Override
    public Lancamento reabrir(UUID id) {
        Lancamento existing = findById(id);
        existing.reabrir();
        return repository.save(existing);
    }

    @Override
    public List<LancamentoHistoryItem> findHistorico(UUID id) {
        findById(id);
        return repository.findHistory(id);
    }

    private Categoria buscarCategoria(UUID categoriaId) {
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + categoriaId));
    }

    private Pessoa buscarPessoa(UUID pessoaId) {
        return pessoaRepository.findById(pessoaId)
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada: " + pessoaId));
    }

    private void validarPessoaAtiva(Pessoa pessoa) {
        if (pessoa != null && !pessoa.isAtivo()) {
            throw new RegraDeNegocioException("Não é possível salvar lançamento para pessoa inativa.");
        }
    }
}
