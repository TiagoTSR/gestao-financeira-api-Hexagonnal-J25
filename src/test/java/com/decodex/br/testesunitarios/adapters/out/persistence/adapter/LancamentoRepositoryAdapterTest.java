package com.decodex.br.testesunitarios.adapters.out.persistence.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.decodex.br.adapters.out.persistence.adapter.LancamentoRepositoryAdapter;
import com.decodex.br.adapters.out.persistence.entity.LancamentoEntity;
import com.decodex.br.adapters.out.persistence.mapper.LancamentoMapper;
import com.decodex.br.adapters.out.persistence.repository.LancamentoRepository;
import com.decodex.br.domain.model.*;
import com.decodex.br.domain.pagination.PageRequest;
import com.decodex.br.domain.pagination.PageResult;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários - LancamentoRepositoryAdapter")
class LancamentoRepositoryAdapterTest {

    @Mock
    private LancamentoRepository lancamentoRepository;

    @Mock
    private LancamentoMapper lancamentoMapper;

    @InjectMocks
    private LancamentoRepositoryAdapter adapter;

    private Lancamento domainLancamento;
    private LancamentoEntity lancamentoEntity;

    @BeforeEach
    void setUp() {
        Categoria categoria = new Categoria(1L, "Alimentação");
        Endereco endereco = new Endereco("Rua A", "10", null, "Centro", "00000-000", "São Paulo", "SP");
        Pessoa pessoa = new Pessoa(1L, "João Silva", endereco, true);

        domainLancamento = new Lancamento(
            1L, "Conta de luz", LocalDate.of(2025, 6, 10), null,
            new BigDecimal("350.00"), "Observação teste", TipoLancamento.DESPESA, categoria, pessoa
        );

        lancamentoEntity = new LancamentoEntity();
        lancamentoEntity.setId(1L);
        lancamentoEntity.setDescricao("Conta de luz");
    }

    @Test
    @DisplayName("Deve salvar e retornar domínio")
    void save_ShouldPersistAndReturnDomain() {
        when(lancamentoMapper.toEntity(domainLancamento)).thenReturn(lancamentoEntity);
        when(lancamentoRepository.save(lancamentoEntity)).thenReturn(lancamentoEntity);
        when(lancamentoMapper.toDomain(lancamentoEntity)).thenReturn(domainLancamento);

        Lancamento result = adapter.save(domainLancamento);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescricao()).isEqualTo("Conta de luz");
        verify(lancamentoMapper).toEntity(domainLancamento);
        verify(lancamentoRepository).save(lancamentoEntity);
        verify(lancamentoMapper).toDomain(lancamentoEntity);
    }

    @Test
    @DisplayName("Deve retornar domínio ao buscar por ID existente")
    void findById_WhenExists_ShouldReturnDomain() {
        when(lancamentoRepository.findById(1L)).thenReturn(Optional.of(lancamentoEntity));
        when(lancamentoMapper.toDomain(lancamentoEntity)).thenReturn(domainLancamento);

        Optional<Lancamento> result = adapter.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        verify(lancamentoRepository).findById(1L);
        verify(lancamentoMapper).toDomain(lancamentoEntity);
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar por ID inexistente")
    void findById_WhenNotExists_ShouldReturnEmpty() {
        when(lancamentoRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Lancamento> result = adapter.findById(999L);

        assertThat(result).isEmpty();
        verify(lancamentoRepository).findById(999L);
        verifyNoInteractions(lancamentoMapper);
    }

    @Test
    @DisplayName("Deve listar todos os lançamentos com paginação")
    void findAll_ShouldReturnPageResult() {
        PageRequest pageRequest = new PageRequest(0, 10);
        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        Page<LancamentoEntity> page = new PageImpl<>(List.of(lancamentoEntity), pageable, 1);

        when(lancamentoRepository.findAll(pageable)).thenReturn(page);
        when(lancamentoMapper.toDomain(lancamentoEntity)).thenReturn(domainLancamento);

        PageResult<Lancamento> result = adapter.findAll(pageRequest);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).getDescricao()).isEqualTo("Conta de luz");
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.totalElements()).isEqualTo(1L);
        assertThat(result.totalPages()).isEqualTo(1);
        verify(lancamentoRepository).findAll(pageable);
        verify(lancamentoMapper).toDomain(lancamentoEntity);
    }

    @Test
    @DisplayName("Deve deletar lançamento por ID")
    void deleteById_ShouldCallRepository() {
        doNothing().when(lancamentoRepository).deleteById(1L);

        adapter.deleteById(1L);

        verify(lancamentoRepository).deleteById(1L);
        verifyNoMoreInteractions(lancamentoRepository);
    }
}