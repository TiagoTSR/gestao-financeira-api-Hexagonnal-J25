package com.decodex.br.testesunitarios.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.decodex.br.application.dto.lancamento.LancamentoFilter;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.model.TipoLancamento;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;
import com.decodex.br.application.dto.lancamento.LancamentoCreateDTO;
import com.decodex.br.application.dto.lancamento.LancamentoUpdateDTO;
import com.decodex.br.domain.exception.RegraDeNegocioException;
import com.decodex.br.domain.port.out.CategoriaRepositoryPort;
import com.decodex.br.domain.port.out.LancamentoRepositoryPort;
import com.decodex.br.domain.port.out.PessoaRepositoryPort;
import com.decodex.br.domain.service.LancamentoService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários para LancamentoService")
class LancamentoServiceTest {

    @Mock
    private LancamentoRepositoryPort repository;

    @Mock
    private CategoriaRepositoryPort categoriaRepository;

    @Mock
    private PessoaRepositoryPort pessoaRepository;

    @InjectMocks
    private LancamentoService service;

    private Categoria categoria;
    private Pessoa pessoa;
    private Lancamento lancamento;

    @BeforeEach
    void setUp() {
        categoria = new Categoria(1L, "Lazer");
        Endereco endereco = new Endereco("Rua A", "10", null, "Centro", "00000-000", "São Paulo", "SP");
        pessoa = new Pessoa(2L, "Ana", endereco, true);
        lancamento = new Lancamento(10L, "Cinema", LocalDate.of(2025, 6, 10), null,
                new BigDecimal("50.00"), null, TipoLancamento.DESPESA, categoria, pessoa);
    }

    @Test
    @DisplayName("Deve listar lançamentos paginados")
    void findAll_ShouldReturnPageResult() {
        LancamentoFilter filter = new LancamentoFilter();
        PageRequest pageRequest = new PageRequest(0, 10);
        PageResult<Lancamento> pageResult = new PageResult<>(
            List.of(lancamento), 0, 10, 1L, 1
        );
        when(repository.findAll(any(LancamentoFilter.class), eq(pageRequest))).thenReturn(pageResult);

        PageResult<Lancamento> result = service.findAll(filter, pageRequest);

        assertThat(result.content()).hasSize(1).contains(lancamento);
        assertThat(result.page()).isZero();
        assertThat(result.totalElements()).isEqualTo(1L);
        verify(repository, times(1)).findAll(any(LancamentoFilter.class), eq(pageRequest));
    }

    @Test
    @DisplayName("Deve buscar lançamento por ID com sucesso")
    void findById_WhenExists_ShouldReturnLancamento() {
        when(repository.findById(10L)).thenReturn(Optional.of(lancamento));

        Lancamento found = service.findById(10L);

        assertThat(found).isEqualTo(lancamento);
        verify(repository, times(1)).findById(10L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar lançamento por ID inexistente")
    void findById_WhenNotExists_ShouldThrowException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Lancamento não encontrado: 99");
        verify(repository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve criar novo lançamento")
    void create_ShouldSaveAndReturn() {
        when(repository.save(any(Lancamento.class))).thenReturn(lancamento);

        Lancamento created = service.create(lancamento);

        assertThat(created).isEqualTo(lancamento);
        verify(repository, times(1)).save(lancamento);
    }

    @Test
    @DisplayName("Deve atualizar lançamento existente")
    void update_ShouldUpdateFieldsAndSave() {
        Lancamento existing = new Lancamento(
            10L, "Cinema", LocalDate.of(2025, 6, 10), null,
            new BigDecimal("50.00"), null, TipoLancamento.DESPESA, categoria, pessoa
        );
        Lancamento updatedDetails = new Lancamento(
            null, "Cinema IMAX", LocalDate.of(2025, 6, 15),
            LocalDate.of(2025, 6, 14), new BigDecimal("75.00"), "Ingresso VIP",
            TipoLancamento.DESPESA, categoria, pessoa
        );
        Lancamento expectedUpdated = new Lancamento(
            10L, "Cinema IMAX", LocalDate.of(2025, 6, 15),
            LocalDate.of(2025, 6, 14), new BigDecimal("75.00"), "Ingresso VIP",
            TipoLancamento.DESPESA, categoria, pessoa
        );

        when(repository.findById(10L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Lancamento.class))).thenReturn(expectedUpdated);

        Lancamento result = service.update(10L, updatedDetails);

        assertThat(result.getDescricao()).isEqualTo("Cinema IMAX");
        assertThat(result.getDataVencimento()).isEqualTo(LocalDate.of(2025, 6, 15));
        assertThat(result.getValor()).isEqualTo(new BigDecimal("75.00"));
        assertThat(result.getObservacao()).isEqualTo("Ingresso VIP");

        verify(repository).findById(10L);
        verify(repository).save(argThat(l -> 
            l.getId().equals(10L) &&
            l.getDescricao().equals("Cinema IMAX") &&
            l.getValor().equals(new BigDecimal("75.00"))
        ));
    }

    @Test
    @DisplayName("Deve deletar lançamento existente")
    void delete_ShouldDeleteById() {
        when(repository.findById(10L)).thenReturn(Optional.of(lancamento));
        doNothing().when(repository).deleteById(10L);

        service.delete(10L);

        verify(repository, times(1)).findById(10L);
        verify(repository, times(1)).deleteById(10L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar lançamento inexistente")
    void delete_WhenNotExists_ShouldThrowException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Lancamento não encontrado: 99");

        verify(repository, times(1)).findById(99L);
        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Deve criar lançamento a partir de DTO buscando categoria e pessoa")
    void create_WithDTO_ShouldFindDependenciesAndSave() {
        LancamentoCreateDTO dto = new LancamentoCreateDTO(
            "Salário", LocalDate.of(2025, 6, 10), null, new BigDecimal("5000.00"),
            null, TipoLancamento.RECEITA, 1L, 2L
        );
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(pessoaRepository.findById(2L)).thenReturn(Optional.of(pessoa));
        when(repository.save(any(Lancamento.class))).thenReturn(lancamento);

        Lancamento result = service.create(dto);

        assertThat(result).isNotNull();
        verify(categoriaRepository).findById(1L);
        verify(pessoaRepository).findById(2L);
        verify(repository).save(any(Lancamento.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar com DTO quando categoria não for encontrada")
    void create_WithDTO_WhenCategoriaNotFound_ShouldThrowException() {
        LancamentoCreateDTO dto = new LancamentoCreateDTO(
            "Salário", LocalDate.of(2025, 6, 10), null, new BigDecimal("5000.00"),
            null, TipoLancamento.RECEITA, 99L, 2L
        );
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Categoria não encontrada: 99");
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar com DTO quando pessoa não for encontrada")
    void create_WithDTO_WhenPessoaNotFound_ShouldThrowException() {
        LancamentoCreateDTO dto = new LancamentoCreateDTO(
            "Salário", LocalDate.of(2025, 6, 10), null, new BigDecimal("5000.00"),
            null, TipoLancamento.RECEITA, 1L, 99L
        );
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(pessoaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pessoa não encontrada: 99");
    }

    @Test
    @DisplayName("Deve lançar RegraDeNegocioException ao criar com DTO para pessoa inativa")
    void create_WithDTO_WhenPessoaInactive_ShouldThrowRegraDeNegocioException() {
        Pessoa inativa = new Pessoa(2L, "Inativa", pessoa.getEndereco(), false);
        LancamentoCreateDTO dto = new LancamentoCreateDTO(
            "Salário", LocalDate.of(2025, 6, 10), null, new BigDecimal("5000.00"),
            null, TipoLancamento.RECEITA, 1L, 2L
        );
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(pessoaRepository.findById(2L)).thenReturn(Optional.of(inativa));

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessage("Não é possível salvar lançamento para pessoa inativa.");
    }

    @Test
    @DisplayName("Deve atualizar lançamento a partir de DTO buscando categoria e pessoa")
    void update_WithDTO_ShouldFindDependenciesAndUpdate() {
        LancamentoUpdateDTO dto = new LancamentoUpdateDTO(
            "Novo Cinema", LocalDate.of(2025, 6, 15), null, new BigDecimal("60.00"),
            "Com pipoca", TipoLancamento.DESPESA, 1L, 2L
        );
        when(repository.findById(10L)).thenReturn(Optional.of(lancamento));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(pessoaRepository.findById(2L)).thenReturn(Optional.of(pessoa));
        when(repository.save(any(Lancamento.class))).thenReturn(lancamento);

        Lancamento result = service.update(10L, dto);

        assertThat(result).isNotNull();
        verify(repository).findById(10L);
        verify(categoriaRepository).findById(1L);
        verify(pessoaRepository).findById(2L);
        verify(repository).save(lancamento);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com DTO quando pessoa for inativa")
    void update_WithDTO_WhenPessoaInactive_ShouldThrowRegraDeNegocioException() {
        Pessoa inativa = new Pessoa(2L, "Inativa", pessoa.getEndereco(), false);
        LancamentoUpdateDTO dto = new LancamentoUpdateDTO(
            "Novo Cinema", LocalDate.of(2025, 6, 15), null, new BigDecimal("60.00"),
            "Com pipoca", TipoLancamento.DESPESA, 1L, 2L
        );
        when(repository.findById(10L)).thenReturn(Optional.of(lancamento));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(pessoaRepository.findById(2L)).thenReturn(Optional.of(inativa));

        assertThatThrownBy(() -> service.update(10L, dto))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessage("Não é possível salvar lançamento para pessoa inativa.");
    }
}