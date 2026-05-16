package com.decodex.br.testesIntegração.adapters.out.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.decodex.br.adapters.out.persistence.adapter.LancamentoRepositoryAdapter;
import com.decodex.br.adapters.out.persistence.entity.LancamentoEntity;
import com.decodex.br.adapters.out.persistence.mapper.LancamentoMapper;
import com.decodex.br.adapters.out.persistence.repository.LancamentoRepository;
import com.decodex.br.domain.model.Categoria;
import com.decodex.br.domain.model.Endereco;
import com.decodex.br.domain.model.Lancamento;
import com.decodex.br.domain.model.Pessoa;
import com.decodex.br.domain.model.TipoLancamento;

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

        Endereco endereco = new Endereco(
            "Rua A", "10", null, "Centro", "00000-000", "São Paulo", "SP"
        );
        Pessoa pessoa = new Pessoa(1L, "João Silva", endereco, true);

        domainLancamento = new Lancamento(
            1L,
            "Conta de luz",
            LocalDate.of(2025, 6, 10),
            null,
            new BigDecimal("350.00"),
            "Observação teste",
            TipoLancamento.DESPESA,
            categoria,
            pessoa
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
        assertThat(result.getValor()).isEqualByComparingTo("350.00");
        assertThat(result.getCategoria()).isNotNull();
        assertThat(result.getPessoa()).isNotNull();

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
        assertThat(result.get().getDescricao()).isEqualTo("Conta de luz");
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
    @DisplayName("Deve listar todos os lançamentos")
    void findAll_ShouldReturnAll() {
        when(lancamentoRepository.findAll()).thenReturn(List.of(lancamentoEntity));
        when(lancamentoMapper.toDomain(lancamentoEntity)).thenReturn(domainLancamento);

        List<Lancamento> result = adapter.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescricao()).isEqualTo("Conta de luz");
        assertThat(result.get(0).getCategoria().getNome()).isEqualTo("Alimentação");
        assertThat(result.get(0).getPessoa().getNome()).isEqualTo("João Silva");
        verify(lancamentoRepository).findAll();
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